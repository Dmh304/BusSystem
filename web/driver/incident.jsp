<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Báo cáo sự cố</h2>
            <form action="${pageContext.request.contextPath}/driver/incident" method="post">
                <input type="hidden" name="sessionType" value="${empty param.sessionType ? 'MORNING' : param.sessionType}">
                <label>Tiêu đề</label>
                <input type="text" name="title" required>
                <label>Mô tả</label>
                <textarea name="description" required></textarea>
                <button type="submit">Gửi sự cố</button>
            </form>
        </div>

        <div class="card">
            <h3>Lịch sử sự cố</h3>
            <table>
                <tr>
                    <th>Tiêu đề</th>
                    <th>Mô tả</th>
                    <th>Trạng thái</th>
                    <th>Báo cáo lúc</th>
                </tr>
                <c:forEach items="${incidents}" var="item">
                    <tr>
                        <td>${item.title}</td>
                        <td>${item.description}</td>
                        <td>${item.incidentStatus}</td>
                        <td>${item.reportedAt}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
