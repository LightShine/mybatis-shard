package org.lysu.shard.interceptor;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.lysu.shard.annotation.DbShard;
import org.lysu.shard.annotation.DbShardWith;
import org.lysu.shard.ds.RoutingSetting;
import org.lysu.shard.locator.Locator;
import org.lysu.shard.locator.Locators;
import org.lysu.shard.mapperinfo.DbParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author lysu created on 14-4-6 下午3:57
 * @version $Id$
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class }) })
public class DbShardInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();

        if (args.length != 2) {
            return invocation.proceed();
        }

        Object paramValue = args[0];
        if (!(paramValue instanceof MappedStatement)) {
            return invocation.proceed();
        }

        MappedStatement statement = (MappedStatement) args[0];

        if (!(args[1] instanceof Map)) {
            return invocation.proceed();
        }

        Map parameterObject = (Map) args[1];

        String command = statement.getId();

        String _mapperName = StringUtils.substringBeforeLast(command, ".");
        String _methodName = StringUtils.substringAfterLast(command, ".");

        Class<?> mapperClazz = null;
        try {
            mapperClazz = Class.forName(_mapperName);
        } catch (ClassNotFoundException e) {
        }

        if (mapperClazz == null) {
            return invocation.proceed();
        }

        DbShard dbShardConfig = mapperClazz.getAnnotation(DbShard.class);

        Method methodClazz = null;
        for (Method method : mapperClazz.getMethods()) {
            if (Objects.equal(method.getName(), _methodName)) {
                methodClazz = method;
            }
        }

        if (methodClazz == null) {
            return invocation.proceed();
        }

        List<DbParameter> dbParameterList = Lists.newArrayList();

        for (Annotation[] annotations : methodClazz.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {

                DbShardWith dbShardWith = null;
                if (annotation instanceof DbShardWith) {
                    dbShardWith = (DbShardWith) annotation;
                }

                Param param = null;
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                }

                if (dbShardWith != null) {
                    DbParameter dbParameter = new DbParameter();
                    dbParameter.setParam(param);
                    dbParameter.setDbShardWith(dbShardWith);
                    dbParameterList.add(dbParameter);
                }

            }
        }

        List<Object> sharedValues = Lists.newArrayList();

        for (DbParameter paramInfo : dbParameterList) {

            DbShardWith dbShardWith = paramInfo.getDbShardWith();

            // 原生参数的处理方法.
            if (ArrayUtils.isEmpty(dbShardWith.props())) {
                Param batisParam = paramInfo.getParam();
                // this is dirt and wait to improve...
                checkNotNull(batisParam, "@TableShardWith标注的基本基本数据类型参数必须有@Param注解命名");
                Object value = parameterObject.get(batisParam.value());
                checkArgument(value.getClass().isPrimitive(), "没有prop属性的@TableShardWith注解只能针对基本类型");
                sharedValues.add(value);
                continue;
            }

            // 非原生带内嵌参数的处理.
            for (String prop : dbShardWith.props()) {
                sharedValues.add(checkNotNull(parameterObject.get(prop)));
            }

        }

        Locator locator = Locators.instance.takeLocator(checkNotNull(dbShardConfig.rule()));
        String dbSuffix = locator.locate(sharedValues);

        String routingKey = dbShardConfig.dbKey() + "_" + dbSuffix;
        try {
            RoutingSetting.setRoutingInfo(routingKey);
            return invocation.proceed();
        } finally {
            RoutingSetting.clearRoutingInfo();
        }

    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
