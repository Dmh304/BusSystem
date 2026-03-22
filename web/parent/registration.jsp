<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Register Trip for Students</h2>
            <p class="small">${timeHint}</p>
        </div>

        <c:forEach items="${students}" var="student">
            <div class="card">
                <h3>${student.fullName} - ${student.studentCode}</h3>
                <p>Grade: ${student.grade} | Pickup Stop: ${student.pickupStopName} | Route: ${student.routeName}</p>
                <p style="color: #0066cc; font-weight: bold;">
                    Manager: ${student.managerName} | Driver: ${student.driverName}
                </p>

                <div class="grid-2">
                    <div>
                        <h4>Morning Session</h4>
                        <p>Current:
                            <strong>${empty morningMap[student.studentId] ? 'BUS (default)' : morningMap[student.studentId].attendanceChoice}</strong>
                        </p>
                        <form action="${pageContext.request.contextPath}/parent/registration" method="post">
                            <input type="hidden" name="studentId" value="${student.studentId}">
                            <input type="hidden" name="sessionType" value="MORNING">
                            <select name="attendanceChoice" ${!canEditMorning ? 'disabled="disabled"' : ''}>
                                <option value="BUS">BUS</option>
                                <option value="SELF">SELF</option>
                                <option value="OFF">OFF</option>
                            </select>
                            <textarea name="note" placeholder="Note"></textarea>
                            <button type="submit" ${!canEditMorning ? 'disabled="disabled"' : ''}>Save Morning</button>
                        </form>
                    </div>

                    <div>
                        <h4>Afternoon Session</h4>
                        <p>Current:
                            <strong>${empty afternoonMap[student.studentId] ? 'BUS (default)' : afternoonMap[student.studentId].attendanceChoice}</strong>
                        </p>
                        <form action="${pageContext.request.contextPath}/parent/registration" method="post">
                            <input type="hidden" name="studentId" value="${student.studentId}">
                            <input type="hidden" name="sessionType" value="AFTERNOON">
                            <select name="attendanceChoice" ${!canEditAfternoon ? 'disabled="disabled"' : ''}>
                                <option value="BUS">BUS</option>
                                <option value="SELF">SELF</option>
                                <option value="OFF">OFF</option>
                            </select>
                            <textarea name="note" placeholder="Note"></textarea>
                            <button type="submit" ${!canEditAfternoon ? 'disabled="disabled"' : ''}>Save Afternoon</button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
