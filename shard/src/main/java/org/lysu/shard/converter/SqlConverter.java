/**
 * 
 */
package org.lysu.shard.converter;

import net.sf.jsqlparser.statement.Statement;

/**
 * sql转换修改接口.
 * 
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public interface SqlConverter {
    /**
     * 对sql进行修改
     * 
     * @param statement
     * @param suffix
     * @param includePattern
     * @return
     */
    String convert(Statement statement, String suffix, String includePattern);
}
