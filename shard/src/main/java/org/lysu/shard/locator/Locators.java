package org.lysu.shard.locator;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.lysu.shard.config.LocatorConfig;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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
                    //如果未设置locator property，那么使用默认的SpElLocator
                    if(StringUtils.isEmpty(LocatorConfig.getLocater())){
                        return new SpELLocator(rule);
                    }
                    Class locatorClass = Class.forName(LocatorConfig.getLocater());
                    Constructor<Locator> constructor = locatorClass.getConstructor(String.class);
                    return constructor.newInstance(rule);
                }
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

}