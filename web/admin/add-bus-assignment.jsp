<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Assign Bus to Manager</h2>
            <p class="small">Assign a bus to an unassigned manager + driver pair</p>
        </div>

        <div class="card">
            <form action="${pageContext.request.contextPath}/admin/add-bus-assignment" method="post">
                <input type="hidden" name="action" value="assign">
                
                <div>
                    <label for="busId">Select Bus (Unassigned):</label>
                    <select id="busId" name="busId" required>
                        <option value="">-- Select Bus --</option>
                        <c:forEach items="${unassignedBuses}" var="bus">
                            <option value="${bus.busId}">${bus.busName} (${bus.plateNumber}) - ${bus.capacity} seats</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div>
                    <label for="managerUserId">Manager (Unassigned):</label>
                    <select id="managerUserId" name="managerUserId" required>
                        <option value="">-- Select Manager --</option>
                        <c:forEach items="${managers}" var="mgr">
                            <option value="${mgr.userId}">${mgr.fullName} (${mgr.username})</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div>
                    <label for="driverUserId">Driver (Unassigned):</label>
                    <select id="driverUserId" name="driverUserId" required>
                        <option value="">-- Select Driver --</option>
                        <c:forEach items="${drivers}" var="drv">
                            <option value="${drv.userId}">${drv.fullName} (${drv.username})</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div>
                    <label for="routeId">Route:</label>
                    <select id="routeId" name="routeId" required>
                        <option value="">-- Select Route --</option>
                        <c:forEach items="${routes}" var="route">
                            <option value="${route.routeId}">${route.routeName}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <button type="submit" class="success">Assign Bus</button>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="button">Cancel</a>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
