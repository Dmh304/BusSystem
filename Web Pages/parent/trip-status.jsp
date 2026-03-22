<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Trạng thái chuyến đi</h2>
            <p>Theo dõi học sinh đã lên xe hay chưa và xe đang ở trạm nào.</p>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>Học sinh</th>
                    <th>Buổi sáng</th>
                    <th>Buổi chiều</th>
                </tr>
                <c:forEach items="${students}" var="student">
                    <tr>
                        <td>
                            <strong>${student.fullName}</strong><br>
                            ${student.grade} - ${student.pickupStopName}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty statusMorningMap[student.studentId]}">
                                    Chưa có manifest sáng
                                </c:when>
                                <c:otherwise>
                                    Trạng thái: <strong>${statusMorningMap[student.studentId].boardingStatus}</strong><br>
                                    Xe đang ở: <strong>${empty statusMorningMap[student.studentId].currentStopName ? 'Chưa xuất phát' : statusMorningMap[student.studentId].currentStopName}</strong>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty statusAfternoonMap[student.studentId]}">
                                    Chưa có manifest chiều
                                </c:when>
                                <c:otherwise>
                                    Trạng thái: <strong>${statusAfternoonMap[student.studentId].boardingStatus}</strong><br>
                                    Xe đang ở: <strong>${empty statusAfternoonMap[student.studentId].currentStopName ? 'Chưa xuất phát' : statusAfternoonMap[student.studentId].currentStopName}</strong>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
