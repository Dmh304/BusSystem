<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="session-tabs">
            <a class="${selectedSession eq 'MORNING' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/boarding?sessionType=MORNING">MORNING</a>
            <a class="${selectedSession eq 'AFTERNOON' ? 'active' : ''}" href="${pageContext.request.contextPath}/manager/boarding?sessionType=AFTERNOON">AFTERNOON</a>
        </div>

        <div class="card">
            <h2>Boarding Update - ${selectedSession}</h2>
            <p>Can Update: <span class="tag ${canUpdate ? 'green' : 'red'}"><strong>${canUpdate ? 'YES' : 'NO'}</strong></span></p>
            <c:if test="${currentStopOrder > 0}">
                <p>Bus at stop order: <strong>${currentStopOrder}</strong> &mdash; Only students at reached stops can board.</p>
            </c:if>
            <c:if test="${currentStopOrder == 0}">
                <p><span class="tag orange">Trip not started yet. Start the trip in Tracking before boarding students.</span></p>
            </c:if>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>Student</th>
                    <th>Pickup Stop</th>
                    <th>Choice</th>
                    <th>Current Status</th>
                    <th>Actions</th>
                </tr>
                <c:forEach items="${students}" var="item">
                    <c:set var="stopReached" value="${currentStopOrder >= item.pickupStopOrder && item.pickupStopOrder > 0}" />
                    <tr style="${!stopReached && item.attendanceChoice eq 'BUS' ? 'opacity: 0.5;' : ''}">
                        <td>${item.studentName}</td>
                        <td>${item.pickupStopName}</td>
                        <td>${item.attendanceChoice}</td>
                        <td>
                            <c:choose>
                                <c:when test="${item.boardingStatus eq 'BOARDED'}">
                                    <span class="status-boarded">BOARDED</span>
                                </c:when>
                                <c:when test="${item.boardingStatus eq 'NO_SHOW'}">
                                    <span class="status-noshow">NO_SHOW</span>
                                </c:when>
                                <c:when test="${item.boardingStatus eq 'PENDING'}">
                                    <span class="status-pending">PENDING</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag gray">${item.boardingStatus}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${item.attendanceChoice eq 'BUS'}">
                                <c:choose>
                                    <c:when test="${!stopReached}">
                                        <span class="tag gray">Bus not at this stop yet</span>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="action-buttons">
                                            <form action="${pageContext.request.contextPath}/manager/boarding" method="post" class="inline-form">
                                                <input type="hidden" name="manifestStudentId" value="${item.manifestStudentId}">
                                                <input type="hidden" name="sessionType" value="${selectedSession}">
                                                <input type="hidden" name="boardingStatus" value="BOARDED">
                                                <button type="submit" class="success"
                                                    ${!canUpdate || item.boardingStatus eq 'BOARDED' ? 'disabled="disabled"' : ''}
                                                    style="${item.boardingStatus eq 'BOARDED' ? 'opacity: 0.4; cursor: not-allowed;' : ''}">
                                                    BOARDING
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/manager/boarding" method="post" class="inline-form">
                                                <input type="hidden" name="manifestStudentId" value="${item.manifestStudentId}">
                                                <input type="hidden" name="sessionType" value="${selectedSession}">
                                                <input type="hidden" name="boardingStatus" value="NO_SHOW">
                                                <button type="submit" class="danger"
                                                    ${!canUpdate || item.boardingStatus eq 'NO_SHOW' ? 'disabled="disabled"' : ''}
                                                    style="${item.boardingStatus eq 'NO_SHOW' ? 'opacity: 0.4; cursor: not-allowed;' : ''}">
                                                    NO_SHOW
                                                </button>
                                            </form>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <c:if test="${item.attendanceChoice ne 'BUS'}">
                                <span class="status-notneeded">Not Required</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
