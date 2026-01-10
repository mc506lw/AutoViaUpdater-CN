package common;

import java.util.logging.Logger;

public class LoggerUtil {
    private static java.util.logging.Logger javaLogger = null;
    private static org.slf4j.Logger slf4jLogger = null;

    public static void setJavaLogger(java.util.logging.Logger log) {
        javaLogger = log;
    }

    public static void setSlf4jLogger(org.slf4j.Logger log) {
        slf4jLogger = log;
    }

    public static void setLogger(Logger log) {
        setJavaLogger(log);
    }

    public static void info(String message) {
        if (slf4jLogger != null) {
            slf4jLogger.info(message);
        } else if (javaLogger != null) {
            javaLogger.info(message);
        } else {
            System.out.println(message);
        }
    }

    public static void warning(String message) {
        if (slf4jLogger != null) {
            slf4jLogger.warn(message);
        } else if (javaLogger != null) {
            javaLogger.warning(message);
        } else {
            System.err.println(message);
        }
    }

    public static void severe(String message) {
        if (slf4jLogger != null) {
            slf4jLogger.error(message);
        } else if (javaLogger != null) {
            javaLogger.severe(message);
        } else {
            System.err.println(message);
        }
    }
}