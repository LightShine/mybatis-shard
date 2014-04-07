package org.lysu.shard.locator;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author lysu created on 14-4-7 上午12:43
 * @version $Id$
 */
public class GroovyScriptHelper {

    private static final FastDateFormat MONTH_PART = FastDateFormat.getInstance("yyyyMM");

    public static String month(Date date) {
        return MONTH_PART.format(date);
    }

    public static String leftPad(long data, int toLength) {
        return StringUtils.leftPad(String.valueOf(data), toLength, '0');
    }

}
