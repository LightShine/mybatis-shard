package org.lysu.shard.locator;

import com.google.common.collect.Maps;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;

/**
 * @author lysu created on 14-4-6 下午3:53
 * @version $Id$
 */
public class SpELLocator implements Locator {

    private String rule;

    public SpELLocator(String rule) {
        this.rule = rule;
    }

    @Override
    public String locate(List<Object> locateKey) {

        ExpressionParser parser = new SpelExpressionParser();

        Expression expression = parser.parseExpression(this.rule);

        Map<String, Object> rootObject = buildRoot(locateKey);

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(rootObject);

        return expression.getValue(context, String.class);

    }

    private Map<String, Object> buildRoot(List<Object> locateKey) {
        Map<String, Object> root = Maps.newHashMap();
        for (int i = 0; i < locateKey.size(); ++i) {
            String name = (i == 0) ? "$" : "$" + i;
            root.put(name, locateKey.get(i));
        }
        return root;
    }

}
