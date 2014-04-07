package org.lysu.shard.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.lysu.shard.context.ExecuteInfoContext;
import org.lysu.shard.config.ExecutionConfig;
import org.lysu.shard.config.ExecutionConfigBuilder;

import java.util.Properties;

/**
 * @author lysu created on 14-4-7 上午1:45
 * @version $Id$
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class }) })
public class PlanExecuteInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();

        if (args.length < 2) {
            return invocation.proceed();
        }

        Object paramValue = args[0];
        if (!(paramValue instanceof MappedStatement)) {
            return invocation.proceed();
        }

        ExecutionConfig executeInfo = ExecutionConfigBuilder.instance.build((MappedStatement) args[0], args[1]);

        if (executeInfo == null) {
            return invocation.proceed();
        }

        try {
            ExecuteInfoContext.setExecuteInfo(executeInfo);
            // 因为对同一节点上的Interceptor执行次序表示怀疑...这里还是自己执行dbInterceptor.
            return DbShardInterceptor.instance.intercept(invocation);
        } finally {
            ExecuteInfoContext.clearExecuteInfo();
        }

    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
