package com.cloud.gemini.util;

import org.slf4j.Logger;

/**
 * @author fufan
 * @Desc
 * @date 2021/2/3 9:10 AM
 */
public class LogUtil {

    public static void logRecord(Logger log, String message) {
        log.info(message);
    }

    public static void logRecord(Logger log, String message, String traceId) {
        log.info(message + " [" + traceId + "]");
    }
}
