<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.UserAccount"%>
<%
    String pageTitle = (String) request.getAttribute("pageTitle");
    if (pageTitle == null || pageTitle.trim().isEmpty()) {
        pageTitle = "Bus Management System";
    }
    UserAccount currentUser = (UserAccount) session.getAttribute("currentUser");
    String dashboardPath = "";
    if (currentUser != null && currentUser.getRoleName() != null) {
        String roleName = currentUser.getRoleName().toLowerCase();
        if ("parent".equals(roleName)) {
            dashboardPath = request.getContextPath() + "/parent/dashboard";
        } else if ("driver".equals(roleName)) {
            dashboardPath = request.getContextPath() + "/driver/dashboard";
        } else if ("manager".equals(roleName)) {
            dashboardPath = request.getContextPath() + "/manager/dashboard";
        } else if ("admin".equals(roleName)) {
            dashboardPath = request.getContextPath() + "/admin/dashboard";
        }
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><%= pageTitle %></title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
    </head>
    <body>
        <% if (currentUser != null) { %>
        <nav class="top-navbar">
            <div class="navbar-left">
                <a href="<%= dashboardPath %>" class="navbar-brand">BusManagement</a>
            </div>
            <div class="navbar-right">
                <span class="navbar-user">
                    <strong><%= currentUser.getFullName() %></strong>
                    <span class="navbar-role">(<%= currentUser.getRoleName() %>)</span>
                </span>
                <a href="<%= request.getContextPath() %>/logout" class="button-logout">Logout</a>
            </div>
        </nav>
        <% } %>
