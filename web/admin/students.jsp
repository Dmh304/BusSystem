<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Quản lý học sinh</h2>
            <form action="${pageContext.request.contextPath}/admin/students" method="get">
                <input type="text" name="keyword" value="${keyword}" placeholder="Tìm theo mã, tên, phụ huynh">
                <button type="submit">Tìm kiếm</button>
            </form>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>Mã HS</th>
                    <th>Họ tên</th>
                    <th>Lớp</th>
                    <th>Phụ huynh</th>
                    <th>Trạm đón</th>
                    <th>Tuyến</th>
                </tr>
                <c:forEach items="${students}" var="item">
                    <tr>
                        <td>${item.studentCode}</td>
                        <td>${item.fullName}</td>
                        <td>${item.grade}</td>
                        <td>${item.parentName}</td>
                        <td>${item.pickupStopName}</td>
                        <td>${item.routeName}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
