package org.lysu.shard.interceptor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.lysu.shard.config.ExecutionConfig;
import org.lysu.shard.config.TableConfig;
import org.lysu.shard.context.ExecuteInfoContext;
import org.lysu.shard.converter.SqlConverterFactory;
import org.lysu.shard.locator.Locator;
import org.lysu.shard.locator.Locators;
import org.lysu.shard.tools.Reflections;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author lysu created on 14-4-6 下午4:01
 * @version $Id$
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class TableShardInterceptor implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        BoundSql boundSql = statementHandler.getBoundSql();

        String sql = tryConvertSql(boundSql);

        if (StringUtils.isNotEmpty(sql)) {
            Reflections.setFieldValue(boundSql, "sql", sql);
        }

        return invocation.proceed();

    }

    @VisibleForTesting
    String tryConvertSql(BoundSql boundSql) {

        ExecutionConfig executeInfo = ExecuteInfoContext.getExecuteInfo();

        if (executeInfo == null) {
            return null;
        }

        TableConfig tableConfig = executeInfo.getTableConfig();

        if (tableConfig == null) {
            return null;
        }

        Locator locator = Locators.instance.takeLocator(checkNotNull(tableConfig.getRule()));
        String targetSuffix = locator.locate(tableConfig.getParams());

        SqlConverterFactory converterFactory = SqlConverterFactory.getInstance();
        return converterFactory.convert(boundSql.getSql(), targetSuffix, checkNotNull(tableConfig.getTablePattern()));

    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {

    }

}
