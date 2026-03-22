<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Quản lý tuyến</h2>
            <form action="${pageContext.request.contextPath}/admin/routes" method="get">
                <select name="routeId">
                    <c:forEach items="${routes}" var="route">
                        <option value="${route.routeId}" ${route.routeId eq selectedRouteId ? 'selected="selected"' : ''}>
                            ${route.routeName}
                        </option>
                    </c:forEach>
                </select>
                <button type="submit">Xem trạm</button>
            </form>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>STT</th>
                    <th>Trạm</th>
                    <th>Giờ sáng</th>
                    <th>Giờ chiều</th>
                </tr>
                <c:forEach items="${routeStops}" var="item">
                    <tr>
                        <td>${item.stopOrder}</td>
                        <td>${item.stopName}</td>
                        <td>${item.estimatedMorningTime}</td>
                        <td>${item.estimatedAfternoonTime}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
