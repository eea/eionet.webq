<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<c:choose>
    <c:when test="${projectFile.fileType == 'WEBFORM'}">
        <c:set var="fileTypeLabel" value="WebForm"/>
        <c:set var="isWebform" value="true"/>
    </c:when>
    <c:otherwise>
        <c:set var="fileTypeLabel" value="Project file"/>
    </c:otherwise>
</c:choose>

<h1>Save ${fileTypeLabel}</h1>
    <c:url var="saveUrl" value="/projects/${projectEntry.projectId}/webform/save"/>
    <s:eval expression="T(org.apache.commons.io.FileUtils).byteCountToDisplaySize(projectFile.fileSizeInBytes)" var="humanReadableFileSize"/>
    <f:form modelAttribute="projectFile" action="${saveUrl}" method="post" enctype="multipart/form-data">
        <f:errors path="*" element="div" cssClass="error-msg"/>
        <table class="datatable">
            <tr>
                <th scope="row"><label for="title">Title</label></th>
                <td><f:input path="title" style="width:500px"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="file">${fileTypeLabel}</label></th>
                <td>
                    <f:input path="file" type="file"/>
                </td>
            </tr>
            <c:if test="${not empty projectFile.fileContent}">
                <tr>
                    <th scope="row">Current file</th>
                    <td>
                        <a href="<c:url value="/download/project/${projectEntry.projectId}/file/${projectFile.fileName}"/>">${projectFile.fileName}</a>
                    </td>
                </tr>
                <tr>
                    <th scope="row">Modified</th>
                    <td>
                        <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${projectFile.updated}"/>
                    </td>
                </tr>
                <tr>
                    <th scope="row">Size</th>
                    <td>
                        ${humanReadableFileSize} (${projectFile.fileSizeInBytes} bytes)
                    </td>
                </tr>
            </c:if>
            <tr>
                <th scope="row"><label for="remoteFileUrl">Remote file URL</label></th>
                <td><f:input path="remoteFileUrl" style="width:500px"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="description">Description</label></th>
                <td><f:textarea path="description" cols="60"/></td>
            </tr>
            <c:if test="${isWebform}">
                <tr>
                    <th scope="row"><label for="xmlSchema">Xml Schema</label></th>
                    <td><f:input path="xmlSchema" style="width:500px"/></td>
                </tr>
                <tr>
                    <th scope="row"><label for="newXmlFileName">New xml file name</label></th>
                    <td><f:input path="newXmlFileName" style="width:500px"/></td>
                </tr>
                <tr>
                    <th scope="row"><label for="emptyInstanceUrl">Empty instance XML URL</label></th>
                    <td><f:input path="emptyInstanceUrl" style="width:500px"/></td>
                </tr>
                <tr>
                    <th scope="row"><label for="active">Active</label></th>
                    <td><f:checkbox path="active" id="active"/></td>
                </tr>
                <tr>
                    <th scope="row"><label for="mainForm">Main form</label></th>
                    <td><f:checkbox path="mainForm" id="mainForm"/></td>
                </tr>
            </c:if>
            <tr>
                <th scope="row"><label for="userName">Username</label></th>
                <td><f:input path="userName"/></td>
            </tr>
        </table>
        <f:hidden path="id"/>
        <f:hidden path="fileType"/>
        <input type="submit" value="Save ${fileTypeLabel}"/>
        <input type="button" onclick="window.location = '<c:url value="/projects/${projectEntry.projectId}/view"/>'" value="Cancel"/>
    </f:form>

