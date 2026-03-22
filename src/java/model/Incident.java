package model;

public class Incident {
    private int incidentId;
    private int assignmentId;
    private Integer driverUserId;
    private String title;
    private String description;
    private String incidentStatus;
    private java.sql.Timestamp reportedAt;
    private java.sql.Timestamp resolvedAt;
    private String driverName;
    private String plateNumber;

    public Incident() {
    }

    public int getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Integer getDriverUserId() {
        return driverUserId;
    }

    public void setDriverUserId(Integer driverUserId) {
        this.driverUserId = driverUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIncidentStatus() {
        return incidentStatus;
    }

    public void setIncidentStatus(String incidentStatus) {
        this.incidentStatus = incidentStatus;
    }

    public java.sql.Timestamp getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(java.sql.Timestamp reportedAt) {
        this.reportedAt = reportedAt;
    }

    public java.sql.Timestamp getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(java.sql.Timestamp resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

}