<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="drop-operations">
    <h2>Operations</h2>
    <ul>
        <li><span><a title="Add project" href="<c:url value="/projects/add"/>">Add project</a></span></li>
    </ul>
</div>
<h1>Webform projects</h1>
<c:if test="${not empty allProjects}">
    <table class="datatable">
        <thead>
        <tr>
            <th>Id</th>
            <th>Title</th>
            <th>Created</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${allProjects}" var="project">
                <tr>
                    <td><a href="<c:url value="/projects/${fn:escapeXml(project.projectId)}/view"/>">${fn:escapeXml(project.projectId)}</a></td>
                    <td>${fn:escapeXml(project.description)}</td>
                    <td><fmt:formatDate pattern="dd MMM yyyy" value="${project.created}" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>