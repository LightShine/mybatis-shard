/**
 * 
 */
package org.lysu.shard.converter;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.regex.Pattern;

/**
 *
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public class SelectSqlConverter extends AbstractSqlConverter {

    @Override
    protected Statement doConvert(Statement statement, String tableSuffix, Pattern includePattern) {

        if (!(statement instanceof Select)) {
            throw new IllegalArgumentException("The argument statement must is instance of Select.");
        }
        TableNameModifier modifier = new TableNameModifier(tableSuffix, includePattern);
        ((Select) statement).getSelectBody().accept(modifier);
        return statement;
    }

    private class TableNameModifier implements SelectVisitor, FromItemVisitor {

        private String suffix;
        private Pattern includePattern;

        private TableNameModifier(String suffix, Pattern includePattern) {
            this.suffix = suffix;
            this.includePattern = includePattern;
        }

        @Override
        public void visit(PlainSelect plainSelect) {
            plainSelect.getFromItem().accept(this);
        }

        @Override
        public void visit(SetOperationList setOpList) {

        }

        @Override
        public void visit(WithItem withItem) {

        }

        @Override
        public void visit(Table tableName) {
            String tableNameName = tableName.getName();

            if (includePattern.matcher(tableNameName).find()) {

                String newName = convertTableName(tableNameName, suffix);
                tableName.setName(newName);
            }
        }

        @Override
        public void visit(SubSelect subSelect) {

        }

        @Override
        public void visit(SubJoin subjoin) {

        }

        @Override
        public void visit(LateralSubSelect lateralSubSelect) {

        }

        @Override
        public void visit(ValuesList valuesList) {

        }
    }

}
