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

        <div class="card tracking-header-card">
            <h2>Bus Tracking - ${selectedSession}</h2>
            <p>Bus: ${empty manifest ? 'N/A' : manifest.plateNumber}</p>
            <p>
                <c:choose>
                    <c:when test="${selectedSession eq 'MORNING'}">
                        Direction: <span class="tag blue">Home → School (Stop 1 → 7)</span>
                    </c:when>
                    <c:otherwise>
                        Direction: <span class="tag orange">School → Home (Stop 7 → 1)</span>
                    </c:otherwise>
                </c:choose>
            </p>
            <p>Current Stop: 
                <strong>
                    <c:choose>
                        <c:when test="${empty manifest.currentStopName}">
                            <span class="trip-status not-started">Not Departed</span>
                        </c:when>
                        <c:when test="${isAtFinalStop}">
                            <span class="trip-status completed">${manifest.currentStopName} - ARRIVED AT DESTINATION</span>
                        </c:when>
                        <c:otherwise>
                            <span class="trip-status in-progress">${manifest.currentStopName}</span>
                        </c:otherwise>
                    </c:choose>
                </strong>
            </p>
        </div>

        <!-- Destination reached notification -->
        <c:if test="${isAtFinalStop}">
            <div class="destination-reached-alert">
                <div class="destination-icon">🏁</div>
                <div class="destination-text">
                    <strong>Trip Completed!</strong>
                    <c:choose>
                        <c:when test="${selectedSession eq 'MORNING'}">
                            The bus has arrived at the final stop. All students have reached the school.
                        </c:when>
                        <c:otherwise>
                            The bus has arrived at the final stop. All students have been dropped off at home.
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:if>

        <!-- Auto-refresh indicator -->
        <div class="auto-refresh-bar">
            <div class="pulse-dot"></div>
            <span>Live tracking - auto-refreshes every <strong>15 seconds</strong></span>
            <span id="refreshCountdown" style="margin-left: auto; font-weight: bold;">15s</span>
        </div>

        <div class="card">
            <h3>Stop List 
                <c:choose>
                    <c:when test="${selectedSession eq 'MORNING'}">
                        <span class="small">(Stop 1 → 7)</span>
                    </c:when>
                    <c:otherwise>
                        <span class="small">(Stop 7 → 1)</span>
                    </c:otherwise>
                </c:choose>
            </h3>
            <div class="table-scroll-wrapper">
                <table>
                    <thead class="sticky-table-header">
                        <tr>
                            <th>Order</th>
                            <th>Stop</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="displayOrder" value="0" />
                        <c:forEach items="${routeStops}" var="stop" varStatus="loop">
                            <c:set var="displayOrder" value="${loop.index + 1}" />
                            <c:set var="currentStopId" value="${manifest.currentRouteStopId}" />
                            <c:set var="isPassed" value="false" />
                            <c:set var="isCurrent" value="false" />
                            <c:set var="isLast" value="${loop.last}" />
                            
                            <c:if test="${not empty currentStopId}">
                                <%-- Find the index of the current stop in the displayed list --%>
                                <c:set var="currentIndex" value="-1" />
                                <c:forEach items="${routeStops}" var="cs" varStatus="csLoop">
                                    <c:if test="${cs.routeStopId eq currentStopId}">
                                        <c:set var="currentIndex" value="${csLoop.index}" />
                                    </c:if>
                                </c:forEach>
                                <%-- Passed = displayed before the current stop in the list --%>
                                <c:if test="${loop.index < currentIndex}">
                                    <c:set var="isPassed" value="true" />
                                </c:if>
                                <c:if test="${stop.routeStopId eq currentStopId}">
                                    <c:set var="isCurrent" value="true" />
                                </c:if>
                            </c:if>

                            <tr class="${isCurrent ? 'stop-current' : (isPassed ? 'stop-passed' : 'stop-upcoming')}">
                                <td>${displayOrder}</td>
                                <td>
                                    ${stop.stopName}
                                    <c:if test="${isCurrent && isLast}"> &nbsp;<span class="tag green">FINAL STOP</span></c:if>
                                    <c:if test="${isCurrent && !isLast}"> &nbsp;<span class="tag blue">CURRENT</span></c:if>
                                    <c:if test="${isPassed}"> &nbsp;<span class="tag green">PASSED</span></c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${isCurrent && isLast}">
                                            <span class="tag green">✅ Arrived at Destination</span>
                                        </c:when>
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
                                            <c:when test="${isCurrent && isLast}">
                                                <button type="submit" class="success" disabled="disabled" style="opacity: 0.4;">🏁 Destination</button>
                                            </c:when>
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
                    </tbody>
                </table>
            </div>
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
