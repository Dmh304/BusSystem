package util;

import jakarta.servlet.http.HttpSession;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DemoTimeUtil {

    public static final String DEMO_TIME_KEY = "demoTime";
    private static final LocalTime DEFAULT_TIME = LocalTime.of(4, 30);
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static LocalTime getDemoTime(HttpSession session) {
        if (session == null) {
            return DEFAULT_TIME;
        }
        Object value = session.getAttribute(DEMO_TIME_KEY);
        if (value == null) {
            session.setAttribute(DEMO_TIME_KEY, DEFAULT_TIME.toString());
            return DEFAULT_TIME;
        }
        try {
            return LocalTime.parse(value.toString());
        } catch (Exception e) {
            session.setAttribute(DEMO_TIME_KEY, DEFAULT_TIME.toString());
            return DEFAULT_TIME;
        }
    }

    public static void setDemoTime(HttpSession session, LocalTime time) {
        if (session != null && time != null) {
            session.setAttribute(DEMO_TIME_KEY, time.toString());
        }
    }

    public static void resetDemoTime(HttpSession session) {
        if (session != null) {
            session.setAttribute(DEMO_TIME_KEY, DEFAULT_TIME.toString());
        }
    }

    public static String getDisplayTime(HttpSession session) {
        return getDemoTime(session).format(DISPLAY_FORMAT);
    }
}
