package org.lysu.shard.example.mapper;

import org.lysu.shard.annotation.DbShard;
import org.lysu.shard.annotation.DbShardWith;
import org.lysu.shard.annotation.TableShard;
import org.lysu.shard.annotation.TableShardWith;
import org.lysu.shard.example.model.Test;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lysu created on 14-4-6 下午4:32
 * @version $Id$
 */
@DbShard(dbKey = "ds", rule = "$a$ % 2")
@TableShard(tablePattern = "test", rule = "leftPad($a$ % 4, 3)")
@Repository
public interface TestMapper {

    public void save(@DbShardWith(props = "a") @TableShardWith(props = "a")Test test);

    public List<Test> query(@DbShardWith(props = "a") @TableShardWith(props = "a")Map<String, Object> param);

    public void update(@DbShardWith(props = "a") @TableShardWith(props = "a") Map<String, Object> param);

}
