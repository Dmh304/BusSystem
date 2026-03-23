<%@page import="model.UserAccount"%>
<%
    UserAccount currentUser = (UserAccount) session.getAttribute("currentUser");
    String roleName = currentUser == null || currentUser.getRoleName() == null ? "" : currentUser.getRoleName().toUpperCase();
    String currentPath = request.getServletPath();
%>
<div class="sidebar-nav">
    <% if ("PARENT".equals(roleName)) { %>
        <a href="<%= request.getContextPath() %>/parent/dashboard" class="<%= currentPath.contains("/parent/dashboard") ? "active" : "" %>">Dashboard</a>
        <a href="<%= request.getContextPath() %>/parent/children" class="<%= currentPath.contains("/parent/children") ? "active" : "" %>">My Children</a>
        <a href="<%= request.getContextPath() %>/parent/registration" class="<%= currentPath.contains("/parent/registration") ? "active" : "" %>">Registration</a>
        <a href="<%= request.getContextPath() %>/parent/trip-status" class="<%= currentPath.contains("/parent/trip-status") ? "active" : "" %>">Trip Status</a>
    <% } else if ("MANAGER".equals(roleName)) { %>
        <a href="<%= request.getContextPath() %>/manager/dashboard" class="<%= currentPath.contains("/manager/dashboard") ? "active" : "" %>">Dashboard</a>
        <a href="<%= request.getContextPath() %>/manager/manifest" class="<%= currentPath.contains("/manager/manifest") ? "active" : "" %>">Manifest</a>
        <a href="<%= request.getContextPath() %>/manager/boarding" class="<%= currentPath.contains("/manager/boarding") ? "active" : "" %>">Boarding</a>
        <a href="<%= request.getContextPath() %>/manager/tracking" class="<%= currentPath.contains("/manager/tracking") ? "active" : "" %>">Tracking</a>
    <% } else if ("DRIVER".equals(roleName)) { %>
        <a href="<%= request.getContextPath() %>/driver/dashboard" class="<%= currentPath.contains("/driver/dashboard") ? "active" : "" %>">Dashboard</a>
        <a href="<%= request.getContextPath() %>/driver/bus-status" class="<%= currentPath.contains("/driver/bus-status") ? "active" : "" %>">Bus Status</a>
        <a href="<%= request.getContextPath() %>/driver/incident" class="<%= currentPath.contains("/driver/incident") ? "active" : "" %>">Incident</a>
    <% } else if ("ADMIN".equals(roleName)) { %>
        <a href="<%= request.getContextPath() %>/admin/dashboard" class="<%= currentPath.contains("/admin/dashboard") ? "active" : "" %>">Dashboard</a>
        <a href="<%= request.getContextPath() %>/admin/users" class="<%= currentPath.contains("/admin/users") ? "active" : "" %>">Users</a>
        <a href="<%= request.getContextPath() %>/admin/students" class="<%= currentPath.contains("/admin/students") ? "active" : "" %>">Students</a>
        <a href="<%= request.getContextPath() %>/admin/buses" class="<%= currentPath.contains("/admin/buses") ? "active" : "" %>">Buses</a>
        <a href="<%= request.getContextPath() %>/admin/routes" class="<%= currentPath.contains("/admin/routes") ? "active" : "" %>">Routes</a>
    <% } %>
</div>
