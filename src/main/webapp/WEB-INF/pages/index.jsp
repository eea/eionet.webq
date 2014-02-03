<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:set value="${sessionScope.isCoordinator}" var="isCoordinator"/>

<c:choose>
    <c:when test="${isCoordinator}">
        <h1>Merge session files</h1>
        <p>This tool helps you to merge multiple XML files. Either upload XML files separately or multiple files packed into a zip archive. The zip file will be uploaded and unwrapped.<br />
        <p><input type="button" id="uploadButton" value="Upload session file"/></p>
    </c:when>
    <c:otherwise>
        <h1>Web Questionnaires</h1>
        <p>This tool helps gather data for reporting obligations, using web questionnaires predefined by the EEA.<br />
        Data entries are gathered in a session file (in XML format).<br />
        You can:
        </p>
        <ul>
            <li>start a new session, or</li>
            <li>upload a file from a previous session to edit it.</li>
        </ul>
        <p>Do not forget to save your file on your computer, and to do that often! The data will disappear if the session expires or if you close your browser.</p>

        <p><input type="button" value="Start new session" onclick="showStartWebformArea()"/> or <input type="button" id="uploadButton" value="Upload session file"/></p>
    </c:otherwise>
</c:choose>


<div class="container">
    <c:choose>
        <c:when test="${isCoordinator}">
            <c:url var="uploadUrl" value="/uploadXml"/>
        </c:when>
        <c:otherwise>
            <c:url var="uploadUrl" value="/uploadXmlWithRedirect"/>
        </c:otherwise>
    </c:choose>
    <f:form modelAttribute="uploadForm" action="${uploadUrl}" method="POST" enctype="multipart/form-data">
        <f:errors path="*" element="div" cssClass="error-msg"/>
        <div class="col1" id="startWebformArea">
        <fieldset>
            <p>
                <label for="selectFile">Select web form:</label>
                <select id="selectFile" name="selectFile" title="Select new webform">
                    <c:forEach var="form" items="${allWebForms}">
                        <option value="<c:url value="/startWebform?formId=${form.id}"/>">${form.title}</option>
                    </c:forEach>
                </select>
            </p>
            <p>
                <input type="button" value="Start" onclick="window.location=getSelectedFileValue()"/>
            </p>
        </fieldset>
        </div>
        <div class="col2" id="uploadXmlArea">
            <f:input id="userFile" class="hidden" type="file" path="userFiles"/>
            <input id="newFileSubmit" class="hidden" type="submit" value="Upload"/>
        </div>
    </f:form>
</div>
<c:if test="${not empty uploadedFiles}">
<div class="files">
    <h2>My session files</h2>
        <div hidden="hidden" class="important-msg" id="not-downloaded-files-present"><strong>Note</strong><p>Please download your modified files!</p></div>
        <form id="actionForm" method="post" action="<c:url value="/remove/files"/>">
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
                    <s:eval expression="T(eionet.webq.dao.orm.util.UserFileInfo).isNotDownloadedAfterUpdateUsingForm(file)"
                        var="notDownloadedAfterUpdate"/>
                    <c:set var="downloadNotificationsRequired" value="${not isCoordinator and notDownloadedAfterUpdate}"/>
                    <s:eval expression="T(org.apache.commons.io.FileUtils).byteCountToDisplaySize(file.sizeInBytes)" var="humanReadableFileSize"/>
                    <c:set var="idPrefix" value="${file.id}-"/>
                    <tr class="user_file">
                        <td>
                            <input type="checkbox" name="selectedUserFile" value="${file.id}" id="chk-${file.id}"/>
                        </td>
                        <td><label for="chk-${file.id}">
                            <c:choose>
                                <c:when test="${downloadNotificationsRequired}">
                                    <strong>${file.name}</strong>
                                </c:when>
                                <c:otherwise>
                                    ${file.name}
                                </c:otherwise>
                            </c:choose>
                            </label>
                        </td>
                        <td>
                            File size: ${humanReadableFileSize}<br/>
                            Created: <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.created}" /><br/>
                            Updated: <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.updated}" /><br/>
                            Downloaded: <span id="${idPrefix}downloaded"><c:choose>
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
                                    <c:when test="${downloadNotificationsRequired}">
                                        <c:set var="updateNote" value="(NB! updated through web form)"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="updateNote" value=""/>
                                    </c:otherwise>
                                </c:choose>
                            <a href="${downloadLink}" onclick="hideNotDownloadedNote('${idPrefix}');" title="Download file">Download
                                <c:if test="${not empty updateNote}">
                                    <span id="${idPrefix}not-downloaded" class="not-downloaded" style="color:red;text-decoration:none"> ${updateNote}</span>
                                </c:if>
                            </a>
                            </div>
                            <c:forEach var="webForm" items="${allWebForms}">

                                <c:if test="${file.xmlSchema eq webForm.xmlSchema}">
                                    <div class="action"><strong><a href="<c:url value="${webForm.webformLink}&amp;instance=${downloadLink}&amp;fileId=${file.id}&amp;base_uri=${pageContext.request.contextPath}"/>">Edit
                                        with '${webForm.title}' web form</a></strong></div>
                                </c:if>
                            </c:forEach>
                            <sec:authorize access="hasRole('DEVELOPER')" var="isDeveloper"/>
                            <sec:authorize access="hasRole('ADMIN')" var="isAdmin"/>
                            <c:set var="developerOrAdmin" value="${isAdmin or isDeveloper}"/>


                            <c:if test="${not empty file.availableConversions or developerOrAdmin}">
                            <div class="action">
                                View file as:
                                <ul>
                                <c:if test="${not empty file.availableConversions}">
                                    <c:forEach items="${file.availableConversions}" var="conversion">
                                        <li><a href="<c:url value="/download/convert?fileId=${file.id}&amp;conversionId=${conversion.id}"/>">${conversion.description}</a></li>
                                    </c:forEach>
                                </c:if>
                                <c:url value="/download/converted_user_file?fileId=${file.id}" var="conversionDownloadLink"/>
                                <c:if test="${developerOrAdmin}">
                                    <li><a href="#" onclick="showJson('${conversionDownloadLink}')">View as JSON</a></li>
                                    <li><a href="#" onclick="showJsonToXml('${conversionDownloadLink}')">View as XML</a></li>
                                </c:if>
                                </ul>
                            </div>
                            </c:if>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
        <input type="submit" id="removeButton" value="Delete selected files"/>
        <c:if test="${isCoordinator}">
            <input type="button" id="mergeButton" value="Merge selected files"/>
        </c:if>
        </form>
</div>
</c:if>
