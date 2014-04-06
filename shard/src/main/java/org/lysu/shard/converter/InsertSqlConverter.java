/**
 * 
 */
package org.lysu.shard.converter;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;

import java.util.regex.Pattern;

/**
 *
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public class InsertSqlConverter extends AbstractSqlConverter {

    @Override
    protected Statement doConvert(Statement statement, String suffix, Pattern includePattern) {
        if (!(statement instanceof Insert)) {
            throw new IllegalArgumentException("The argument statement must is instance of Insert.");
        }
        Insert insert = (Insert) statement;

        String name = insert.getTable().getName();

        if (includePattern.matcher(name).find()) {
            insert.getTable().setName(this.convertTableName(name, suffix));
        }

        return insert;
    }

}
