<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Bus Information</h2>
            <p>Plate Number: <strong>${bus.plateNumber}</strong></p>
            <p>Bus Name: <strong>${bus.busName}</strong></p>
            <p>Capacity: <strong>${bus.capacity}</strong></p>
            <p>Route: <strong>${bus.routeName}</strong></p>
            <p>Current Status: <strong>${bus.status}</strong></p>
        </div>

        <div class="card">
            <h3>Update Bus Status</h3>
            <form action="${pageContext.request.contextPath}/driver/bus-status" method="post">
                <input type="hidden" name="busId" value="${bus.busId}">
                <select name="status">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                    <option value="MAINTENANCE">MAINTENANCE</option>
                </select>
                <button type="submit">Update</button>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
