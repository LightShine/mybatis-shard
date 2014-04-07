package org.lysu.shard.execution;

import org.lysu.shard.config.ExecutionConfig;
import org.lysu.shard.execution.pipeline.Pipeline;
import org.lysu.shard.execution.pipeline.PipelineEvent;

/**
 * @author lysu created on 14-4-8 上午12:21
 * @version $Id$
 */
public class PipelineBootstrap {

    private PipelineFactory pipelineFactory;

    public PipelineBootstrap(PipelineFactory pipelineFactory) {
        this.pipelineFactory = pipelineFactory;
    }

    public Object bootstrap(ExecutionConfig config) {
        PipelineEvent event = new PipelineEvent();

        Pipeline pipeline = pipelineFactory.pipeline(config);
        pipeline.doUpstream(event);

        return asExecutePlan(event);

    }

    private Object asExecutePlan(PipelineEvent event) {
        return null;
    }

}
