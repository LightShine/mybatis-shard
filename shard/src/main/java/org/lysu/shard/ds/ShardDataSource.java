package org.lysu.shard.ds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 支持选择具体数据源的数据源,基于spring的routing data source.
 *
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public class ShardDataSource extends AbstractRoutingDataSource {

    /**
     * 获取当前要使用的数据源key.
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingSetting.getRoutingInfo();
    }

}
