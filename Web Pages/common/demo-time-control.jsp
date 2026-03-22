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
    <h3>Điều khiển thời gian mô phỏng</h3>
    <p>Thời gian hiện tại: <strong><%= currentDemoTime %></strong></p>
    <div class="button-row">
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="04:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">04:30 - Mở đăng ký sáng</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="05:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">05:30 - Chốt sáng</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="07:00">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">07:00 - Xe sáng chạy</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="13:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">13:30 - Mở chiều</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="14:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">14:30 - Chốt chiều</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="16:00">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">16:00 - Xe chiều chạy</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="action" value="reset">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit" class="secondary">Reset</button>
        </form>
    </div>
</div>
