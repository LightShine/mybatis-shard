package org.lysu.shard.interceptor;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author lysu created on 14-4-6 下午4:01
 * @version $Id$
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class TableShardInterceptor extends AbstractShardInterceptor {

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

        Map<String, Object> shardValues = sharedValue(shardParams, boundSql);

        if (MapUtils.isEmpty(shardValues)) {
            return null;
        }

        Locator locator = Locators.instance.takeLocator(checkNotNull(tableShardConfig.rule()));
        String targetSuffix = locator.locate(shardValues);

        SqlConverterFactory converterFactory = SqlConverterFactory.getInstance();
        return converterFactory.convert(boundSql.getSql(), targetSuffix, checkNotNull(tableShardConfig.tablePattern()));

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

    private Map<String, Object> sharedValue(List<TableParameter> shardParams, BoundSql boundSql) {

        Map<String, Object> sharedValues = Maps.newHashMap();

        for (TableParameter paramInfo : shardParams) {

            Object parameterObject = boundSql.getParameterObject();

            TableShardWith tableShardWith = paramInfo.getTableShardWith();

            // 原生参数的处理方法.
            if (ArrayUtils.isEmpty(tableShardWith.props())) {
                sharedValues.put(paramInfo.getParam().value(),
                        takeRawParameterValue(parameterObject, paramInfo.getParam()));
                continue;
            }

            // 非原生带内嵌参数的处理.
            for (String prop : tableShardWith.props()) {
                sharedValues.put(prop, checkNotNull(Reflections.getPropertyValue(parameterObject, prop)));
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
