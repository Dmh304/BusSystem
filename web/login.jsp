<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="login-page">
    <div class="login-box">
        <h2>Đăng nhập hệ thống BusManagementSystem</h2>
        <jsp:include page="/common/message.jsp"></jsp:include>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <label>Tên đăng nhập</label>
            <input type="text" name="username" required>

            <label>Mật khẩu</label>
            <input type="password" name="password" required>

            <button type="submit">Đăng nhập</button>
        </form>

        <div class="card mt-16">
            <h3>Tài khoản demo</h3>
            <ul>
                <li>admin1 / 123</li>
                <li>manager1 / 123</li>
                <li>driver1 / 123</li>
                <li>parent1 đến parent5 / 123</li>
            </ul>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
