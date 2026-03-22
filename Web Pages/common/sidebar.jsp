<%@page import="model.UserAccount"%>
<%
    UserAccount currentUser = (UserAccount) session.getAttribute("currentUser");
    String roleName = currentUser == null || currentUser.getRoleName() == null ? "" : currentUser.getRoleName().toUpperCase();
%>
<div class="sidebar">
    <div class="sidebar-box">
        <h3>Bus Management</h3>
        <% if (currentUser != null) { %>
            <p><strong><%= currentUser.getFullName() %></strong></p>
            <p>Role: <%= currentUser.getRoleName() %></p>
        <% } %>
        <hr>
        <% if ("PARENT".equals(roleName)) { %>
            <a href="<%= request.getContextPath() %>/parent/dashboard">Dashboard</a>
            <a href="<%= request.getContextPath() %>/parent/registration">Registration</a>
            <a href="<%= request.getContextPath() %>/parent/trip-status">Trip Status</a>
        <% } else if ("MANAGER".equals(roleName)) { %>
            <a href="<%= request.getContextPath() %>/manager/dashboard">Dashboard</a>
            <a href="<%= request.getContextPath() %>/manager/manifest">Manifest</a>
            <a href="<%= request.getContextPath() %>/manager/boarding">Boarding</a>
            <a href="<%= request.getContextPath() %>/manager/tracking">Tracking</a>
        <% } else if ("DRIVER".equals(roleName)) { %>
            <a href="<%= request.getContextPath() %>/driver/dashboard">Dashboard</a>
            <a href="<%= request.getContextPath() %>/driver/bus-status">Bus Status</a>
            <a href="<%= request.getContextPath() %>/driver/incident">Incident</a>
        <% } else if ("ADMIN".equals(roleName)) { %>
            <a href="<%= request.getContextPath() %>/admin/dashboard">Dashboard</a>
            <a href="<%= request.getContextPath() %>/admin/users">Users</a>
            <a href="<%= request.getContextPath() %>/admin/students">Students</a>
            <a href="<%= request.getContextPath() %>/admin/buses">Buses</a>
            <a href="<%= request.getContextPath() %>/admin/routes">Routes</a>
        <% } %>
        <hr>
        <a href="<%= request.getContextPath() %>/logout" class="danger-link">Logout</a>
    </div>
</div>
