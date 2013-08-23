<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<h2>Project files</h2>
<c:if test="${empty allProjectFiles}">
    <div>No project files yet.</div>
</c:if>


<table class="datatable">
    <thead>
        <tr>
            <th>Title</th>
            <th>File</th>
            <th>Last modified</th>
            <th>Active</th>
            <th>Main form</th>
            <th>Username</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach items="${allProjectFiles}" var="projectFile">
        <c:set value="view-file-${projectFile.id}" var="popup_id"/>
        <tr>
            <td>${projectFile.title}</td>
            <td><a href="#" onclick="view_file($('#${popup_id}'))">${projectFile.fileName}</a></td>
            <td><fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${projectFile.updated}" /></td>
            <td><input type="checkbox" checked="${projectFile.active}" disabled="disabled"/></td>
            <td><input type="checkbox" checked="${projectFile.mainForm}" disabled="disabled"/></td>
            <td>${projectFile.userName}</td>
        </tr>
        <tr class="dialogTemplate">
            <td colspan="5">
                <div title="File for project '${projectEntry.projectId}'" id="view-file-${projectFile.id}" >
                    <table class="datatable">
                        <tr>
                            <th scope="row">Title</th>
                            <td>${projectFile.title}</td>
                        </tr>
                        <tr>
                            <th scope="row">File</th>
                            <td><a href="<c:url value="/download/project/${projectEntry.projectId}/file/${projectFile.id}"/>">${projectFile.fileName}</a></td>
                        </tr>
                        <tr>
                            <th scope="row">Last modified</th>
                            <td><fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${projectFile.updated}" /></td>
                        </tr>
                        <tr>
                            <th scope="row">Description</th>
                            <td>${projectFile.description}</td>
                        </tr>
                        <tr>
                            <th scope="row">Xml Schema</th>
                            <td>${projectFile.xmlSchema}</td>
                        </tr>
                        <tr>
                            <th scope="row">Empty instance URL</th>
                            <td>${projectFile.emptyInstanceUrl}</td>
                        </tr>
                        <tr>
                            <th scope="row">New xml file name</th>
                            <td>${projectFile.newXmlFileName}</td>
                        </tr>
                        <tr>
                            <th scope="row">Active</th>
                            <td>${projectFile.active}</td>
                        </tr>
                        <tr>
                            <th scope="row">Main form</th>
                            <td>${projectFile.mainForm}</td>
                        </tr>
                        <tr>
                            <th scope="row">Username</th>
                            <td>${projectFile.userName}</td>
                        </tr>
                        <tr>
                            <th scope="row">Actions</th>
                            <td>
                                <a href="<c:url value="/projects/${projectEntry.projectId}/webform/edit/?fileId=${projectFile.id}"/>">Edit</a>
                                <a href="#" onclick="removeFile('${projectFile.id}');">Remove</a>
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div id="remove-file" title="Remove project file?" class="dialogTemplate">
    <p>This file will be removed. Are you sure?</p>
</div>

<div id="remove-project" title="Remove project?" class="dialogTemplate">
    <p>This project and all its files will be removed. Are you sure?</p>
</div>
