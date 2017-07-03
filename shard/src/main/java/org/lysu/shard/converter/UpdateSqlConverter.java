/**
 * 
 */
package org.lysu.shard.converter;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

import java.util.List;
import java.util.regex.Pattern;

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
        List<Table> tables = update.getTables();
        for (Table table : tables) {
            if (includePattern.matcher(table.getName()).find()) {
                table.setName(this.convertTableName(table.getName(), suffix));
            }
        }
        return update;
    }

}
