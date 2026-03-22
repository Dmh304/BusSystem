<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Quản lý xe</h2>
            <table>
                <tr>
                    <th>Biển số</th>
                    <th>Tên xe</th>
                    <th>Sức chứa</th>
                    <th>Tuyến</th>
                    <th>Manager</th>
                    <th>Driver</th>
                    <th>Trạng thái</th>
                    <th>Cập nhật</th>
                </tr>
                <c:forEach items="${buses}" var="item">
                    <tr>
                        <td>${item.plateNumber}</td>
                        <td>${item.busName}</td>
                        <td>${item.capacity}</td>
                        <td>${item.routeName}</td>
                        <td>${item.managerName}</td>
                        <td>${item.driverName}</td>
                        <td>${item.status}</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/buses" method="post">
                                <input type="hidden" name="busId" value="${item.busId}">
                                <select name="status">
                                    <option value="ACTIVE">ACTIVE</option>
                                    <option value="INACTIVE">INACTIVE</option>
                                    <option value="MAINTENANCE">MAINTENANCE</option>
                                </select>
                                <button type="submit">Lưu</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
