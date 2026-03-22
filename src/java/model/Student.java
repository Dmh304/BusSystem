package model;

public class Student {
    private int studentId;
    private int parentUserId;
    private String studentCode;
    private String fullName;
    private String gender;
    private String grade;
    private int defaultRouteId;
    private int defaultPickupStopId;
    private int defaultDropoffStopId;
    private String status;
    private String pickupStopName;
    private String dropoffStopName;
    private String routeName;
    private String parentName;
    private String managerName;
    private String driverName;

    public Student() {
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(int parentUserId) {
        this.parentUserId = parentUserId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getDefaultRouteId() {
        return defaultRouteId;
    }

    public void setDefaultRouteId(int defaultRouteId) {
        this.defaultRouteId = defaultRouteId;
    }

    public int getDefaultPickupStopId() {
        return defaultPickupStopId;
    }

    public void setDefaultPickupStopId(int defaultPickupStopId) {
        this.defaultPickupStopId = defaultPickupStopId;
    }

    public int getDefaultDropoffStopId() {
        return defaultDropoffStopId;
    }

    public void setDefaultDropoffStopId(int defaultDropoffStopId) {
        this.defaultDropoffStopId = defaultDropoffStopId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickupStopName() {
        return pickupStopName;
    }

    public void setPickupStopName(String pickupStopName) {
        this.pickupStopName = pickupStopName;
    }

    public String getDropoffStopName() {
        return dropoffStopName;
    }

    public void setDropoffStopName(String dropoffStopName) {
        this.dropoffStopName = dropoffStopName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

}