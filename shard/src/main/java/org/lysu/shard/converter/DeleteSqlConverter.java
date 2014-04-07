/**
 * 
 */
package org.lysu.shard.converter;

import java.util.regex.Pattern;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;

/**
 * 
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public class DeleteSqlConverter extends AbstractSqlConverter {

    @Override
    protected Statement doConvert(Statement statement, String tableSuffix, Pattern includePattern) {
        if (!(statement instanceof Delete)) {
            throw new IllegalArgumentException("The argument statement must is instance of Delete.");
        }
        Delete delete = (Delete) statement;

        String name = delete.getTable().getName();

        if (includePattern.matcher(name).find()) {
            delete.getTable().setName(this.convertTableName(name, tableSuffix));
        }

        return delete;
    }

}
