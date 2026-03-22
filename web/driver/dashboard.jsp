<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="grid-3">
            <div class="card">
                <h3>Assigned Bus</h3>
                <div class="stat-number">${empty bus ? '-' : bus.plateNumber}</div>
                <div class="small">${empty bus ? '' : bus.busName}</div>
            </div>
            <div class="card">
                <h3>Bus Status</h3>
                <div class="stat-number">${empty bus ? '-' : bus.status}</div>
            </div>
            <div class="card">
                <h3>Open Incidents</h3>
                <div class="stat-number">${openIncidentCount}</div>
            </div>
        </div>

        <div class="card">
            <h3>Current Trip</h3>
            <p>Session: ${selectedSession}</p>
            <p>Manifest status: ${empty manifest ? 'N/A' : manifest.manifestStatus}</p>
            <p>Current stop: ${empty manifest.currentStopName ? 'Not Departed' : manifest.currentStopName}</p>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
