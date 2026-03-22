<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="session-tabs">
            <a class="${selectedSession eq 'MORNING' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/manifest?sessionType=MORNING">MORNING</a>
            <a class="${selectedSession eq 'AFTERNOON' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/manifest?sessionType=AFTERNOON">AFTERNOON</a>
        </div>

        <div class="card">
            <h2>Manifest - ${selectedSession}</h2>
            <p>Bus: ${empty manifest ? 'N/A' : manifest.plateNumber} | Route: ${empty manifest ? 'N/A' : manifest.routeName}</p>
            <p>Current stop: ${empty manifest.currentStopName ? 'Not Departed' : manifest.currentStopName}</p>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>Student Code</th>
                    <th>Full Name</th>
                    <th>Pickup Stop</th>
                    <th>Choice</th>
                    <th>Boarding</th>
                    <th>Note</th>
                </tr>
                <c:forEach items="${students}" var="item">
                    <tr>
                        <td>${item.studentCode}</td>
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
                        <td>${item.note}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
