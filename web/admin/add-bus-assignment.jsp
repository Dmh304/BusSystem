<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Gán xe cho Quản lý</h2>
            <p class="small">Gán xe cho cặp quản lý + tài xế chưa được gán</p>
        </div>

        <div class="card">
            <form action="${pageContext.request.contextPath}/admin/add-bus-assignment" method="post">
                <input type="hidden" name="action" value="assign">
                
                <div>
                    <label for="busId">Chọn xe (Chưa được gán):</label>
                    <select id="busId" name="busId" required>
                        <option value="">-- Chọn xe --</option>
                        <c:forEach items="${unassignedBuses}" var="bus">
                            <option value="${bus.busId}">${bus.busName} (${bus.plateNumber}) - ${bus.capacity} chỗ</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div>
                    <label for="managerUserId">Quản lý (Chưa được gán):</label>
                    <select id="managerUserId" name="managerUserId" required>
                        <option value="">-- Chọn quản lý --</option>
                        <c:forEach items="${managers}" var="mgr">
                            <option value="${mgr.userId}">${mgr.fullName} (${mgr.username})</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div>
                    <label for="driverUserId">Tài xế (Chưa được gán):</label>
                    <select id="driverUserId" name="driverUserId" required>
                        <option value="">-- Chọn tài xế --</option>
                        <c:forEach items="${drivers}" var="drv">
                            <option value="${drv.userId}">${drv.fullName} (${drv.username})</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div>
                    <label for="routeId">Tuyến đường:</label>
                    <select id="routeId" name="routeId" required>
                        <option value="">-- Chọn tuyến đường --</option>
                        <c:forEach items="${routes}" var="route">
                            <option value="${route.routeId}">${route.routeName}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <button type="submit" class="success">Gán xe</button>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="button">Hủy</a>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
