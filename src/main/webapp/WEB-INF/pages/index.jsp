<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<h1>Web Questionnaires</h1>
<p>The system lets you create and edit XML files by using webforms.
<ul>
    <li>By selecting "Start a new webform" your are able to fill in your data on empty webform and the XML file is created automatically. </li>
    <li>If you already have an XML file that you want to edit, then select "Upload XML file". Appropriate actions will be available if your XML file conforms to any of the webform stored in the system.</li>
</ul></p>
<p><strong>After editing your file do not forget to download it!</strong> The files are stored in the system temporarily and they will be deleted if your session expires or if you close your web browser.</p>

<p><input type="button" value="Start a new webform" onclick="showStartWebformArea()"/> or <input type="button" value="Upload XML file" onclick="showUploadXmlArea()"/></p>
<div class="container">
    <c:url var="uploadUrl" value="/uploadXml"/>
    <f:form modelAttribute="uploadForm" action="${uploadUrl}" method="POST" enctype="multipart/form-data">
        <f:errors path="*" element="div" cssClass="error-msg"/>
        <div class="col1" id="startWebformArea">
        <fieldset>
            <legend>Start a new web form</legend>
            <p>
                <label for="selectFile">1. Select the web form</label>
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
    <div class="col2" id="uploadXmlArea">
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
        <div hidden="hidden" class="important-msg" id="not-downloaded-files-present"><strong>Note</strong><p>Please download your modified files!</p></div>
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
                <c:if test="${not file.fromCdr}">
                    <c:url value="/download/user_file?fileId=${file.id}" var="downloadLink"/>
                    <s:eval expression="T(eionet.webq.dao.orm.util.UserFileInfo).isNotUpdatedOrDownloadedAfterUpdateUsingForm(file)"
                        var="downloadedAfterUpdateOrNotChanged"/>
                    <s:eval expression="T(org.apache.commons.io.FileUtils).byteCountToDisplaySize(file.sizeInBytes)" var="humanReadableFileSize"/>
                    <c:set var="id-prefix" value="${file.id}-"/>
                    <tr class="user_file">
                        <td>
                            <input type="checkbox" name="selectedUserFile" value="${file.id}">
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not downloadedAfterUpdateOrNotChanged}">
                                    <strong>${file.name}</strong>
                                </c:when>
                                <c:otherwise>
                                    ${file.name}
                                </c:otherwise>
                            </c:choose>

                        </td>
                        <td>
                            File size: ${humanReadableFileSize}<br/>
                            Created: <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.created}" /><br/>
                            Updated:  <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.updated}" /><br/>
                            Downloaded:  <span id="${id-prefix}downloaded"><c:choose>
                            <c:when test="${not empty file.downloaded}">
                                <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.downloaded}" />
                            </c:when>
                            <c:otherwise>
                                never
                            </c:otherwise>
                            </c:choose>
                            </span>
                        </td>
                        <td>
                            <div class="action">
                                <c:choose>
                                    <c:when test="${not downloadedAfterUpdateOrNotChanged}">
                                        <c:set var="updateNote" value="(NB! updated through web form)"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="updateNote" value=""/>
                                    </c:otherwise>
                                </c:choose>
                            <a href="${downloadLink}" onclick="hideNotDownloadedNote('${id-prefix}');" title="Download file">Download
                                <c:if test="${not empty updateNote}">
                                    <span id="${id-prefix}not-downloaded" class="not-downloaded" style="color:red;text-decoration:none"> ${updateNote}</span></a>
                                </c:if>
                            </div>
                            <c:forEach var="webForm" items="${allWebForms}">
                                <c:if test="${file.xmlSchema eq webForm.xmlSchema}">
                                    <div class="action"><strong><a href="<c:url value="/xform/?formId=${webForm.id}&instance=${downloadLink}&amp;fileId=${file.id}&amp;base_uri=${pageContext.request.contextPath}"/>">Edit
                                        with '${webForm.title}' web form</a></strong></div>
                                </c:if>
                            </c:forEach>
                            <c:if test="${not empty file.availableConversions}">
                            <div class="action">
                                View file as:
                                <ul>
                                <c:forEach items="${file.availableConversions}" var="conversion">
                                    <li><a href="<c:url value="/download/convert?fileId=${file.id}&conversionId=${conversion.id}"/>">${conversion.description}</a></li>
                                </c:forEach>
                                </ul>
                            </div>
                            </c:if>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
        <input type="submit" value="Delete selected files"/>
        </form>
</div>
</c:if>
