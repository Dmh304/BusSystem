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
            return "Phụ huynh đang được phép đăng ký cả buổi sáng và buổi chiều.";
        }
        if (isMorningLocked(time)) {
            return "Buổi sáng đã chốt danh sách. Chỉ quản lý xe xem manifest.";
        }
        if (isMorningRunning(time)) {
            return "Buổi sáng đang chạy. Quản lý xe có thể cập nhật lên xe.";
        }
        if (isAfternoonLocked(time)) {
            return "Buổi chiều đã chốt danh sách. Phụ huynh không thể sửa đăng ký.";
        }
        if (isAfternoonRunning(time)) {
            return "Buổi chiều đang chạy. Quản lý xe có thể cập nhật lên xe.";
        }
        return "Hệ thống đang ở chế độ mô phỏng.";
    }
}
