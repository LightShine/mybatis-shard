package org.lysu.shard.mapperinfo;

import org.apache.ibatis.annotations.Param;
import org.lysu.shard.annotation.DbShardWith;

/**
 * @author lysu created on 14-4-6 下午3:55
 * @version $Id$
 */
public class DbParameter {

    private Param param;

    private DbShardWith dbShardWith;

    public org.apache.ibatis.annotations.Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public DbShardWith getDbShardWith() {
        return dbShardWith;
    }

    public void setDbShardWith(DbShardWith dbShardWith) {
        this.dbShardWith = dbShardWith;
    }
}
