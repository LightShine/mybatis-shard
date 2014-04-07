/**
 * 
 */
package org.lysu.shard.converter;

import java.util.regex.Pattern;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

/**
 * 
 * 
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public abstract class AbstractSqlConverter implements SqlConverter {

    public String convert(Statement statement, String tableSuffix, String includeTable) {
        Pattern includePattern = Pattern.compile(includeTable);
        return doDeParse(doConvert(statement, tableSuffix, includePattern));
    }

    /**
     * 将Statement反解析为sql
     * 
     * @param statement
     * @return
     */
    protected String doDeParse(Statement statement) {
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        statement.accept(deParser);
        return deParser.getBuffer().toString();
    }

    /**
     * 从ShardConfigFactory中查找ShardStrategy并对表名进行修改<br>
     * 如果没有相应的ShardStrategy则对表名不做修改
     * 
     * @param tableName
     * @param suffix
     * @return
     */
    protected String convertTableName(String tableName, String suffix) {
        return tableName + "_" + suffix;
    }

    /**
     * 修改statement代表的sql语句
     * 
     * @param statement
     * @param tableSuffix
     * @param includePattern
     * @return
     */
    protected abstract Statement doConvert(Statement statement, String tableSuffix, Pattern includePattern);

}
