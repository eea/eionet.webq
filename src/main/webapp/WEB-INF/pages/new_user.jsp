<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form action="add" method="post" modelAttribute="user">
    <label for="userName">Username</label>
    <input id="userName" name="userName" type="text"/>
    <label for="roles">Select role</label>
    <select id="roles" name="role" title="Select role">
        <c:forEach var="role" items="${allRoles}">
            <option value="${role}">${role}</option>
        </c:forEach>
    </select>
    <input type="submit" value="Add/Update role"/>
</form>