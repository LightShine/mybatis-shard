package org.lysu.shard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表区分策略注解
 *
 * @author lysu created on 14-4-6 下午3:38
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableShard {

    /**
     * 参与分表区分的表明表达式.
     *
     * @return
     */
    String tablePattern();

    /**
     * 选择表的规则.
     *
     * @return
     */
    String rule();

}