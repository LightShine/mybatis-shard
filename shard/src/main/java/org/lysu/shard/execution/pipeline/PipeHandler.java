package org.lysu.shard.execution.pipeline;

/**
 * @author lysu created on 14-4-7 下午11:08
 * @version $Id$
 */
public interface PipeHandler {

    void handle(PipeContext context, PipelineEvent pipelineEvent);

}
