<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="drop-operations">
    <h2>Operations</h2>
    <ul>
        <li><span><a title="Add project" href="<c:url value="/projects/add"/>">Add project</a></span></li>
    </ul>
</div>
<h1>All projects</h1>
<c:if test="${not empty allProjects}">
    <table>
        <thead>
        <tr>
            <th>Id</th>
            <th>Description</th>
            <th>Created</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${allProjects}" var="project">
                <tr>
                    <td><a href="<c:url value="/projects/${project.projectId}/view"/>">${project.projectId}</a></td>
                    <td>${project.description}</td>
                    <td>${project.created}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
