<%
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    if (request.getAttribute("error") != null) {
        errorMessage = request.getAttribute("error").toString();
    }
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
%>
<% if (successMessage != null && !successMessage.trim().isEmpty()) { %>
    <div class="alert success"><%= successMessage %></div>
<% } %>
<% if (errorMessage != null && !errorMessage.trim().isEmpty()) { %>
    <div class="alert error"><%= errorMessage %></div>
<% } %>
