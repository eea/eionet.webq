<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<h1>Import project files</h1>
<c:url var="importUrl" value="/projects/${projectEntry.projectId}/import"/>
<f:form id="frmSubmitImportProjects" modelAttribute="httpFileInfo" action="${importUrl}" method="post" enctype="multipart/form-data">
    <f:errors path="*" element="div" cssClass="error-msg"/>
    <table class="datatable">
        <tr>
            <th scope="row">
                <label for="file">Archive</label>
            </th>
            <td>
                <f:input path="content" type="file"/>
            </td>
        </tr>
    </table>
    <input id="btnSubmitImportProjectsForm" type="button" value="Save ${fileTypeLabel}"/>
    <input type="button" onclick="window.location = '<c:url value="/projects/${projectEntry.projectId}/view"/>'" value="Cancel"/>
</f:form>
