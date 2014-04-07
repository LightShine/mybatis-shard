package org.lysu.shard.execution;

import org.lysu.shard.config.ExecutionConfig;
import org.lysu.shard.execution.handler.GenerateSqlHandler;
import org.lysu.shard.execution.handler.RuleParseHandler;
import org.lysu.shard.execution.handler.SqlParseHandler;
import org.lysu.shard.execution.pipeline.DefaultPipeline;
import org.lysu.shard.execution.pipeline.Pipeline;

/**
 * @author lysu created on 14-4-7 下午11:05
 * @version $Id$
 */
public class PipelineFactory {

    private Pipeline pipeline = new DefaultPipeline();

    {
        pipeline.addLast("sqlParse", new SqlParseHandler());
        pipeline.addLast("ruleParse", new RuleParseHandler());
        pipeline.addLast("generateSql", new GenerateSqlHandler());
    }

    public Pipeline pipeline(ExecutionConfig config) {
        return pipeline;
    }

}
