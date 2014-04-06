package org.lysu.shard.locator;

import java.util.List;

/**
 * @author lysu created on 14-4-6 下午3:52
 * @version $Id$
 */
public interface Locator {

    String locate(List<Object> locateKey);

}

