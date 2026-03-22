package util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DemoTimeUtil {

    public static final String DEMO_TIME_KEY = "demoTime";
    private static final LocalTime DEFAULT_TIME = LocalTime.of(4, 30);
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ServletContext method - used to share time between all sessions
    public static LocalTime getDemoTime(ServletContext context) {
        if (context == null) {
            return DEFAULT_TIME;
        }
        Object value = context.getAttribute(DEMO_TIME_KEY);
        if (value == null) {
            context.setAttribute(DEMO_TIME_KEY, DEFAULT_TIME.toString());
            return DEFAULT_TIME;
        }
        try {
            return LocalTime.parse(value.toString());
        } catch (Exception e) {
            context.setAttribute(DEMO_TIME_KEY, DEFAULT_TIME.toString());
            return DEFAULT_TIME;
        }
    }

    public static void setDemoTime(ServletContext context, LocalTime time) {
        if (context != null && time != null) {
            context.setAttribute(DEMO_TIME_KEY, time.toString());
        }
    }

    public static void resetDemoTime(ServletContext context) {
        if (context != null) {
            context.setAttribute(DEMO_TIME_KEY, DEFAULT_TIME.toString());
        }
    }

    public static String getDisplayTime(ServletContext context) {
        return getDemoTime(context).format(DISPLAY_FORMAT);
    }

    // Deprecated: kept for backward compatibility
    @Deprecated
    public static LocalTime getDemoTime(HttpSession session) {
        if (session == null || session.getServletContext() == null) {
            return DEFAULT_TIME;
        }
        return getDemoTime(session.getServletContext());
    }

    @Deprecated
    public static void setDemoTime(HttpSession session, LocalTime time) {
        if (session != null && session.getServletContext() != null) {
            setDemoTime(session.getServletContext(), time);
        }
    }

    @Deprecated
    public static void resetDemoTime(HttpSession session) {
        if (session != null && session.getServletContext() != null) {
            resetDemoTime(session.getServletContext());
        }
    }

    @Deprecated
    public static String getDisplayTime(HttpSession session) {
        if (session == null || session.getServletContext() == null) {
            return DEFAULT_TIME.format(DISPLAY_FORMAT);
        }
        return getDisplayTime(session.getServletContext());
    }
}