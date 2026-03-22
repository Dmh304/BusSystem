<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="grid-4">
            <div class="card">
                <h3>Users</h3>
                <div class="stat-number">${userCount}</div>
            </div>
            <div class="card">
                <h3>Students</h3>
                <div class="stat-number">${studentCount}</div>
            </div>
            <div class="card">
                <h3>Active Buses</h3>
                <div class="stat-number">${busCount}</div>
            </div>
            <div class="card">
                <h3>Open Incidents</h3>
                <div class="stat-number">${openIncidentCount}</div>
            </div>
        </div>

        <div class="grid-3">
            <div class="card"><h3>Parent</h3><div class="stat-number">${parentCount}</div></div>
            <div class="card"><h3>Manager</h3><div class="stat-number">${managerCount}</div></div>
            <div class="card"><h3>Driver</h3><div class="stat-number">${driverCount}</div></div>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
