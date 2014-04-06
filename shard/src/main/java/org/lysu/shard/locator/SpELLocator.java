package org.lysu.shard.locator;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 *
 * @deprecated @see org.lysu.shard.locator.GroovyLocator
 * @author lysu created on 14-4-6 下午3:53
 * @version $Id$
 */
@Deprecated
public class SpELLocator implements Locator {

    private String rule;

    public SpELLocator(String rule) {
        this.rule = rule;
    }

    @Override
    public String locate(Map<String, Object> locateParam) {

        ExpressionParser parser = new SpelExpressionParser();

        Expression expression = parser.parseExpression(this.rule);

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(locateParam);

        return expression.getValue(context, String.class);

    }

}
