package org.lysu.shard.example.app;

import com.google.common.collect.ImmutableMap;
import org.lysu.shard.example.mapper.TestMapper;
import org.lysu.shard.example.model.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * @author lysu created on 14-4-6 下午4:47
 * @version $Id$
 */
public class App {

    public static void main(String[] args) {

        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(
                "spring-config.xml");
        TestMapper bean = classPathXmlApplicationContext.getBean(TestMapper.class);

        for (int i = 1; i < 100; i++) {
            Test test = new Test();
            test.setA(i);
            test.setB(i);
            test.setC(i);
//            bean.save(test);
        }

        Map<String, Object> param = ImmutableMap.<String, Object> of("a", 2);
        List<Test> result = bean.query(param);

        System.out.println(result);

    }

}
