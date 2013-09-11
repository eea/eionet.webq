<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Add/Replace user role</h1>

<form action="add" method="post" modelAttribute="user">
    <table class="datatable">
        <tr>
            <th scope="row"><label for="userName">Username</label></th>
            <td><input id="userName" name="userName" type="text"/></td>
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
    <input type="submit" value="Add/Update role"/>
</form>