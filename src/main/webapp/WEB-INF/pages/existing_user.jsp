<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<h1>Add new user</h1>
<div id="operations">
    <ul>
        <li><a href="${contextPath}/users/view" >back to users</a></li>
    </ul>
</div>
<form action="edit" method="post" modelAttribute="user">
    <table class="datatable">
        <tr>
            <th scope="row"><label for="userName">Username</label></th>
            <td><input id="userName" name="userName" value="${userName}" type="text" readonly></td>
        </tr>
        <tr>
            <th scope="row"><label for="roles">Select user role</label></th>
            <td>
                <c:forEach var="validRole" items="${allRoles}">
                    <input type="radio" name="role" value="${validRole}" <c:if test="${fn:contains(role, validRole)}">checked</c:if> >${validRole}<br/>
                </c:forEach>
            </td>
        </tr>
    </table>
    <input type="submit" value="Edit user">
</form>