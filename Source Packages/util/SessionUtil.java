package util;

import jakarta.servlet.http.HttpSession;
import model.UserAccount;

public class SessionUtil {

    public static final String CURRENT_USER = "currentUser";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

    public static void setCurrentUser(HttpSession session, UserAccount user) {
        if (session != null) {
            session.setAttribute(CURRENT_USER, user);
        }
    }

    public static UserAccount getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(CURRENT_USER);
        if (value instanceof UserAccount) {
            return (UserAccount) value;
        }
        return null;
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    public static void setSuccess(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute(SUCCESS_MESSAGE, message);
        }
    }

    public static void setError(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute(ERROR_MESSAGE, message);
        }
    }

    public static String redirectDashboard(UserAccount user) {
        if (user == null || user.getRoleName() == null) {
            return "/login";
        }
        switch (user.getRoleName().toUpperCase()) {
            case "ADMIN":
                return "/admin/dashboard";
            case "MANAGER":
                return "/manager/dashboard";
            case "DRIVER":
                return "/driver/dashboard";
            default:
                return "/parent/dashboard";
        }
    }
}
