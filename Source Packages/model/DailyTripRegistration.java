package model;

public class DailyTripRegistration {
    private int registrationId;
    private int studentId;
    private java.sql.Date tripDate;
    private String sessionType;
    private String attendanceChoice;
    private String sourceType;
    private String note;
    private java.sql.Timestamp updatedAt;

    public DailyTripRegistration() {
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public java.sql.Date getTripDate() {
        return tripDate;
    }

    public void setTripDate(java.sql.Date tripDate) {
        this.tripDate = tripDate;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getAttendanceChoice() {
        return attendanceChoice;
    }

    public void setAttendanceChoice(String attendanceChoice) {
        this.attendanceChoice = attendanceChoice;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public java.sql.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

}