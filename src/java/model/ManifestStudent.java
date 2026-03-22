package model;

public class ManifestStudent {
    private int manifestStudentId;
    private int manifestId;
    private int studentId;
    private String attendanceChoice;
    private String boardingStatus;
    private java.sql.Timestamp boardedAt;
    private String note;
    private String studentCode;
    private String studentName;
    private String pickupStopName;
    private int pickupStopOrder;
    private String sessionType;
    private java.sql.Date tripDate;
    private String manifestStatus;
    private String currentStopName;

    public ManifestStudent() {
    }

    public int getManifestStudentId() {
        return manifestStudentId;
    }

    public void setManifestStudentId(int manifestStudentId) {
        this.manifestStudentId = manifestStudentId;
    }

    public int getManifestId() {
        return manifestId;
    }

    public void setManifestId(int manifestId) {
        this.manifestId = manifestId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getAttendanceChoice() {
        return attendanceChoice;
    }

    public void setAttendanceChoice(String attendanceChoice) {
        this.attendanceChoice = attendanceChoice;
    }

    public String getBoardingStatus() {
        return boardingStatus;
    }

    public void setBoardingStatus(String boardingStatus) {
        this.boardingStatus = boardingStatus;
    }

    public java.sql.Timestamp getBoardedAt() {
        return boardedAt;
    }

    public void setBoardedAt(java.sql.Timestamp boardedAt) {
        this.boardedAt = boardedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPickupStopName() {
        return pickupStopName;
    }

    public void setPickupStopName(String pickupStopName) {
        this.pickupStopName = pickupStopName;
    }

    public int getPickupStopOrder() {
        return pickupStopOrder;
    }

    public void setPickupStopOrder(int pickupStopOrder) {
        this.pickupStopOrder = pickupStopOrder;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public java.sql.Date getTripDate() {
        return tripDate;
    }

    public void setTripDate(java.sql.Date tripDate) {
        this.tripDate = tripDate;
    }

    public String getManifestStatus() {
        return manifestStatus;
    }

    public void setManifestStatus(String manifestStatus) {
        this.manifestStatus = manifestStatus;
    }

    public String getCurrentStopName() {
        return currentStopName;
    }

    public void setCurrentStopName(String currentStopName) {
        this.currentStopName = currentStopName;
    }

}