<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui-1.10.3.custom.min.css"/>">

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<h1>Users - Roles</h1>
<input id="add-role" type="button" href="${contextPath}/users/new" value="Add new User">
<table class="datatable">
    <tr>
        <th>User</th>
        <th>Roles</th>
        <th>Edit User</th>
        <th>Delete User</th>
    </tr>
    <c:forEach var="user" items="${allUsers}">
            <tr>
                <td>${user.username}</td>
                <td>
                    <c:forEach var="authority" items="${user.authorities}" varStatus="loopStatus">
                        ${authority}
                        <c:if test="${!loopStatus.last}"> , </c:if>
                    </c:forEach>
                </td>
                <td>
                    <input class="edit-button" type="button" value="Edit" href="${contextPath}/users/existing?userName=${user.username}">
                </td>
                <td>
                    <input class="delete-button" type="button" value="Delete" href="${contextPath}/users/delete?userName=${user.username}">
                </td>
            </tr>
    </c:forEach>
</table>
<script type="text/javascript">
    (function(){
        $('#add-role, .edit-button').click(function(){
            window.location.assign($(this).attr("href"));
        });
        $('.delete-button').click(function(){
            if (confirm("Are you sure you want to delete the user ?") == true) {
                window.location.assign($(this).attr("href"));
            }
        });
    })();
</script>