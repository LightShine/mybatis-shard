package org.lysu.shard.locator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * 
 * @author lysu created on 14-4-6 下午3:53
 * @version $Id$
 */
public class SpELLocator implements Locator {

    private String rule;

    private static ThreadLocal<StandardEvaluationContext> sc = new ThreadLocal<StandardEvaluationContext>(){
        @Override
        public StandardEvaluationContext initialValue(){
            return new StandardEvaluationContext();
        }
    };

    private static ThreadLocal<ExpressionParser> parser = new ThreadLocal<ExpressionParser>(){
        @Override
        public ExpressionParser initialValue(){
            return new SpelExpressionParser();
        }
    };

    public SpELLocator(String rule) {
        this.rule = rule;
    }

    @Override
    public String locate(Map<String, Object> locateParam) {
        if(locateParam.isEmpty()){
            return null;
        }
        Expression expression = parser.get().parseExpression(this.rule);
        try {
            sc.get().registerFunction("leftPad", SpelFunctions.class.getDeclaredMethod("leftPad", String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        sc.get().setVariables(locateParam);
        return expression.getValue(sc.get(), String.class);
    }

    static class SpelFunctions{
        public static String leftPad(String input){
            return StringUtils.leftPad(input, 3, '0');
        }
    }

}
