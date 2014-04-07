package org.lysu.shard.context;

import org.lysu.shard.config.ExecutionConfig;

/**
 * @author lysu created on 14-4-7 上午1:49
 * @version $Id$
 */
public class ExecuteInfoContext {

    private static final ThreadLocal<ExecutionConfig> contextHolder = new ThreadLocal<ExecutionConfig>();

    public static ExecutionConfig getExecuteInfo() {
        return (ExecutionConfig) contextHolder.get();
    }

    public static void setExecuteInfo(ExecutionConfig executeInfo) {
        contextHolder.set(executeInfo);
    }

    public static void clearExecuteInfo() {
        contextHolder.remove();
    }

}
