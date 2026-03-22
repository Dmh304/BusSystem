<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="session-tabs">
            <a class="${selectedSession eq 'MORNING' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/tracking?sessionType=MORNING">MORNING</a>
            <a class="${selectedSession eq 'AFTERNOON' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/tracking?sessionType=AFTERNOON">AFTERNOON</a>
        </div>

        <div class="card">
            <h2>Theo dõi xe - ${selectedSession}</h2>
            <p>Xe: ${empty manifest ? 'N/A' : manifest.plateNumber}</p>
            <p>Trạm hiện tại: <strong>${empty manifest.currentStopName ? 'Chưa xuất phát' : manifest.currentStopName}</strong></p>
        </div>

        <div class="card">
            <h3>Danh sách trạm</h3>
            <table>
                <tr>
                    <th>Thứ tự</th>
                    <th>Trạm</th>
                    <th>Hành động</th>
                </tr>
                <c:forEach items="${routeStops}" var="stop">
                    <tr>
                        <td>${stop.stopOrder}</td>
                        <td>${stop.stopName}</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/manager/tracking" method="post">
                                <input type="hidden" name="sessionType" value="${selectedSession}">
                                <input type="hidden" name="manifestId" value="${manifest.manifestId}">
                                <input type="hidden" name="routeStopId" value="${stop.routeStopId}">
                                <button type="submit">Đặt là trạm hiện tại</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
