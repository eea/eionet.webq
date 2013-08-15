<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<a href="<c:url value="/projects/add"/>">Add project</a>

<h2>All projects</h2>
<c:if test="${not empty allProjects}">
    <table>
        <thead>
        <tr>
            <th>Id</th>
            <th>Description</th>
            <th>Created</th>
            <th colspan="2">Actions</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${allProjects}" var="project">
                <tr>
                    <td>${project.projectId}</td>
                    <td>${project.description}</td>
                    <td>${project.created}</td>
                    <td><a href="<c:url value="/projects/${project.projectId}/view"/>">View</a></td>
                    <td><a href="<c:url value="/projects/edit?projectId=${project.projectId}"/>">Edit</a></td>
                    <td><a href="<c:url value="/projects/remove?projectId=${project.projectId}"/>">Remove</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
