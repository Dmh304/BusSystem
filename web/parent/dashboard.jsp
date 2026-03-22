<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Parent - Dashboard</h2>
            <p class="small">${timeHint}</p>
        </div>

        <div class="card">
            <h3>Edit Permissions</h3>
            <p>Morning:
                <span class="tag ${canEditMorning ? 'green' : 'red'}">${canEditMorning ? 'Editable' : 'Locked'}</span>
            </p>
            <p>Afternoon:
                <span class="tag ${canEditAfternoon ? 'green' : 'red'}">${canEditAfternoon ? 'Editable' : 'Locked'}</span>
            </p>
        </div>

        <div class="card">
            <h3>Student List</h3>
            <table>
                <tr>
                    <th>Student Code</th>
                    <th>Full Name</th>
                    <th>Grade</th>
                    <th>Pickup Stop</th>
                    <th>Managed By</th>
                    <th>Morning</th>
                    <th>Afternoon</th>
                </tr>
                <c:forEach items="${students}" var="student">
                    <tr>
                        <td>${student.studentCode}</td>
                        <td>${student.fullName}</td>
                        <td>${student.grade}</td>
                        <td>${student.pickupStopName}</td>
                        <td>
                            <strong>Mgr: ${student.managerName}</strong><br>
                            <strong>Drv: ${student.driverName}</strong>
                        </td>
                        <td>
                            Choice:
                            <strong>${empty morningMap[student.studentId] ? 'not select yet' : morningMap[student.studentId].attendanceChoice}</strong>
                            <br>
                            Boarding:
                            <strong>${empty statusMorningMap[student.studentId] ? 'No manifest' : statusMorningMap[student.studentId].boardingStatus}</strong>
                        </td>
                        <td>
                            Choice:
                            <strong>${empty afternoonMap[student.studentId] ? 'not select yet' : afternoonMap[student.studentId].attendanceChoice}</strong>
                            <br>
                            Boarding:
                            <strong>${empty statusAfternoonMap[student.studentId] ? 'No manifest' : statusAfternoonMap[student.studentId].boardingStatus}</strong>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
