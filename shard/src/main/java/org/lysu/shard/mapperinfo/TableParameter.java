package org.lysu.shard.mapperinfo;

import org.apache.ibatis.annotations.Param;
import org.lysu.shard.annotation.TableShardWith;

/**
 * @author lysu created on 14-4-6 下午3:55
 * @version $Id$
 */
public class TableParameter {

    private Param param;

    private TableShardWith tableShardWith;

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public TableShardWith getTableShardWith() {
        return tableShardWith;
    }

    public void setTableShardWith(TableShardWith tableShardWith) {
        this.tableShardWith = tableShardWith;
    }

}
