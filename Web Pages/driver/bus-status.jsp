<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Thông tin xe</h2>
            <p>Biển số: <strong>${bus.plateNumber}</strong></p>
            <p>Tên xe: <strong>${bus.busName}</strong></p>
            <p>Sức chứa: <strong>${bus.capacity}</strong></p>
            <p>Tuyến: <strong>${bus.routeName}</strong></p>
            <p>Trạng thái hiện tại: <strong>${bus.status}</strong></p>
        </div>

        <div class="card">
            <h3>Cập nhật trạng thái xe</h3>
            <form action="${pageContext.request.contextPath}/driver/bus-status" method="post">
                <input type="hidden" name="busId" value="${bus.busId}">
                <select name="status">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                    <option value="MAINTENANCE">MAINTENANCE</option>
                </select>
                <button type="submit">Cập nhật</button>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
