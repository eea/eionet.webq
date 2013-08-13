<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<a href="#">Add new project</a>
<c:if test="not empty ${allProjects}">
    <table>
        <thead>
        <tr>
            <th>Id</th>
            <th>Description</th>
            <th>Created</th>
        </tr>
        </thead>
        <c:forEach items="${allProjects}" var="project">
            <tr>
                <td>${project.id}</td>
                <td>${project.description}</td>
                <td>${project.created}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>
