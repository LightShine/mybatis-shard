package org.lysu.shard.execute;

/**
 * @author lysu created on 14-4-7 上午1:30
 * @version $Id$
 */
public class ExecuteInfo {

    private DataSourceInfo dataSourceInfo;

    private TableInfo tableInfo;

    public ExecuteInfo() {
    }

    public ExecuteInfo(DataSourceInfo dataSourceInfo, TableInfo tableInfo) {
        this.dataSourceInfo = dataSourceInfo;
        this.tableInfo = tableInfo;
    }

    public DataSourceInfo getDataSourceInfo() {
        return dataSourceInfo;
    }

    public void setDataSourceInfo(DataSourceInfo dataSourceInfo) {
        this.dataSourceInfo = dataSourceInfo;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }
}

