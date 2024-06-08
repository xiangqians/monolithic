package org.xiangqian.monolithic.util;

import java.time.ZoneId;

/**
 * 时区工具
 *
 * @author xiangqian
 * @date 19:52 2024/06/07
 */
public class ZoneIdUtil {

    /**
     * 默认时区
     */
    public static final ZoneId DEFAULT = ZoneId.systemDefault();

    /**
     * 【亚洲】中国标准时间
     */
    public static final ZoneId ASIA_SHANGHAI = ZoneId.of("Asia/Shanghai");

    /**
     * 【欧洲】格林尼治标准时间
     */
    public static final ZoneId EUROPE_LONDON = ZoneId.of("Europe/London");

    /**
     * 【美洲】东部标准时间
     */
    public static final ZoneId AMERICA_NEWYORK = ZoneId.of("America/New_York");

    /**
     * 【非洲】南非标准时间
     */
    public static final ZoneId AFRICA_JOHANNESBURG = ZoneId.of("Africa/Johannesburg");

    /**
     * 【大洋洲】澳大利亚东部标准时间
     */
    public static final ZoneId AUSTRALIA_SYDNEY = ZoneId.of("Australia/Sydney");

}
