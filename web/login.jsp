<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="login-page">
    <div class="login-box">
        <h2>Login to BusManagementSystem</h2>
        <jsp:include page="/common/message.jsp"></jsp:include>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <label>Username</label>
            <input type="text" name="username" required>

            <label>Password</label>
            <input type="password" name="password" required>

            <button type="submit">Login</button>
        </form>

        <div class="card mt-16">
            <h3>Demo Accounts</h3>
            <ul>
                <li>admin1 / 123</li>
                <li>manager1 / 123</li>
                <li>driver1 / 123</li>
                <li>parent1 to parent5 / 123</li>
            </ul>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
