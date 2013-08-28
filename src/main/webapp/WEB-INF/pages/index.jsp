<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript">
    function getSelectedFileValue() {
        var select = document.getElementById('selectFile');
        return select.options[select.selectedIndex].value;
    }
</script>
<style type="text/css">
    .container {
        padding-top:1em;
        width: 100%;
    }
    .col1 {
        float: left;
        width: 50%;
    }
    .col2 {
        float: right;
        width: 50%;
    }
    .container legend{
        font-weight:bold;
    }
    .files{
        padding-top:1em;
        clear:both;
    }
</style>

<h1>Web Questionnaires</h1>
<div class="container">
    <c:url var="uploadUrl" value="/uploadXml"/>
    <f:form modelAttribute="uploadForm" action="${uploadUrl}" method="POST" enctype="multipart/form-data">
        <f:errors path="*" element="div" cssClass="error-msg"/>
        <div class="col1">
        <fieldset>
            <legend>Start a new web form</legend>
            <p>
                <label for="selectFile">1. Select the web form</label><br/>
                <select id="selectFile" name="selectFile" title="Select new webform">
                    <c:forEach var="form" items="${allWebForms}">
                        <option value="<c:url value="/startWebform?formId=${form.id}"/>">${form.title}</option>
                    </c:forEach>
                </select>
            </p>
            <p>
                2. <input type="button" value="Start" onclick="window.location=getSelectedFileValue()"/> to open the web form
            </p>
        </fieldset>
        </div>
    <div class="col2">
        <fieldset>
            <legend>Upload XML file</legend>

            <p>
                <label for="userFile">1. Select the file from My Computer</label>
                <f:input id="userFile" type="file" path="userFile"/>
            </p>

            <p>
                2. <input type="submit" value="Upload"/> XML file and edit it on web form
            </p>

        </fieldset>
    </div>
    </f:form>
</div>
<c:if test="${not empty uploadedFiles}">
<div class="files">
    <h2>My XML files</h2>
        <form method="post" action="<c:url value="/remove/files"/>">
        <table class="datatable" style="width:100%">
            <thead>
            <tr>
                <th scope="col"></th>
                <th scope="col">File</th>
                <th scope="col">File info</th>
                <th scope="col">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${uploadedFiles}" var="file">
                <c:url value="/download/user_file?fileId=${file.id}" var="downloadLink"/>
                <tr>
                    <td>
                        <input type="checkbox" name="selectedUserFile" value="${file.id}">
                    </td>
                    <td>
                        <a href="${downloadLink}" title="Download file">${file.name}</a>
                    </td>
                    <td>
                        File size: ${file.sizeInBytes} bytes<br/>
                        Created: <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.created}" /><br/>
                        Updated:  <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.updated}" />
                    </td>
                    <td>
                        <c:forEach var="webForm" items="${allWebForms}">
                            <c:if test="${file.xmlSchema eq webForm.xmlSchema}">
                                <strong><a href="<c:url value="/xform/?formId=${webForm.id}&instance=${downloadLink}&amp;fileId=${file.id}&amp;base_uri=${pageContext.request.contextPath}"/>">Edit
                                    with '${webForm.title}' web form</a></strong><br/>
                            </c:if>
                        </c:forEach>
                        <c:if test="${not empty file.availableConversions}">
                            View file as:
                            <ul>
                            <c:forEach items="${file.availableConversions}" var="conversion">
                                <li><a href="<c:url value="/download/convert?fileId=${file.id}&conversionId=${conversion.id}"/>">${conversion.description}</a></li>
                            </c:forEach>
                            </ul>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <input type="submit" value="Remove selected files"/>
        </form>
</div>
</c:if>
