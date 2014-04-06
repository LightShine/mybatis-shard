package org.lysu.shard.execute;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.MappedStatement;
import org.lysu.shard.annotation.DbShard;
import org.lysu.shard.annotation.DbShardWith;
import org.lysu.shard.annotation.TableShard;
import org.lysu.shard.annotation.TableShardWith;
import org.lysu.shard.tools.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author lysu created on 14-4-7 上午2:30
 * @version $Id$
 */
public enum ExecuteInfoBuilder {

    instance;

    public ExecuteInfo build(MappedStatement mappedStatement, Object executeParam) {

        Class<?> mapperClazz = mapperClazz(mappedStatement);
        if (mapperClazz == null) {
            return null;
        }

        DbShard dbAnnotation = mapperClazz.getAnnotation(DbShard.class);

        TableShard tableAnnotation = mapperClazz.getAnnotation(TableShard.class);

        Method mapperMethod = shardMapperMethod(mappedStatement, mapperClazz);
        if (mapperMethod == null || ArrayUtils.isEmpty(mapperMethod.getParameterTypes())) {
            return null;
        }

        DataSourceInfo dataSourceInfo = buildDataSourceInfo(mapperMethod, dbAnnotation, executeParam);

        TableInfo tableInfo = buildTableInfo(mapperMethod, tableAnnotation, executeParam);

        if (dataSourceInfo == null && tableInfo == null) {
            return null;
        }

        return new ExecuteInfo(dataSourceInfo, tableInfo);

    }

    private TableInfo buildTableInfo(Method mapperMethod, TableShard tableAnnotation, Object executeParam) {

        Map<String, Object> params = Maps.newHashMap();
        for (Annotation[] annotations : mapperMethod.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {

                TableShardWith tableShardWith = null;
                if (annotation instanceof TableShardWith) {
                    tableShardWith = (TableShardWith) annotation;
                }

                Param param = null;
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                }

                if (tableShardWith != null) {

                    // 原生参数的处理方法.
                    if (ArrayUtils.isEmpty(tableShardWith.props())) {
                        checkNotNull(param, "对基本数据类型参数必须得有@Param说明名字");
                        params.put(param.value(), takeRawParameterValue(executeParam, param));
                        continue;
                    }

                    // 非原生带内嵌参数的处理.
                    for (String prop : tableShardWith.props()) {
                        params.put(prop, checkNotNull(Reflections.getPropertyValue(executeParam, prop)));
                    }
                }

            }
        }

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTablePattern(tableAnnotation.tablePattern());
        tableInfo.setRule(tableAnnotation.rule());
        tableInfo.setParams(params);
        return tableInfo;

    }

    private DataSourceInfo buildDataSourceInfo(Method mapperMethod, DbShard dbShardAnnotation, Object executeParam) {

        Map<String, Object> params = Maps.newHashMap();
        for (Annotation[] annotations : mapperMethod.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {

                DbShardWith dbShardWith = null;
                if (annotation instanceof DbShardWith) {
                    dbShardWith = (DbShardWith) annotation;
                }

                Param param = null;
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                }

                if (dbShardWith != null) {

                    // 原生参数的处理方法.
                    if (ArrayUtils.isEmpty(dbShardWith.props())) {
                        checkNotNull(param, "对基本数据类型参数必须得有@Param说明名字");
                        params.put(param.value(), takeRawParameterValue(executeParam, param));
                        continue;
                    }

                    // 非原生带内嵌参数的处理.
                    for (String prop : dbShardWith.props()) {
                        params.put(prop, checkNotNull(Reflections.getPropertyValue(executeParam, prop)));
                    }

                }

            }
        }

        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setDataSourceName(dbShardAnnotation.dbKey());
        dataSourceInfo.setRule(dbShardAnnotation.rule());
        dataSourceInfo.setParams(params);

        return dataSourceInfo;
    }

    private Class<?> mapperClazz(MappedStatement mappedStatement) {
        String mapperId = mappedStatement.getId();

        String _mapperClazz = StringUtils.substringBeforeLast(mapperId, ".");

        Class<?> mapperClazz = null;
        try {
            mapperClazz = Class.forName(_mapperClazz);
        } catch (ClassNotFoundException e) {
        }
        return mapperClazz;
    }

    private Method shardMapperMethod(MappedStatement mappedStatement, Class<?> mapperClazz) {
        String _mapperMethod = StringUtils.substringAfterLast(mappedStatement.getId(), ".");

        Method mapperMethod = null;
        for (Method method : mapperClazz.getMethods()) {
            if (Objects.equal(method.getName(), _mapperMethod)) {
                mapperMethod = method;
            }
        }
        return mapperMethod;
    }

    private Object takeRawParameterValue(Object parameterObject, Param batisParam) {
        // this is dirt and wait to improve...
        checkNotNull(batisParam, "@TableShardWith标注的基本基本数据类型参数必须有@Param注解命名");
        Object value = Reflections.getPropertyValue(parameterObject, batisParam.value());
        checkArgument(value.getClass().isPrimitive(), "没有prop属性的@TableShardWith注解只能针对基本类型");
        return value;
    }

}
