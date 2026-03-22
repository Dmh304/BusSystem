<%@page import="util.DemoTimeUtil"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%
    String currentDemoTime = DemoTimeUtil.getDisplayTime(session);
    String currentUrl = request.getRequestURI();
    if (request.getQueryString() != null) {
        currentUrl += "?" + request.getQueryString();
    }
    String realServerTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
%>
<div class="card">
    <h3>Simulation Time Control</h3>
    <div class="time-display">
        <div class="time-item real-time">
            <div>
                <div class="time-label">Real Time</div>
                <div class="time-value" id="realTimeClock"><%= realServerTime %></div>
            </div>
        </div>
        <div class="time-item sim-time">
            <div>
                <div class="time-label">Simulation Time</div>
                <div class="time-value"><%= currentDemoTime %></div>
            </div>
        </div>
    </div>
    <div class="button-row">
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="04:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">04:30 - Open Morning Reg.</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="05:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">05:30 - Lock Morning</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="07:00">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">07:00 - Morning Depart</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="13:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">13:30 - Open Afternoon</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="14:30">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">14:30 - Lock Afternoon</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="value" value="16:00">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit">16:00 - Afternoon Depart</button>
        </form>
        <form action="<%= request.getContextPath() %>/demo-time" method="post">
            <input type="hidden" name="action" value="reset">
            <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
            <button type="submit" class="secondary">Reset</button>
        </form>
    </div>
</div>
<script>
    (function() {
        var clockEl = document.getElementById('realTimeClock');
        if (!clockEl) return;
        setInterval(function() {
            var now = new Date();
            var h = String(now.getHours()).padStart(2, '0');
            var m = String(now.getMinutes()).padStart(2, '0');
            var s = String(now.getSeconds()).padStart(2, '0');
            clockEl.textContent = h + ':' + m + ':' + s;
        }, 1000);
    })();
</script>