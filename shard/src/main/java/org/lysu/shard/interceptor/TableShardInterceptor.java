package org.lysu.shard.interceptor;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.lysu.shard.annotation.TableShard;
import org.lysu.shard.annotation.TableShardWith;
import org.lysu.shard.converter.SqlConverterFactory;
import org.lysu.shard.locator.Locator;
import org.lysu.shard.locator.Locators;
import org.lysu.shard.mapperinfo.TableParameter;
import org.lysu.shard.tools.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author lysu created on 14-4-6 下午4:01
 * @version $Id$
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class TableShardInterceptor implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        BoundSql boundSql = statementHandler.getBoundSql();

        String sql = tryConvertSql(statementHandler, boundSql);

        if (StringUtils.isNotEmpty(sql)) {
            Reflections.setFieldValue(boundSql, "sql", sql);
        }

        return invocation.proceed();

    }

    @VisibleForTesting
    String tryConvertSql(StatementHandler statementHandler, BoundSql boundSql) {

        MappedStatement mappedStatement = mappedStatement(statementHandler);

        Class<?> mapperClazz = mapperClazz(mappedStatement);
        if (mapperClazz == null) {
            return null;
        }

        TableShard tableShardConfig = mapperClazz.getAnnotation(TableShard.class);
        if (tableShardConfig == null) {
            return null;
        }

        Method mapperMethod = shardMapperMethod(mappedStatement, mapperClazz);
        if (mapperMethod == null || ArrayUtils.isEmpty(mapperMethod.getParameterTypes())) {
            return null;
        }

        List<TableParameter> shardParams = shardParams(mapperMethod);
        if (CollectionUtils.isEmpty(shardParams)) {
            return null;
        }

        List<Object> shardValues = sharedValue(shardParams, boundSql);

        Locator locator = Locators.instance.takeLocator(checkNotNull(tableShardConfig.rule()));
        String targetSuffix = locator.locate(shardValues);

        SqlConverterFactory converterFactory = SqlConverterFactory.getInstance();
        return converterFactory.convert(boundSql.getSql(), targetSuffix, checkNotNull(tableShardConfig.tablePattern()));

    }

    private Class<?> mapperClazz(MappedStatement mappedStatement) {
        String mapperId = mappedStatement.getId();

        String _mapperClazz = StringUtils.substringBeforeLast(mapperId, ".");

        Class<?> mapperClazz = null;
        try {
            mapperClazz = Class.forName(_mapperClazz);
        } catch (ClassNotFoundException e) {
        }
        return mapperClazz;
    }

    private Method shardMapperMethod(MappedStatement mappedStatement, Class<?> mapperClazz) {
        String _mapperMethod = StringUtils.substringAfterLast(mappedStatement.getId(), ".");

        Method mapperMethod = null;
        for (Method method : mapperClazz.getMethods()) {
            if (Objects.equal(method.getName(), _mapperMethod)) {
                mapperMethod = method;
            }
        }
        return mapperMethod;
    }

    private List<TableParameter> shardParams(Method mapperMethod) {

        List<TableParameter> params = Lists.newArrayList();

        for (Annotation[] annotations : mapperMethod.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {

                TableShardWith tableShardWith = null;
                if (annotation instanceof TableShardWith) {
                    tableShardWith = (TableShardWith) annotation;
                }

                Param param = null;
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                }

                if (tableShardWith != null) {
                    TableParameter tableParameter = new TableParameter();
                    tableParameter.setParam(param);
                    tableParameter.setTableShardWith(tableShardWith);
                    params.add(tableParameter);
                }

            }
        }

        return params;
    }

    private List<Object> sharedValue(List<TableParameter> shardParams, BoundSql boundSql) {

        List<Object> sharedValues = Lists.newArrayList();

        for (TableParameter paramInfo : shardParams) {

            Map parameterObject = (Map) boundSql.getParameterObject();

            TableShardWith tableShardWith = paramInfo.getTableShardWith();

            // 原生参数的处理方法.
            if (ArrayUtils.isEmpty(tableShardWith.props())) {
                Param batisParam = paramInfo.getParam();
                // this is dirt and wait to improve...
                checkNotNull(batisParam, "@TableShardWith标注的基本基本数据类型参数必须有@Param注解命名");
                String name = batisParam.value();
                checkArgument(StringUtils.isNotEmpty(name));
                Object value = parameterObject.get(name);
                checkArgument(value.getClass().isPrimitive(), "没有prop属性的@TableShardWith注解只能针对基本类型");
                sharedValues.add(value);
                continue;
            }

            // 非原生带内嵌参数的处理.
            for (String prop : tableShardWith.props()) {
                sharedValues.add(checkNotNull(parameterObject.get(prop)));
            }

        }

        return sharedValues;
    }

    private MappedStatement mappedStatement(StatementHandler statementHandler) {
        if (statementHandler instanceof RoutingStatementHandler) {
            StatementHandler delegate = (StatementHandler) Reflections.getFieldValue(statementHandler, "delegate");
            return (MappedStatement) Reflections.getFieldValue(delegate, "mappedStatement");
        }
        return (MappedStatement) Reflections.getFieldValue(statementHandler, "mappedStatement");
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {

    }

}
