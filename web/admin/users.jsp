<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>User Management</h2>
            <form action="${pageContext.request.contextPath}/admin/users" method="get">
                <input type="text" name="keyword" value="${keyword}" placeholder="Search by username, name, email">
                <select name="roleName">
                    <option value="">All Roles</option>
                    <option value="ADMIN">ADMIN</option>
                    <option value="MANAGER">MANAGER</option>
                    <option value="DRIVER">DRIVER</option>
                    <option value="PARENT">PARENT</option>
                </select>
                <button type="submit">Filter</button>
            </form>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Status</th>
                </tr>
                <c:forEach items="${users}" var="item">
                    <tr>
                        <td>${item.userId}</td>
                        <td>${item.username}</td>
                        <td>${item.fullName}</td>
                        <td>${item.email}</td>
                        <td>${item.roleName}</td>
                        <td>${item.status}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
