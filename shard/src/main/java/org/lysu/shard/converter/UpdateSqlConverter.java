/**
 * 
 */
package org.lysu.shard.converter;

import java.util.regex.Pattern;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

/**
 * 
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public class UpdateSqlConverter extends AbstractSqlConverter {

    @Override
    protected Statement doConvert(Statement statement, String suffix, Pattern includePattern) {
        if (!(statement instanceof Update)) {
            throw new IllegalArgumentException("The argument statement must is instance of Update.");
        }
        Update update = (Update) statement;
        String name = update.getTable().getName();

        if (includePattern.matcher(name).find()) {
            update.getTable().setName(this.convertTableName(name, suffix));
        }
        return update;
    }

}
