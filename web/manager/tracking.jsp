<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="session-tabs">
            <a class="${selectedSession eq 'MORNING' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/tracking?sessionType=MORNING">MORNING</a>
            <a class="${selectedSession eq 'AFTERNOON' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/tracking?sessionType=AFTERNOON">AFTERNOON</a>
        </div>

        <div class="card">
            <h2>Bus Tracking - ${selectedSession}</h2>
            <p>Bus: ${empty manifest ? 'N/A' : manifest.plateNumber}</p>
            <p>Current Stop: 
                <strong>
                    <c:choose>
                        <c:when test="${empty manifest.currentStopName}">
                            <span class="trip-status not-started">Not Departed</span>
                        </c:when>
                        <c:otherwise>
                            <span class="trip-status in-progress">${manifest.currentStopName}</span>
                        </c:otherwise>
                    </c:choose>
                </strong>
            </p>
        </div>

        <!-- Auto-refresh indicator -->
        <div class="auto-refresh-bar">
            <div class="pulse-dot"></div>
            <span>Live tracking - auto-refreshes every <strong>15 seconds</strong></span>
            <span id="refreshCountdown" style="margin-left: auto; font-weight: bold;">15s</span>
        </div>

        <div class="card">
            <h3>Stop List</h3>
            <table>
                <tr>
                    <th>Order</th>
                    <th>Stop</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                <c:forEach items="${routeStops}" var="stop">
                    <c:set var="currentStopId" value="${manifest.currentRouteStopId}" />
                    <c:set var="isPassed" value="false" />
                    <c:set var="isCurrent" value="false" />
                    
                    <c:if test="${not empty currentStopId}">
                        <c:forEach items="${routeStops}" var="cs">
                            <c:if test="${cs.routeStopId eq currentStopId}">
                                <c:set var="currentStopOrder" value="${cs.stopOrder}" />
                            </c:if>
                        </c:forEach>
                        <c:if test="${stop.stopOrder < currentStopOrder}">
                            <c:set var="isPassed" value="true" />
                        </c:if>
                        <c:if test="${stop.routeStopId eq currentStopId}">
                            <c:set var="isCurrent" value="true" />
                        </c:if>
                    </c:if>

                    <tr class="${isCurrent ? 'stop-current' : (isPassed ? 'stop-passed' : 'stop-upcoming')}">
                        <td>${stop.stopOrder}</td>
                        <td>
                            ${stop.stopName}
                            <c:if test="${isCurrent}"> &nbsp;<span class="tag blue">CURRENT</span></c:if>
                            <c:if test="${isPassed}"> &nbsp;<span class="tag green">PASSED</span></c:if>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${isCurrent}">
                                    <span class="tag blue">At this stop</span>
                                </c:when>
                                <c:when test="${isPassed}">
                                    <span class="tag green">Completed</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag gray">Upcoming</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <form action="${pageContext.request.contextPath}/manager/tracking" method="post">
                                <input type="hidden" name="sessionType" value="${selectedSession}">
                                <input type="hidden" name="manifestId" value="${manifest.manifestId}">
                                <input type="hidden" name="routeStopId" value="${stop.routeStopId}">
                                <c:choose>
                                    <c:when test="${isCurrent}">
                                        <button type="submit" class="info" disabled="disabled" style="opacity: 0.4;">Current Stop</button>
                                    </c:when>
                                    <c:when test="${isPassed}">
                                        <button type="submit" class="secondary" style="opacity: 0.6;">Re-select</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" class="success">Arrive Here</button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>

<script>
    // Auto-refresh every 15 seconds
    (function() {
        var countdown = 15;
        var countdownEl = document.getElementById('refreshCountdown');
        
        setInterval(function() {
            countdown--;
            if (countdownEl) {
                countdownEl.textContent = countdown + 's';
            }
            if (countdown <= 0) {
                location.reload();
            }
        }, 1000);
    })();
</script>

<jsp:include page="/common/footer.jsp"></jsp:include>
