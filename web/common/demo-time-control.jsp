<%@page import="util.DemoTimeUtil"%>
<%@page import="java.time.LocalTime"%>
<%
    String currentDemoTime = DemoTimeUtil.getDisplayTime(session);
    String currentUrl = request.getRequestURI();
    if (request.getQueryString() != null) {
        currentUrl += "?" + request.getQueryString();
    }
%>
<div class="card">
    <h3>?i?u khi?n th?i gian mô ph?ng</h3>
    <p>Th?i gian hi?n t?i: <strong><%= currentDemoTime %></strong></p>
    <div class="button-row">
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="04:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">04:30 - M? ??ng ký sáng</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="05:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">05:30 - Ch?t sáng</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="07:00">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">07:00 - Xe sáng ch?y</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="13:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">13:30 - M? chi?u</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="14:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">14:30 - Ch?t chi?u</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="16:00">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">16:00 - Xe chi?u ch?y</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="action" value="reset">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit" class="secondary">Reset</button>
        </form>
    </div>
</div>
