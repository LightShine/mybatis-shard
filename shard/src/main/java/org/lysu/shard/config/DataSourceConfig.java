package org.lysu.shard.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * @author lysu created on 14-4-7 上午1:44
 * @version $Id$
 */
public class DataSourceConfig {

    private Set<String> availableKey = Sets.newHashSet();

    private String dataSourceName;

    private String rule;

    private Map<String, Object> params = Maps.newHashMap();

    public Set<String> getAvailableKey() {
        return availableKey;
    }

    public void setAvailableKey(Set<String> availableKey) {
        this.availableKey = availableKey;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
