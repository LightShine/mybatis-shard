package org.lysu.shard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源区分策略注解.
 *
 * @author lysu created on 14-4-6 下午3:27
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DbShard {

    /**
     * 用于选择数据源的key.
     *
     * @return
     */
    String dbKey();

    /**
     * 选择数据源的规则.
     *
     * @return
     */
    String rule();

}
