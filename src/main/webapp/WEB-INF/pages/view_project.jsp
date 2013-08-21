<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
    table thead tr {
        font-weight: bold;
    }
</style>
<div id="drop-operations">
    <h2>Operations</h2>
    <ul>
        <li><span><a title="Edit project" href="<c:url value="/projects/edit?projectId=${projectEntry.projectId}"/>">Edit project</a></span></li>
        <li><span><a href="<c:url value="/projects/remove?projectId=${projectEntry.projectId}"/>">Remove project</a></span></li>
        <li><span><a title="Add webform" href="<c:url value="/projects/${projectEntry.projectId}/webform/add"/>">Add webform</a></span></li>
    </ul>
</div>
<h1>Project: ${projectEntry.projectId}</h1>
<h4>${projectEntry.description} Created: ${projectEntry.created}</h4>

<fieldset>
    <legend>Project files</legend>
    <c:if test="${not empty allProjectFiles}">
        <table>
            <%--TODO errors--%>
            <thead>
                <tr>
                    <td>Title</td>
                    <td>File</td>
                    <td>Description</td>
                    <td>Xml schema</td>
                    <td>Active?</td>
                    <td>Main form?</td>
                    <td>Username</td>
                    <td>Actions</td>
                </tr>
            </thead>
            <c:forEach items="${allProjectFiles}" var="projectFile">
                <tr>
                    <td>${projectFile.title}</td>
                    <td><a href="<c:url value="/download/project/${projectEntry.projectId}/file/${projectFile.id}"/>">Download</a></td>
                    <td>${projectFile.description}</td>
                    <td>${projectFile.xmlSchema}</td>
                    <td>${projectFile.active}</td>
                    <td>${projectFile.mainForm}</td>
                    <td>${projectFile.userName}</td>
                    <td>
                        <a href="<c:url value="/projects/${projectEntry.projectId}/webform/edit/?fileId=${projectFile.id}"/>">Edit</a>
                        <a href="<c:url value="/projects/${projectEntry.projectId}/webform/remove/?fileId=${projectFile.id}"/>">Remove</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</fieldset>
