<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui-1.10.3.custom.min.css"/>">
<style type="text/css">
    table thead tr {
        font-weight: bold;
    }
</style>
<script type="text/javascript">
    function removeDialog(dialogElement, callUrl) {
        dialogElement.dialog({
            resizable: false,
            maxHeight: 300,
            modal: true,
            buttons: {
                "Remove" : function() {
                    window.location = callUrl;
                },
                Cancel: function() {
                    $(this).dialog("close");
                }
            }
        });
    }

    function removeFile(id) {
        removeDialog($("#remove-file"), "<c:url value="/projects/${projectEntry.projectId}/webform/remove/?fileId="/>" + id);
    }

    function removeProject() {
        removeDialog($("#remove-project"), "<c:url value="/projects/remove?projectId=${projectEntry.projectId}"/>");
    }
</script>
<div id="drop-operations">
    <h2>Operations</h2>
    <ul>
        <li><span><a title="Edit project" href="<c:url value="/projects/edit?projectId=${projectEntry.projectId}"/>">Edit project</a></span></li>
        <li><span><a href="#" onclick="removeProject();">Remove project</a></span></li>
        <li><span><a title="Add webform" href="<c:url value="/projects/${projectEntry.projectId}/webform/add"/>">Add webform</a></span></li>
    </ul>
</div>
<h1>Project: ${projectEntry.projectId}</h1>
<p><strong>${projectEntry.description}</strong> (created <fmt:formatDate pattern="dd MMM yyyy" value="${projectEntry.created}" />)</p>

    <h2>Webforms</h2>
    <c:if test="${not empty allProjectFiles}">
        <table class="datatable">
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
                    <td><a href="<c:url value="/download/project/${projectEntry.projectId}/file/${projectFile.id}"/>">${projectFile.fileName}</a></td>
                    <td>${projectFile.description}</td>
                    <td>${projectFile.xmlSchema}</td>
                    <td>${projectFile.active}</td>
                    <td>${projectFile.mainForm}</td>
                    <td>${projectFile.userName}</td>
                    <td>
                        <a href="<c:url value="/projects/${projectEntry.projectId}/webform/edit/?fileId=${projectFile.id}"/>">Edit</a>
                        <a href="#" onclick="removeFile(${projectFile.id});">Remove</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

<div id="remove-file" title="Remove project file?" style="display: none;">
    <p>This file will be removed. Are you sure?</p>
</div>

<div id="remove-project" title="Remove project?" style="display: none;">
    <p>This project and all its files will be removed. Are you sure?</p>
</div>
