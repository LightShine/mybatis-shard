package org.lysu.shard.config;

/**
 * Author: wangguangyao@cmhi.chinamobile.com
 * Project: mybatis-shard
 * Date: 2017-07-03
 * Time: 10:40
 */
public class LocatorConfig {

    private static volatile String locater;

    public static String getLocater() {
        return locater;
    }

    /**
     * locator只能设置一次
     * @param locater
     */
    public static void setLocater(String locater) {
        if(LocatorConfig.locater == null){
            synchronized (LocatorConfig.class){
                if(LocatorConfig.locater == null){
                    LocatorConfig.locater = locater;
                }
            }
        }
    }
}
