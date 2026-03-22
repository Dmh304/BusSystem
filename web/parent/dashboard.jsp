<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Phụ huynh - Dashboard</h2>
            <p class="small">${timeHint}</p>
        </div>

        <div class="grid-2">
            <div class="card">
                <h3>Quyền chỉnh sửa</h3>
                <p>Buổi sáng:
                    <span class="tag ${canEditMorning ? 'green' : 'red'}">${canEditMorning ? 'Được sửa' : 'Đã khóa'}</span>
                </p>
                <p>Buổi chiều:
                    <span class="tag ${canEditAfternoon ? 'green' : 'red'}">${canEditAfternoon ? 'Được sửa' : 'Đã khóa'}</span>
                </p>
            </div>

            <div class="card">
                <h3>Nhanh</h3>
                <div class="button-row">
                    <a class="button-link" href="${pageContext.request.contextPath}/parent/registration">Đăng ký chuyến</a>
                    <a class="button-link" href="${pageContext.request.contextPath}/parent/trip-status">Xem trạng thái xe</a>
                </div>
            </div>
        </div>

        <div class="card">
            <h3>Danh sách học sinh</h3>
            <table>
                <tr>
                    <th>Mã HS</th>
                    <th>Họ tên</th>
                    <th>Lớp</th>
                    <th>Trạm đón</th>
                    <th>Quản lý bởi</th>
                    <th>Sáng</th>
                    <th>Chiều</th>
                </tr>
                <c:forEach items="${students}" var="student">
                    <tr>
                        <td>${student.studentCode}</td>
                        <td>${student.fullName}</td>
                        <td>${student.grade}</td>
                        <td>${student.pickupStopName}</td>
                        <td>
                            <strong>QL: ${student.managerName}</strong><br>
                            <strong>TK: ${student.driverName}</strong>
                        </td>
                        <td>
                            Lựa chọn:
                            <strong>${empty morningMap[student.studentId] ? 'BUS (mặc định)' : morningMap[student.studentId].attendanceChoice}</strong>
                            <br>
                            Lên xe:
                            <strong>${empty statusMorningMap[student.studentId] ? 'Chưa có manifest' : statusMorningMap[student.studentId].boardingStatus}</strong>
                        </td>
                        <td>
                            Lựa chọn:
                            <strong>${empty afternoonMap[student.studentId] ? 'BUS (mặc định)' : afternoonMap[student.studentId].attendanceChoice}</strong>
                            <br>
                            Lên xe:
                            <strong>${empty statusAfternoonMap[student.studentId] ? 'Chưa có manifest' : statusAfternoonMap[student.studentId].boardingStatus}</strong>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
