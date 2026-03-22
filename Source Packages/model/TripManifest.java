package model;

public class TripManifest {
    private int manifestId;
    private int assignmentId;
    private java.sql.Date tripDate;
    private String sessionType;
    private String manifestStatus;
    private Integer currentRouteStopId;
    private java.sql.Time departureTime;
    private java.sql.Timestamp startedAt;
    private java.sql.Timestamp finishedAt;
    private String plateNumber;
    private String routeName;
    private String currentStopName;

    public TripManifest() {
    }

    public int getManifestId() {
        return manifestId;
    }

    public void setManifestId(int manifestId) {
        this.manifestId = manifestId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
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

    public String getManifestStatus() {
        return manifestStatus;
    }

    public void setManifestStatus(String manifestStatus) {
        this.manifestStatus = manifestStatus;
    }

    public Integer getCurrentRouteStopId() {
        return currentRouteStopId;
    }

    public void setCurrentRouteStopId(Integer currentRouteStopId) {
        this.currentRouteStopId = currentRouteStopId;
    }

    public java.sql.Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(java.sql.Time departureTime) {
        this.departureTime = departureTime;
    }

    public java.sql.Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(java.sql.Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public java.sql.Timestamp getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(java.sql.Timestamp finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getCurrentStopName() {
        return currentStopName;
    }

    public void setCurrentStopName(String currentStopName) {
        this.currentStopName = currentStopName;
    }

}