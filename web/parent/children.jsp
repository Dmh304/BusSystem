<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="page-wrapper">
    <jsp:include page="/common/sidebar.jsp"></jsp:include>
    <div class="content">
        <jsp:include page="/common/message.jsp"></jsp:include>
        <jsp:include page="/common/demo-time-control.jsp"></jsp:include>

        <div class="card">
            <h2>My Children</h2>
            <button type="button" onclick="showAddForm()" style="padding: 8px 15px; background-color: #28a745; color: white; border: none; cursor: pointer; border-radius: 4px;">Add New Child</button>
        </div>

        <div class="card" id="childFormDiv" style="display: none; background-color: #f8f9fa; border: 1px solid #ddd;">
            <h3 id="formTitle" style="margin-top: 0;">Add Child</h3>
            <form action="${pageContext.request.contextPath}/parent/children" method="post">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="studentId" id="studentId" value="">
                <div style="display: flex; flex-wrap: wrap; gap: 10px; align-items: center;">
                    <input type="text" name="studentCode" id="studentCode" placeholder="Student Code" required style="padding: 8px;">
                    <input type="text" name="fullName" id="fullName" placeholder="Full Name" required style="padding: 8px;">
                    <select name="gender" id="gender" required style="padding: 8px;">
                        <option value="">-- Gender --</option>
                        <option value="MALE">MALE</option>
                        <option value="FEMALE">FEMALE</option>
                    </select>
                    <input type="text" name="grade" id="grade" placeholder="Grade" required style="padding: 8px;">
                </div>
                <div style="display: flex; flex-wrap: wrap; gap: 10px; align-items: center; margin-top: 10px;">
                    <select name="routeId" id="routeId" required style="padding: 8px;">
                        <option value="">-- Select Route --</option>
                        <c:forEach items="${routes}" var="r">
                            <option value="${r.routeId}">${r.routeName}</option>
                        </c:forEach>
                    </select>
                    <select name="pickupStopId" id="pickupStopId" required style="padding: 8px;">
                        <option value="">-- Pickup Stop (Home) --</option>
                        <c:forEach items="${stopPoints}" var="sp">
                            <option value="${sp.stopId}">${sp.stopName}</option>
                        </c:forEach>
                    </select>
                    <span style="padding: 8px; color: #666; font-style: italic;">Dropoff = School (auto)</span>
                </div>
                <div style="margin-top: 10px;">
                    <button type="submit" style="padding: 8px 15px; background-color: #007bff; color: white; border: none; cursor: pointer; border-radius: 4px;">Save</button>
                    <button type="button" onclick="hideForm()" style="padding: 8px 15px; background-color: #6c757d; color: white; border: none; cursor: pointer; border-radius: 4px;">Cancel</button>
                </div>
            </form>
        </div>

        <div class="card">
            <table>
                <tr>
                    <th>Student Code</th>
                    <th>Full Name</th>
                    <th>Gender</th>
                    <th>Grade</th>
                    <th>Route</th>
                    <th>Pickup Stop</th>
                    <th>Dropoff Stop</th>
                    <th>Actions</th>
                </tr>
                <c:forEach items="${students}" var="item">
                    <tr>
                        <td>${item.studentCode}</td>
                        <td>${item.fullName}</td>
                        <td>${item.gender}</td>
                        <td>${item.grade}</td>
                        <td>${item.routeName}</td>
                        <td>${item.pickupStopName}</td>
                        <td>${item.dropoffStopName}</td>
                        <td>
                            <button type="button" onclick="showEditForm(this)"
                                data-id="${item.studentId}"
                                data-code="${item.studentCode}"
                                data-fullname="${item.fullName}"
                                data-gender="${item.gender}"
                                data-grade="${item.grade}"
                                data-route="${item.defaultRouteId}"
                                data-pickup="${item.defaultPickupStopId}"
                                style="padding: 5px 10px; cursor: pointer; border: none; background-color: #ffc107; border-radius: 4px;">Edit</button>

                            <form action="${pageContext.request.contextPath}/parent/children" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this child?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="studentId" value="${item.studentId}">
                                <button type="submit" style="padding: 5px 10px; cursor: pointer; border: none; background-color: #dc3545; color: white; border-radius: 4px;">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <script>
        function showAddForm() {
            document.getElementById('childFormDiv').style.display = 'block';
            document.getElementById('formTitle').innerText = 'Add Child';
            document.getElementById('formAction').value = 'add';
            document.getElementById('studentId').value = '';
            document.getElementById('studentCode').value = '';
            document.getElementById('studentCode').readOnly = false;
            document.getElementById('fullName').value = '';
            document.getElementById('gender').value = '';
            document.getElementById('grade').value = '';
            document.getElementById('routeId').value = '';
            document.getElementById('pickupStopId').value = '';
        }

        function showEditForm(btn) {
            document.getElementById('childFormDiv').style.display = 'block';
            document.getElementById('formTitle').innerText = 'Edit Child';
            document.getElementById('formAction').value = 'edit';
            document.getElementById('studentId').value = btn.getAttribute('data-id');
            document.getElementById('studentCode').value = btn.getAttribute('data-code');
            document.getElementById('studentCode').readOnly = true;
            document.getElementById('fullName').value = btn.getAttribute('data-fullname');
            document.getElementById('gender').value = btn.getAttribute('data-gender');
            document.getElementById('grade').value = btn.getAttribute('data-grade');
            document.getElementById('routeId').value = btn.getAttribute('data-route');
            document.getElementById('pickupStopId').value = btn.getAttribute('data-pickup');
        }

        function hideForm() {
            document.getElementById('childFormDiv').style.display = 'none';
        }
        </script>
    </div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
