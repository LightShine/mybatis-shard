package org.lysu.shard.ds;

/**
 * @author lysu created on 14-4-6 下午3:31
 * @version $Id$
 */
public class RoutingSetting {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    public static void setRoutingInfo(String customerType) {
        contextHolder.set(customerType);
    }

    public static String getRoutingInfo() {
        return (String) contextHolder.get();
    }

    public static void clearRoutingInfo() {
        contextHolder.remove();
    }

}
