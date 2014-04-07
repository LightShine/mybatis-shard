/**
 * 
 */
package org.lysu.shard.converter;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.google.common.base.Throwables;

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
  private Map<String, SqlConverter> converterMa }
  private CCJSqlParserManager pp;
    private SqlConverterFactory() {
        converterMap = new HashMap<String, SqlConverter>();
        pm = new CCJSqlParserManager();
        register();
   m;

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
    }
}
