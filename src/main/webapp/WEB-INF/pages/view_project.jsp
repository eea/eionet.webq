<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
    table thead tr {
        font-weight: bold;
    }
</style>
<fieldset>
    <legend>Project data</legend>
    <p>
        Project id: ${projectEntry.projectId}
    </p>
    <p>
        Project label: ${projectEntry.description}
    </p>
    <p>
        Project created: ${projectEntry.created}
    </p>
</fieldset>
<fieldset>
    <legend>Project files</legend>
    <c:if test="${not empty allProjectFiles}">
        <table>
            <%--TODO errors--%>
            <thead>
                <tr>
                    <td>Title</td>
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
                    <td><a href="<c:url value="/download/project/${projectEntry.projectId}/file/${projectFile.id}"/>">${projectFile.title}</a></td>
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
    <a href="<c:url value="/projects/${projectEntry.projectId}/webform/add"/>">Add webform</a>
</fieldset>
