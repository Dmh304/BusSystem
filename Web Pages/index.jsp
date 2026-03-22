<%@page import="model.UserAccount"%>
<%
    UserAccount currentUser = (UserAccount) session.getAttribute("currentUser");
    if (currentUser != null) {
        String role = currentUser.getRoleName().toUpperCase();
        if ("ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if ("MANAGER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/manager/dashboard");
        } else if ("DRIVER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/driver/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/parent/dashboard");
        }
        return;
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
