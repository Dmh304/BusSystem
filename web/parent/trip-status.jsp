<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Trip Status</h2>
            <p>Track whether students have boarded and current bus location.</p>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>Student</th>
                    <th>Morning</th>
                    <th>Afternoon</th>
                </tr>
                <c:forEach items="${students}" var="student">
                    <tr>
                        <td>
                            <strong>${student.fullName}</strong><br>
                            ${student.grade} - ${student.pickupStopName}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty statusMorningMap[student.studentId]}">
                                    No morning manifest
                                </c:when>
                                <c:otherwise>
                                    Status: <strong>${statusMorningMap[student.studentId].boardingStatus}</strong><br>
                                    Bus at: <strong>${empty statusMorningMap[student.studentId].currentStopName ? 'Not Departed' : statusMorningMap[student.studentId].currentStopName}</strong>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty statusAfternoonMap[student.studentId]}">
                                    No afternoon manifest
                                </c:when>
                                <c:otherwise>
                                    Status: <strong>${statusAfternoonMap[student.studentId].boardingStatus}</strong><br>
                                    Bus at: <strong>${empty statusAfternoonMap[student.studentId].currentStopName ? 'Not Departed' : statusAfternoonMap[student.studentId].currentStopName}</strong>
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
