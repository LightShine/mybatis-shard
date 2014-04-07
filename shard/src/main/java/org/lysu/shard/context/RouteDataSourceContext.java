package org.lysu.shard.context;

/**
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public class RouteDataSourceContext {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    public static String getRouteKey() {
        return (String) contextHolder.get();
    }

    public static void setRouteKey(String routeKey) {
        contextHolder.set(routeKey);
    }

    public static void clearRouteKey() {
        contextHolder.remove();
    }

}
