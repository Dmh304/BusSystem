<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
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
            <p>Xe: ${empty manifest ? 'N/A' : manifest.plateNumber} | Tuyến: ${empty manifest ? 'N/A' : manifest.routeName}</p>
            <p>Current stop: ${empty manifest.currentStopName ? 'Chưa xuất phát' : manifest.currentStopName}</p>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>Mã HS</th>
                    <th>Họ tên</th>
                    <th>Trạm đón</th>
                    <th>Lựa chọn</th>
                    <th>Boarding</th>
                    <th>Ghi chú</th>
                </tr>
                <c:forEach items="${students}" var="item">
                    <tr>
                        <td>${item.studentCode}</td>
                        <td>${item.studentName}</td>
                        <td>${item.pickupStopName}</td>
                        <td>${item.attendanceChoice}</td>
                        <td>${item.boardingStatus}</td>
                        <td>${item.note}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
