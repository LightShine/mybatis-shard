package org.lysu.shard.interceptor;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.lysu.shard.tools.Reflections;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author lysu created on 14-4-6 下午6:25
 * @version $Id$
 */
public abstract class AbstractShardInterceptor implements Interceptor {

    protected Class<?> mapperClazz(MappedStatement mappedStatement) {
        String mapperId = mappedStatement.getId();

        String _mapperClazz = StringUtils.substringBeforeLast(mapperId, ".");

        Class<?> mapperClazz = null;
        try {
            mapperClazz = Class.forName(_mapperClazz);
        } catch (ClassNotFoundException e) {
        }
        return mapperClazz;
    }

    protected Method shardMapperMethod(MappedStatement mappedStatement, Class<?> mapperClazz) {
        String _mapperMethod = StringUtils.substringAfterLast(mappedStatement.getId(), ".");

        Method mapperMethod = null;
        for (Method method : mapperClazz.getMethods()) {
            if (Objects.equal(method.getName(), _mapperMethod)) {
                mapperMethod = method;
            }
        }
        return mapperMethod;
    }

    protected Object takeRawParameterValue(Object parameterObject, Param batisParam) {
        // this is dirt and wait to improve...
        checkNotNull(batisParam, "@TableShardWith标注的基本基本数据类型参数必须有@Param注解命名");
        Object value = Reflections.getPropertyValue(parameterObject, batisParam.value());
        checkArgument(value.getClass().isPrimitive(), "没有prop属性的@TableShardWith注解只能针对基本类型");
        return value;
    }

}
