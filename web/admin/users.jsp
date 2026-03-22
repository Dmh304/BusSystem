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
                </select>
                <button type="submit" style="padding: 8px 15px;">Filter</button>
                <button type="button" onclick="showAddForm()" style="padding: 8px 15px; margin-left: 10px; background-color: #28a745; color: white; border: none; cursor: pointer; border-radius: 4px;">Add New User</button>
            </form>
        </div>

        <div class="card" id="userFormDiv" style="display: none; background-color: #f8f9fa; border: 1px solid #ddd;">
            <h3 id="formTitle" style="margin-top: 0;">Add User</h3>
            <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display: flex; flex-wrap: wrap; gap: 10px; align-items: center;">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="userId" id="userId" value="">
                
                <input type="text" name="username" id="username" placeholder="Username" required style="padding: 8px;">
                <input type="password" name="password" id="password" placeholder="Password" required style="padding: 8px;">
                <input type="text" name="fullName" id="fullName" placeholder="Full Name" required style="padding: 8px;">
                <input type="email" name="email" id="email" placeholder="Email" required style="padding: 8px;">
                
                <select name="roleName" id="roleName" required style="padding: 8px;">
                    <option value="MANAGER">MANAGER</option>
                    <option value="DRIVER">DRIVER</option>
                    <option value="PARENT">PARENT</option>
                </select>
                
                <select name="status" id="status" style="display: none; padding: 8px;">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                </select>
                
                <button type="submit" style="padding: 8px 15px; background-color: #007bff; color: white; border: none; cursor: pointer; border-radius: 4px;">Save</button>
                <button type="button" onclick="hideForm()" style="padding: 8px 15px; background-color: #6c757d; color: white; border: none; cursor: pointer; border-radius: 4px;">Cancel</button>
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
                    <th>Actions</th>
                </tr>
                <c:forEach items="${users}" var="item">
                    <tr>
                        <td>${item.userId}</td>
                        <td>${item.username}</td>
                        <td>${item.fullName}</td>
                        <td>${item.email}</td>
                        <td>${item.roleName}</td>
                        <td>${item.status}</td>
                        <td>
                            <c:if test="${item.roleName != 'ADMIN'}">
                                <button type="button" onclick="showEditForm(this)" 
                                    data-id="${item.userId}" 
                                    data-username="${item.username}" 
                                    data-fullname="${item.fullName}" 
                                    data-email="${item.email}" 
                                    data-role="${item.roleName}" 
                                    data-status="${item.status}"
                                    style="padding: 5px 10px; cursor: pointer; border: none; background-color: #ffc107; border-radius: 4px;">Edit</button>
                                
                                <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="${item.status == 'ACTIVE' ? 'lock' : 'unlock'}">
                                    <input type="hidden" name="userId" value="${item.userId}">
                                    <button type="submit" style="padding: 5px 10px; cursor: pointer; border: none; background-color: ${item.status == 'ACTIVE' ? '#fd7e14' : '#28a745'}; color: white; border-radius: 4px;">
                                        ${item.status == 'ACTIVE' ? 'Lock' : 'Unlock'}
                                    </button>
                                </form>

                                <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to permanently delete this user? This action cannot be undone!');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="userId" value="${item.userId}">
                                    <button type="submit" style="padding: 5px 10px; cursor: pointer; border: none; background-color: #dc3545; color: white; border-radius: 4px;">Delete</button>
                                </form>
                                
                                <c:if test="${item.roleName == 'PARENT'}">
                                    <a href="${pageContext.request.contextPath}/admin/students?keyword=${item.fullName}" style="display:inline-block; padding: 5px 10px; background-color: #17a2b8; color: white; text-decoration: none; border-radius: 4px; margin-left: 5px; font-size: 13px;">View Children</a>
                                </c:if>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        
        <script>
        function showAddForm() {
            document.getElementById('userFormDiv').style.display = 'block';
            document.getElementById('formTitle').innerText = 'Add User';
            document.getElementById('formAction').value = 'add';
            document.getElementById('userId').value = '';
            document.getElementById('username').value = '';
            document.getElementById('username').readOnly = false;
            document.getElementById('password').style.display = 'inline-block';
            document.getElementById('password').required = true;
            document.getElementById('fullName').value = '';
            document.getElementById('email').value = '';
            document.getElementById('roleName').value = 'MANAGER';
            document.getElementById('status').style.display = 'none';
        }
        
        function showEditForm(btn) {
            document.getElementById('userFormDiv').style.display = 'block';
            document.getElementById('formTitle').innerText = 'Edit User';
            document.getElementById('formAction').value = 'edit';
            document.getElementById('userId').value = btn.getAttribute('data-id');
            document.getElementById('username').value = btn.getAttribute('data-username');
            document.getElementById('username').readOnly = true;
            document.getElementById('password').style.display = 'none';
            document.getElementById('password').required = false;
            document.getElementById('fullName').value = btn.getAttribute('data-fullname');
            document.getElementById('email').value = btn.getAttribute('data-email');
            document.getElementById('roleName').value = btn.getAttribute('data-role');
            document.getElementById('status').value = btn.getAttribute('data-status');
            document.getElementById('status').style.display = 'inline-block';
        }
        
        function hideForm() {
            document.getElementById('userFormDiv').style.display = 'none';
        }
        </script>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
