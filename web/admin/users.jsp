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
                    <option value="ADMIN" ${roleName eq 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                    <option value="MANAGER" ${roleName eq 'MANAGER' ? 'selected' : ''}>MANAGER</option>
                    <option value="DRIVER" ${roleName eq 'DRIVER' ? 'selected' : ''}>DRIVER</option>
                    <option value="PARENT" ${roleName eq 'PARENT' ? 'selected' : ''}>PARENT</option>
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
                        <td>
                            <c:choose>
                                <c:when test="${item.roleName eq 'ADMIN'}">
                                    <span class="role-admin">ADMIN</span>
                                </c:when>
                                <c:when test="${item.roleName eq 'MANAGER'}">
                                    <span class="role-manager">MANAGER</span>
                                </c:when>
                                <c:when test="${item.roleName eq 'DRIVER'}">
                                    <span class="role-driver">DRIVER</span>
                                </c:when>
                                <c:when test="${item.roleName eq 'PARENT'}">
                                    <span class="role-parent">PARENT</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag gray">${item.roleName}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${item.status eq 'ACTIVE'}">
                                    <span class="bus-active">ACTIVE</span>
                                </c:when>
                                <c:when test="${item.status eq 'INACTIVE'}">
                                    <span class="bus-inactive">INACTIVE</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag gray">${item.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
