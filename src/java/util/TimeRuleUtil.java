package util;

import java.time.LocalTime;

public class TimeRuleUtil {

    public static boolean canParentEditMorning(LocalTime time) {
        return !time.isBefore(LocalTime.of(18, 0)) || time.isBefore(LocalTime.of(5, 0));
    }

    public static boolean isMorningLocked(LocalTime time) {
        return !time.isBefore(LocalTime.of(5, 0)) && time.isBefore(LocalTime.of(7, 0));
    }

    public static boolean isMorningRunning(LocalTime time) {
        return !time.isBefore(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(14, 0));
    }

    public static boolean canParentEditAfternoon(LocalTime time) {
        return !time.isBefore(LocalTime.of(18, 0)) || time.isBefore(LocalTime.of(14, 0));
    }

    public static boolean isAfternoonLocked(LocalTime time) {
        return !time.isBefore(LocalTime.of(14, 0)) && time.isBefore(LocalTime.of(16, 0));
    }

    public static boolean isAfternoonRunning(LocalTime time) {
        return !time.isBefore(LocalTime.of(16, 0)) && time.isBefore(LocalTime.of(18, 0));
    }

    public static boolean canManagerUpdateManifest(String sessionType, LocalTime time) {
        if ("MORNING".equalsIgnoreCase(sessionType)) {
            return isMorningRunning(time);
        }
        if ("AFTERNOON".equalsIgnoreCase(sessionType)) {
            return isAfternoonRunning(time);
        }
        return false;
    }

    public static String getPreferredSession(LocalTime time) {
        if (isAfternoonLocked(time) || isAfternoonRunning(time)) {
            return "AFTERNOON";
        }
        return "MORNING";
    }

    public static String getParentHint(LocalTime time) {
        if (canParentEditMorning(time) && canParentEditAfternoon(time)) {
            return "Parents can register for both morning and afternoon sessions.";
        }
        if (isMorningLocked(time)) {
            return "Morning registration is locked. Only the bus manager can view the manifest.";
        }
        if (isMorningRunning(time)) {
            return "Morning session is running. The bus manager can update boarding.";
        }
        if (isAfternoonLocked(time)) {
            return "Afternoon registration is locked. Parents cannot edit.";
        }
        if (isAfternoonRunning(time)) {
            return "Afternoon session is running. The bus manager can update boarding.";
        }
        return "System is in simulation mode.";
    }
}
