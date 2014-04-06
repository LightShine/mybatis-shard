package org.lysu.shard.context;

import org.lysu.shard.execute.ExecuteInfo;

/**
 * @author lysu created on 14-4-7 上午1:49
 * @version $Id$
 */
public class ExecuteInfoContext {

    private static final ThreadLocal<ExecuteInfo> contextHolder = new ThreadLocal<ExecuteInfo>();

    public static void setExecuteInfo(ExecuteInfo executeInfo) {
        contextHolder.set(executeInfo);
    }

    public static ExecuteInfo getExecuteInfo() {
        return (ExecuteInfo) contextHolder.get();
    }

    public static void clearExecuteInfo() {
        contextHolder.remove();
    }

}
