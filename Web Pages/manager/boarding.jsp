<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
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
            <h2>Cập nhật boarding - ${selectedSession}</h2>
            <p>Được cập nhật: <strong>${canUpdate ? 'YES' : 'NO'}</strong></p>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>HS</th>
                    <th>Trạm đón</th>
                    <th>Lựa chọn</th>
                    <th>Boarding hiện tại</th>
                    <th>Hành động</th>
                </tr>
                <c:forEach items="${students}" var="item">
                    <tr>
                        <td>${item.studentName}</td>
                        <td>${item.pickupStopName}</td>
                        <td>${item.attendanceChoice}</td>
                        <td>${item.boardingStatus}</td>
                        <td>
                            <c:if test="${item.attendanceChoice eq 'BUS'}">
                                <form action="${pageContext.request.contextPath}/manager/boarding" method="post" class="inline-form">
                                    <input type="hidden" name="manifestStudentId" value="${item.manifestStudentId}">
                                    <input type="hidden" name="sessionType" value="${selectedSession}">
                                    <input type="hidden" name="boardingStatus" value="BOARDED">
                                    <button type="submit" class="success" ${!canUpdate ? 'disabled="disabled"' : ''}>BOARDED</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/manager/boarding" method="post" class="inline-form">
                                    <input type="hidden" name="manifestStudentId" value="${item.manifestStudentId}">
                                    <input type="hidden" name="sessionType" value="${selectedSession}">
                                    <input type="hidden" name="boardingStatus" value="NO_SHOW">
                                    <button type="submit" class="warn" ${!canUpdate ? 'disabled="disabled"' : ''}>NO_SHOW</button>
                                </form>
                            </c:if>
                            <c:if test="${item.attendanceChoice ne 'BUS'}">
                                Không cần điểm danh
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
