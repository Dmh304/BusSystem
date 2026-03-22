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
                <h3>Xe phụ trách</h3>
                <div class="stat-number">${empty bus ? '-' : bus.plateNumber}</div>
                <div class="small">${empty bus ? '' : bus.busName}</div>
            </div>
            <div class="card">
                <h3>Tuyến</h3>
                <div class="stat-number">${empty bus ? '-' : bus.routeName}</div>
            </div>
            <div class="card">
                <h3>Học sinh BUS</h3>
                <div class="stat-number">${empty busCount ? 0 : busCount}</div>
            </div>
            <div class="card">
                <h3>Đã lên xe</h3>
                <div class="stat-number">${empty boardedCount ? 0 : boardedCount}</div>
            </div>
        </div>

        <div class="card">
            <h3>Thông tin manifest</h3>
            <p>Session: <strong>${selectedSession}</strong></p>
            <p>Manifest status: <strong>${empty manifest ? 'N/A' : manifest.manifestStatus}</strong></p>
            <p>Trạm hiện tại: <strong>${empty manifest.currentStopName ? 'Chưa xuất phát' : manifest.currentStopName}</strong></p>
            <p>Quyền cập nhật lên xe:
                <span class="tag ${canUpdate ? 'green' : 'red'}">${canUpdate ? 'Được cập nhật' : 'Chưa tới giờ cập nhật'}</span>
            </p>
        </div>

        <div class="card">
            <h3>Danh sách nhanh</h3>
            <table>
                <tr>
                    <th>HS</th>
                    <th>Trạm đón</th>
                    <th>Lựa chọn</th>
                    <th>Boarding</th>
                </tr>
                <c:forEach items="${students}" var="item">
                    <tr>
                        <td>${item.studentName}</td>
                        <td>${item.pickupStopName}</td>
                        <td>${item.attendanceChoice}</td>
                        <td>${item.boardingStatus}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
