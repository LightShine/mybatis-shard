package org.lysu.shard.interceptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.lysu.shard.annotation.DbShard;
import org.lysu.shard.annotation.DbShardWith;
import org.lysu.shard.ds.RoutingSetting;
import org.lysu.shard.locator.Locator;
import org.lysu.shard.locator.Locators;
import org.lysu.shard.mapperinfo.DbParameter;
import org.lysu.shard.tools.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author lysu created on 14-4-6 下午3:57
 * @version $Id$
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class }) })
public class DbShardInterceptor extends AbstractShardInterceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();

        if (args.length < 2) {
            return invocation.proceed();
        }

        Object paramValue = args[0];
        if (!(paramValue instanceof MappedStatement)) {
            return invocation.proceed();
        }

        String routingKey = routeKey(args);

        if (isNullOrEmpty(routingKey)) {
            return invocation.proceed();
        }

        try {
            RoutingSetting.setRoutingInfo(routingKey);
            return invocation.proceed();
        } finally {
            RoutingSetting.clearRoutingInfo();
        }

    }

    private String routeKey(Object[] args) {
        MappedStatement mappedStatement = (MappedStatement) args[0];

        Class<?> mapperClazz = mapperClazz(mappedStatement);
        if (mapperClazz == null) {
            return null;
        }

        DbShard dbShardConfig = mapperClazz.getAnnotation(DbShard.class);
        if (dbShardConfig == null) {
            return null;
        }

        Method mapperMethod = shardMapperMethod(mappedStatement, mapperClazz);
        if (mapperMethod == null || ArrayUtils.isEmpty(mapperMethod.getParameterTypes())) {
            return null;
        }

        List<DbParameter> dbParameterList = shardParams(mapperMethod);
        if (CollectionUtils.isEmpty(dbParameterList)) {
            return null;
        }

        Map<String, Object> sharedValues = shareValue(args[1], dbParameterList);

        if (MapUtils.isEmpty(sharedValues)) {
            return null;
        }

        Locator locator = Locators.instance.takeLocator(checkNotNull(dbShardConfig.rule()));
        String dbSuffix = locator.locate(sharedValues);

        if (isNullOrEmpty(dbSuffix)) {
            return null;
        }

        return dbShardConfig.dbKey() + "_" + dbSuffix;

    }

    private Map<String, Object> shareValue(Object parameterObject, List<DbParameter> dbParameterList) {
        Map<String, Object> sharedValues = Maps.newHashMap();

        for (DbParameter paramInfo : dbParameterList) {

            DbShardWith dbShardWith = paramInfo.getDbShardWith();

            // 原生参数的处理方法.
            if (ArrayUtils.isEmpty(dbShardWith.props())) {
                sharedValues.put(paramInfo.getParam().value(),
                        takeRawParameterValue(parameterObject, paramInfo.getParam()));
                continue;
            }

            // 非原生带内嵌参数的处理.
            for (String prop : dbShardWith.props()) {
                sharedValues.put(prop, checkNotNull(Reflections.getPropertyValue(parameterObject, prop)));
            }

        }
        return sharedValues;
    }

    private List<DbParameter> shardParams(Method mapperMethod) {
        List<DbParameter> dbParameterList = Lists.newArrayList();

        for (Annotation[] annotations : mapperMethod.getParameterAnnotations()) {
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
        return dbParameterList;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
