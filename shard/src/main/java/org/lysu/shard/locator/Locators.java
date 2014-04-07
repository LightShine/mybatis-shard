package org.lysu.shard.locator;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author lysu created on 14-4-6 下午3:59
 * @version $Id$
 */
public enum Locators {

    instance;

    private static final Cache<String, Locator> locateCached = CacheBuilder.<String, Locator> newBuilder().build();

    public Locator takeLocator(final String rule) {
        try {
            return locateCached.get(rule, new Callable<Locator>() {
                @Override
                public Locator call() throws Exception {
                    return new GroovyLocator(rule);
                }
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

}
