<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>Bus Management</h2>
            <button type="button" onclick="showAddBusForm()" style="padding: 8px 15px; background-color: #28a745; color: white; border: none; cursor: pointer; border-radius: 4px; margin-bottom: 10px;">Add New Bus</button>
            <div id="busFormDiv" style="display: none; background-color: #f8f9fa; border: 1px solid #ddd; padding: 15px; margin-bottom: 20px;">
                <h3 id="busFormTitle" style="margin-top: 0;">Add Bus</h3>
                <form action="${pageContext.request.contextPath}/admin/buses" method="post" style="display: flex; flex-wrap: wrap; gap: 10px; align-items: center;">
                    <input type="hidden" name="action" id="busAction" value="add">
                    <input type="hidden" name="busId" id="busId" value="">
                    
                    <input type="text" name="plateNumber" id="plateNumber" placeholder="Plate Number" required style="padding: 8px;">
                    <input type="text" name="busName" id="busName" placeholder="Bus Name" required style="padding: 8px;">
                    <input type="number" name="capacity" id="capacity" placeholder="Capacity" required style="padding: 8px;">
                    
                    <select name="status" id="busStatus" style="display: none; padding: 8px;">
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="INACTIVE">INACTIVE</option>
                        <option value="MAINTENANCE">MAINTENANCE</option>
                    </select>
                    
                    <button type="submit" style="padding: 8px 15px; background-color: #007bff; color: white; border: none; cursor: pointer; border-radius: 4px;">Save</button>
                    <button type="button" onclick="hideBusForm()" style="padding: 8px 15px; background-color: #6c757d; color: white; border: none; cursor: pointer; border-radius: 4px;">Cancel</button>
                </form>
            </div>
            
            <div id="assignFormDiv" style="display: none; background-color: #e9ecef; border: 1px solid #ced4da; padding: 15px; margin-bottom: 20px;">
                <h3 style="margin-top: 0;">Assign Team to Bus <span id="assignBusPlate"></span></h3>
                <form action="${pageContext.request.contextPath}/admin/buses" method="post" style="display: flex; flex-wrap: wrap; gap: 10px; align-items: center;">
                    <input type="hidden" name="action" value="assign">
                    <input type="hidden" name="busId" id="assignBusId" value="">
                    
                    Route:
                    <select name="routeId" required style="padding: 8px;">
                        <option value="">Select Route</option>
                        <c:forEach items="${routes}" var="r">
                            <option value="${r.routeId}">${r.routeName}</option>
                        </c:forEach>
                    </select>
                    
                    Manager:
                    <select name="managerId" required style="padding: 8px;">
                        <option value="">Select Manager</option>
                        <c:forEach items="${managers}" var="m">
                            <option value="${m.userId}">${m.fullName}</option>
                        </c:forEach>
                    </select>
                    
                    Driver:
                    <select name="driverId" required style="padding: 8px;">
                        <option value="">Select Driver</option>
                        <c:forEach items="${drivers}" var="d">
                            <option value="${d.userId}">${d.fullName}</option>
                        </c:forEach>
                    </select>
                    
                    <button type="submit" style="padding: 8px 15px; background-color: #007bff; color: white; border: none; cursor: pointer; border-radius: 4px;">Assign</button>
                    <button type="button" onclick="hideAssignForm()" style="padding: 8px 15px; background-color: #6c757d; color: white; border: none; cursor: pointer; border-radius: 4px;">Cancel</button>
                </form>
            </div>
            <table>
                <tr>
                    <th>Plate Number</th>
                    <th>Bus Name</th>
                    <th>Capacity</th>
                    <th>Route</th>
                    <th>Manager</th>
                    <th>Driver</th>
                    <th>Status</th>
                    <th>Update</th>
                </tr>
                <c:forEach items="${buses}" var="item">
                    <tr>
                        <td>${item.plateNumber}</td>
                        <td>${item.busName}</td>
                        <td>${item.capacity}</td>
                        <td>${item.routeName}</td>
                        <td>${item.managerName}</td>
                        <td>${item.driverName}</td>
                        <td>${item.status}</td>
                        <td>
                            <button type="button" onclick="showEditBusForm(this)" 
                                data-id="${item.busId}" 
                                data-plate="${item.plateNumber}" 
                                data-name="${item.busName}" 
                                data-capacity="${item.capacity}" 
                                data-status="${item.status}"
                                style="padding: 5px 10px; cursor: pointer; border: none; background-color: #ffc107; border-radius: 4px; margin-bottom: 5px;">Edit</button>
                                
                            <form action="${pageContext.request.contextPath}/admin/buses" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to deactivate this bus?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="busId" value="${item.busId}">
                                <button type="submit" style="padding: 5px 10px; cursor: pointer; border: none; background-color: #dc3545; color: white; border-radius: 4px; margin-bottom: 5px;">Delete</button>
                            </form>
                            <br/>
                            <button type="button" onclick="showAssignForm('${item.busId}', '${item.plateNumber}')" style="padding: 5px 10px; cursor: pointer; border: none; background-color: #17a2b8; color: white; border-radius: 4px;">Assign Team</button>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        
        <script>
        function showAddBusForm() {
            document.getElementById('busFormDiv').style.display = 'block';
            document.getElementById('assignFormDiv').style.display = 'none';
            document.getElementById('busFormTitle').innerText = 'Add Bus';
            document.getElementById('busAction').value = 'add';
            document.getElementById('busId').value = '';
            document.getElementById('plateNumber').value = '';
            document.getElementById('busName').value = '';
            document.getElementById('capacity').value = '';
            document.getElementById('busStatus').style.display = 'none';
        }
        
        function showEditBusForm(btn) {
            document.getElementById('busFormDiv').style.display = 'block';
            document.getElementById('assignFormDiv').style.display = 'none';
            document.getElementById('busFormTitle').innerText = 'Edit Bus';
            document.getElementById('busAction').value = 'edit';
            document.getElementById('busId').value = btn.getAttribute('data-id');
            document.getElementById('plateNumber').value = btn.getAttribute('data-plate');
            document.getElementById('busName').value = btn.getAttribute('data-name');
            document.getElementById('capacity').value = btn.getAttribute('data-capacity');
            document.getElementById('busStatus').value = btn.getAttribute('data-status');
            document.getElementById('busStatus').style.display = 'inline-block';
        }
        
        function showAssignForm(busId, plateNumber) {
            document.getElementById('assignFormDiv').style.display = 'block';
            document.getElementById('busFormDiv').style.display = 'none';
            document.getElementById('assignBusPlate').innerText = plateNumber;
            document.getElementById('assignBusId').value = busId;
        }
        
        function hideBusForm() {
            document.getElementById('busFormDiv').style.display = 'none';
        }
        
        function hideAssignForm() {
            document.getElementById('assignFormDiv').style.display = 'none';
        }
        </script>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
