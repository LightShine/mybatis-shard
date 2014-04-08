/**
 * 
 */
package org.lysu.shard.converter;

import com.google.common.base.Throwables;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.lysu.shard.parser.ast.expression.primary.Identifier;
import org.lysu.shard.parser.ast.fragment.tableref.TableRefFactor;
import org.lysu.shard.parser.ast.fragment.tableref.TableReference;
import org.lysu.shard.parser.ast.fragment.tableref.TableReferences;
import org.lysu.shard.parser.visitor.EmptySQLASTVisitor;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public class SqlConverterFactory {
    private static final Log log = LogFactory.getLog(SqlConverterFactory.class);

    private static SqlConverterFactory factory;
    static {
        factory = new SqlConverterFactory();
    }
    private Map<String, SqlConverter> converterMap = new HashMap<String, SqlConverter>();
    private CCJSqlParserManager pm;

    private SqlConverterFactory() {

        pm = new CCJSqlParserManager();
        register();
    }

    public static SqlConverterFactory getInstance() {
        return factory;
    }

    private void register() {
        converterMap.put(Select.class.getName(), new SelectSqlConverter());
        converterMap.put(Insert.class.getName(), new InsertSqlConverter());
        converterMap.put(Update.class.getName(), new UpdateSqlConverter());
        converterMap.put(Delete.class.getName(), new DeleteSqlConverter());
    }

    /**
     * 修改sql语句
     * 
     * @param sql
     * @param suffix
     * @return 修改后的sql
     */
    public String convert(String sql, String suffix, String includePattern) {

        Statement statement = null;
        try {
            statement = pm.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            log.error(e.getMessage(), e);
            Throwables.propagate(e);
        }

        SqlConverter converter = this.converterMap.get(statement.getClass().getName());

        if (converter != null) {
            return converter.convert(statement, suffix, includePattern);
        }
        return sql;
        
//        try {
//
//            SQLStatement statement = SQLParserDelegate.parse(sql);
//
//            statement.accept(new TableReplaceVisitor(suffix, includePattern));
//
//            MySQLOutputASTVisitor sqlGen = new MySQLOutputASTVisitor(new StringBuilder());
//            statement.accept(sqlGen);
//
//            return sqlGen.getSql();
//
//        } catch (SQLSyntaxErrorException e) {
//            Throwables.propagate(e);
//        }
//
//        return sql;
    }
}

class TableReplaceVisitor extends EmptySQLASTVisitor {

    private String tableSuffix;

    private String includePattern;

    TableReplaceVisitor() {
    }

    TableReplaceVisitor(String tableSuffix, String includePattern) {
        this.tableSuffix = tableSuffix;
        this.includePattern = includePattern;
    }

    @Override
    public void visit(TableReferences node) {

        List<TableReference> tableReferenceList = node.getTableReferenceList();

        for (TableReference tableReference : tableReferenceList) {
            tableReference.accept(this);
        }

    }

    @Override
    public void visit(TableRefFactor node) {
        Identifier table = node.getTable();
        String tableName = table.getIdTextUpUnescape();
        String alias = node.getAliasUnescapeUppercase();

//        if (alias == null) {
//            tableAlias.put(null, tableName);
//            tableAlias.put(tableName, tableName);
//        } else {
//            if (!tableAlias.containsKey(null)) {
//                tableAlias.put(null, tableName);
//            }
//            tableAlias.put(alias, tableName);
//        }
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    public String getIncludePattern() {
        return includePattern;
    }

    public void setIncludePattern(String includePattern) {
        this.includePattern = includePattern;
    }

}
