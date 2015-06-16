<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
            <td><input id="userName" name="userName" value="${userName}" type="text" readonly/></td>
        </tr>
        <tr>
            <th scope="row"><label for="roles">Select user role</label></th>
            <td>
                <select id="roles" name="role" title="Select role">
                    <c:forEach var="role" items="${allRoles}">
                        <option value="${role}">${role}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
    </table>
    <input type="submit" value="Edit user"/>
</form>