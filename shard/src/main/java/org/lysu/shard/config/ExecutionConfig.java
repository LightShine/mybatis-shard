package org.lysu.shard.config;

/**
 * @author lysu created on 14-4-7 上午1:30
 * @version $Id$
 */
public class ExecutionConfig {

    private DataSourceConfig dataSourceConfig;

    private TableConfig tableConfig;

    public ExecutionConfig() {
    }

    public ExecutionConfig(DataSourceConfig dataSourceConfig, TableConfig tableConfig) {
        this.dataSourceConfig = dataSourceConfig;
        this.tableConfig = tableConfig;
    }

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public void setDataSourceConfig(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public TableConfig getTableConfig() {
        return tableConfig;
    }

    public void setTableConfig(TableConfig tableConfig) {
        this.tableConfig = tableConfig;
    }
}
