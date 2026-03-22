package model;

public class RouteStop {
    private int routeStopId;
    private int routeId;
    private int stopId;
    private int stopOrder;
    private java.sql.Time estimatedMorningTime;
    private java.sql.Time estimatedAfternoonTime;
    private String stopName;
    private String addressDetail;

    public RouteStop() {
    }

    public int getRouteStopId() {
        return routeStopId;
    }

    public void setRouteStopId(int routeStopId) {
        this.routeStopId = routeStopId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public java.sql.Time getEstimatedMorningTime() {
        return estimatedMorningTime;
    }

    public void setEstimatedMorningTime(java.sql.Time estimatedMorningTime) {
        this.estimatedMorningTime = estimatedMorningTime;
    }

    public java.sql.Time getEstimatedAfternoonTime() {
        return estimatedAfternoonTime;
    }

    public void setEstimatedAfternoonTime(java.sql.Time estimatedAfternoonTime) {
        this.estimatedAfternoonTime = estimatedAfternoonTime;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

}