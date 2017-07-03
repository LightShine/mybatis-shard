/**
 * 
 */
package org.lysu.shard.converter;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;

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

        @Override
        public void visit(TableFunction tableFunction) {

        }
    }

}
