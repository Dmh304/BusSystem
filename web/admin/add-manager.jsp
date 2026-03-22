<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Thêm Quản lý mới</h2>
        </div>

        <div class="card">
            <form action="${pageContext.request.contextPath}/admin/add-manager" method="post">
                <input type="hidden" name="action" value="add">
                
                <div>
                    <label for="username">Username:</label>
                    <input type="text" id="username" name="username" required placeholder="Nhập username">
                </div>
                
                <div>
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required placeholder="Nhập mật khẩu">
                </div>
                
                <div>
                    <label for="fullName">Họ tên:</label>
                    <input type="text" id="fullName" name="fullName" required placeholder="Nhập họ tên">
                </div>
                
                <div>
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" required placeholder="Nhập email">
                </div>
                
                <button type="submit" class="success">Thêm Quản lý</button>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="button">Hủy</a>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
