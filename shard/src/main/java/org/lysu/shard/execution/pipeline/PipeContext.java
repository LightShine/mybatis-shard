package org.lysu.shard.execution.pipeline;

/**
 * @author lysu created on 14-4-7 下午11:21
 * @version $Id$
 */
public interface PipeContext {

    public String getName();

    public PipeHandler getHandler();

    public void doUpstream(PipelineEvent event);

}
