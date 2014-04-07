package org.lysu.shard.interceptor;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.apache.ibatis.plugin.Invocation;
import org.lysu.shard.config.DataSourceConfig;
import org.lysu.shard.config.ExecutionConfig;
import org.lysu.shard.context.ExecuteInfoContext;
import org.lysu.shard.context.RouteDataSourceContext;
import org.lysu.shard.locator.Locator;
import org.lysu.shard.locator.Locators;

/**
 * @author lysu created on 14-4-6 下午3:57
 * @version $Id$
 */
enum DbShardInterceptor {

    instance;

    public Object intercept(Invocation invocation) throws Throwable {

        String routingKey = routeKey();

        if (isNullOrEmpty(routingKey)) {
            return invocation.proceed();
        }

        try {
            RouteDataSourceContext.setRouteKey(routingKey);
            return invocation.proceed();
        } finally {
            RouteDataSourceContext.clearRouteKey();
        }

    }

    private String routeKey() {

        ExecutionConfig executeInfo = ExecuteInfoContext.getExecuteInfo();

        if (executeInfo == null) {
            return null;
        }

        DataSourceConfig dataSourceConfig = executeInfo.getDataSourceConfig();

        if (dataSourceConfig == null) {
            return null;
        }

        Locator locator = Locators.instance.takeLocator(dataSourceConfig.getRule());
        String dbSuffix = locator.locate(dataSourceConfig.getParams());

        if (isNullOrEmpty(dbSuffix)) {
            return null;
        }

        return dataSourceConfig.getDataSourceName() + "_" + dbSuffix;

    }

}
