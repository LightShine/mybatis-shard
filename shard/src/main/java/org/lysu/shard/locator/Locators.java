package org.lysu.shard.locator;

/**
 * @author lysu created on 14-4-6 下午3:59
 * @version $Id$
 */
public enum Locators {

    instance;

    public Locator takeLocator(String rule) {
        return new SpELLocator(rule);
    }

}
