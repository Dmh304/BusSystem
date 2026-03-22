<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="session-tabs">
            <a class="${selectedSession eq 'MORNING' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/dashboard?sessionType=MORNING">MORNING</a>
            <a class="${selectedSession eq 'AFTERNOON' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/dashboard?sessionType=AFTERNOON">AFTERNOON</a>
        </div>

        <div class="grid-4">
            <div class="card">
                <h3>Assigned Bus</h3>
                <div class="stat-number">${empty bus ? '-' : bus.plateNumber}</div>
                <div class="small">${empty bus ? '' : bus.busName}</div>
            </div>
            <div class="card">
                <h3>Route</h3>
                <div class="stat-number">${empty bus ? '-' : bus.routeName}</div>
            </div>
            <div class="card">
                <h3>BUS Students</h3>
                <div class="stat-number">${empty busCount ? 0 : busCount}</div>
            </div>
            <div class="card">
                <h3>Boarded</h3>
                <div class="stat-number">${empty boardedCount ? 0 : boardedCount}</div>
            </div>
        </div>

        <div class="card">
            <h3>Manifest Info</h3>
            <p>Session: <strong>${selectedSession}</strong></p>
            <p>Manifest status: <strong>${empty manifest ? 'N/A' : manifest.manifestStatus}</strong></p>
            <p>Current stop: <strong>${empty manifest.currentStopName ? 'Not Departed' : manifest.currentStopName}</strong></p>
            <p>Boarding update permission:
                <span class="tag ${canUpdate ? 'green' : 'red'}">${canUpdate ? 'Can Update' : 'Not Yet Available'}</span>
            </p>
        </div>

        <div class="card">
            <h3>Quick List</h3>
            <table>
                <tr>
                    <th>Student</th>
                    <th>Pickup Stop</th>
                    <th>Choice</th>
                    <th>Boarding</th>
                </tr>
                <c:forEach items="${students}" var="item">
                    <tr>
                        <td>${item.studentName}</td>
                        <td>${item.pickupStopName}</td>
                        <td>${item.attendanceChoice}</td>
                        <td>
                            <c:choose>
                                <c:when test="${item.boardingStatus eq 'BOARDED'}">
                                    <span class="status-boarded">BOARDED</span>
                                </c:when>
                                <c:when test="${item.boardingStatus eq 'NO_SHOW'}">
                                    <span class="status-noshow">NO_SHOW</span>
                                </c:when>
                                <c:when test="${item.boardingStatus eq 'PENDING'}">
                                    <span class="status-pending">PENDING</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag gray">${item.boardingStatus}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
