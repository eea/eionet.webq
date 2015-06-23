<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<div id="drop-operations">
    <h2>Operations</h2>
    <ul>
        <li><span><a title="Add webform" href="<c:url value="/projects/${projectEntry.projectId}/webform/add"/>">Add webform</a></span></li>
        <li><span><a title="Add file" href="<c:url value="/projects/${projectEntry.projectId}/file/add"/>">Add file</a></span></li>
        <li><span><a title="Edit project" href="<c:url value="/projects/edit?projectId=${projectEntry.projectId}"/>">Edit project</a></span></li>
        <li><span><a href="#" onclick="removeProject();">Delete project</a></span></li>
        <li><span><a title="Import" href="<c:url value="/projects/import?projectId=${projectEntry.projectId}"/>">Import</a></span></li>
    </ul>
</div>
<h1>Project: ${projectEntry.projectId}</h1>
<p><strong>${projectEntry.description}</strong> (created <fmt:formatDate pattern="dd MMM yyyy" value="${projectEntry.created}" />)</p>
<c:if test="${not empty fileToUpdate}">
    <div class="system-msg">File ${fileToUpdate} could be updated from remote storage.
        <a href="<c:url value="/projects/remote/update/${projectEntry.projectId}/file/${fileToUpdateId}"/>">Click here to update it</a></div>
</c:if>
<c:if test="${empty allProjectFiles}">
    <h2>Project files</h2>
    <div>No project files yet.</div>
</c:if>

<c:forEach var="projectFilesEntry" items="${allProjectFiles}">
    <c:set var="isWebForm" value="${projectFilesEntry.key == 'WEBFORM'}"/>
    <h2>Project <c:out value="${isWebForm ? 'webforms' : 'files'}"/></h2>
    <form action="<c:url value="/projects/${projectEntry.projectId}/webform/remove"/>" method="post">
    <table class="datatable">
        <thead>
            <tr>
                <th></th>
                <th>Title</th>
                <th>File</th>
                <th>Last modified</th>
                <c:if test="${isWebForm}">
                    <th>Active</th>
                    <th>Local</th>
                    <th>Remote</th>
                </c:if>
                <th>Username</th>
                <th>Remote location</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="projectFile" items="${projectFilesEntry.value}">
            <c:set value="view-file-${projectFile.id}" var="popup_id"/>
            <s:eval expression="T(org.apache.commons.io.FileUtils).byteCountToDisplaySize(projectFile.fileSizeInBytes)" var="humanReadableFileSize"/>
            <tr>
                <td><input type="checkbox" name="fileId" value="${projectFile.id}"/></td>
                <td>${projectFile.title}</td>
                <td><a href="#" onclick="view_file($('#${popup_id}'), <c:out value="${isWebForm ? 800 : 600}"/>)">${projectFile.fileName}</a> (${humanReadableFileSize})</td>
                <td><fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${projectFile.updated}" /></td>
                <c:if test="${isWebForm}">
                    <td><input type="checkbox" ${projectFile.active ? 'checked="checked"' : ''} disabled="disabled"/></td>
                    <td><input type="checkbox" ${projectFile.localForm ? 'checked="checked"' : ''} disabled="disabled"/></td>
                    <td><input type="checkbox" ${projectFile.remoteForm ? 'checked="checked"' : ''} disabled="disabled"/></td>
                </c:if>
                <td>${projectFile.userName}</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty projectFile.remoteFileUrl}">
                            <input type="button" title="${projectFile.remoteFileUrl}" onclick="window.location = '<c:url value="/projects/remote/check/updates/${projectEntry.projectId}/file/${projectFile.id}"/>'" value="Check for updates">
                        </c:when>
                        <c:otherwise>
                            No remote file URL
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr class="dialogTemplate">
                <td colspan="5">
                    <div title="File for project '${projectEntry.projectId}'" id="view-file-${projectFile.id}" >
                        <table class="datatable" style="width:100%">
                            <tr>
                                <th scope="row">Title</th>
                                <td>${projectFile.title}</td>
                            </tr>
                            <tr>
                                <th scope="row">File</th>
                                <td>
                                    <c:url var="downloadLink" value="/download/project/${projectEntry.projectId}/file/${projectFile.fileName}"/>
                                    <a href="${downloadLink}">${projectFile.fileName}</a>
                                    <button onclick="viewFileSource('${downloadLink}');">View Source</button>
                                </td>
                            </tr>
                            <tr>
                                <th scope="row">File size</th>
                                <td>${humanReadableFileSize} (${projectFile.fileSizeInBytes} bytes)</td>
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
                            <c:if test="${isWebForm}">
                                <tr>
                                    <th scope="row">New xml file name</th>
                                    <td>${projectFile.newXmlFileName}</td>
                                </tr>
                                <tr>
                                    <th scope="row">Empty instance URL</th>
                                    <td>${projectFile.emptyInstanceUrl}</td>
                                </tr>
                                <tr>
                                    <th scope="row">Active</th>
                                    <td>${projectFile.active}</td>
                                </tr>
                                <tr>
                                    <th scope="row">Local form</th>
                                    <td>${projectFile.localForm}</td>
                                </tr>
                                <tr>
                                    <th scope="row">Remote form</th>
                                    <td>${projectFile.remoteForm}</td>
                                </tr>
                            </c:if>
                            <tr>
                                <th scope="row">Username</th>
                                <td>${projectFile.userName}</td>
                            </tr>
                        </table>
                        <input type="button" onclick="$('#${popup_id}').dialog('close');" value="Close"/>
                        <input type="button" onclick="window.location = '<c:url value="/projects/${projectEntry.projectId}/webform/edit/?fileId=${projectFile.id}"/>'" value="Edit"/>
                        <c:if test="${projectFile.remoteForm}">
                            <input type="button" onclick="webFormOpenDialog(${projectFile.id});return;" value="Open webform">
                        </c:if>
                        <input type="button" onclick="removeFile('${projectFile.id}');" value="Delete"/>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <input type="submit" value="Delete selected files">
    </form>
</c:forEach>

<div id="remove-file" title="Delete project file?" class="dialogTemplate">
    <p>This file will be deleted. Are you sure?</p>
</div>

<div id="remove-project" title="Delete project?" class="dialogTemplate">
    <p>This project and all its files will be deleted. Are you sure?</p>
</div>
<div id="openWebForm" title="Open webform" class="dialogTemplate">
    <form method="GET" action="<c:url value="/webform/test/run"/>">
        <table class="datatable">
            <tr>
                <th scope="row">Instance URL</th>
                <td><input type="text" name="instance" style="width:500px"></td>
            </tr>
            <tr>
                <th scope="row">Request parameters</th>
                <td><input type="text" name="additionalParameters" placeholder="e.g. country=EE&amp;envelope=http://cdr.eionet.europa.eu/..."  style="width:500px"></td>
            </tr>
        </table>
        <input id="webFormId" type="hidden" name="webFormId">
        <input type="submit" value="Open webform">
    </form>
</div>
