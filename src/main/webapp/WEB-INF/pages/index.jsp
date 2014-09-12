<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${!empty userFileList.userFiles}">
    <script>
        $(function () {
            popup('edit-popup', 800, 600);
        });
    </script>
</c:if>

        <h1>Web Questionnaires</h1>
        <p>This tool helps gather data for reporting obligations, using web questionnaires predefined by the EEA.
        Data entries are gathered in a session file (in XML format).<br />
        You can:
        </p>
        <ul>
            <li>start a new session, or</li>
            <li>upload a file from a previous session to edit it, or</li>
            <li>merge multiple XML files by either uploading XML files separately or multiple files packed into a zip archive.<br />
        </ul>
        <p>Do not forget to save your file on your computer, and to do that often! The data will disappear if the session expires or if you close your browser.</p>

        <p><input type="button" value="Create new session file" onclick="showStartWebformArea()"/> or <input type="button" id="uploadButton" value="Upload session file"/></p>


<div class="container">
    <c:url var="uploadUrl" value="/uploadXml"/>
    <f:form modelAttribute="uploadForm" action="${uploadUrl}" method="POST" enctype="multipart/form-data">
        <f:errors path="*" element="div" cssClass="error-msg"/>
        <div class="col1" id="startWebformArea">
        <fieldset>
            <p>
                <label for="selectFile">Select file type:</label>
                <select id="selectFile" name="selectFile" title="Select new webform">
                    <c:forEach var="form" items="${allWebForms}">
                        <option value="<c:url value="/startWebform?formId=${form.id}"/>">${form.title}</option>
                    </c:forEach>
                </select>
            </p>
            <p>
                <input type="button" value="Save file in session" onclick="window.location=getSelectedFileValue()"/>
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
            <colgroup>
                <col style="width:30px"/>
                <col/>
                <col/>
                <col style="width:220px"/>
                <col style="width:30px;text-align:center"/>
            </colgroup>
            <thead>
            <tr>
                <th scope="col"></th>
                <th scope="col">File</th>
                <th scope="col">Actions</th>
                <th scope="col">Last modified</th>
                <th scope="col"></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${uploadedFiles}" var="file">
                <c:if test="${not file.fromCdr}">
                    <c:url value="/download/user_file?fileId=${file.id}" var="downloadLink"/>
                    <s:eval expression="T(eionet.webq.dao.orm.util.UserFileInfo).isNotDownloadedAfterUpdateUsingForm(file)"
                        var="notDownloadedAfterUpdate"/>
                    <c:set var="downloadNotificationsRequired" value="${notDownloadedAfterUpdate}"/>
                    <s:eval expression="T(org.apache.commons.io.FileUtils).byteCountToDisplaySize(file.sizeInBytes)" var="humanReadableFileSize"/>
                    <c:set var="idPrefix" value="${file.id}-"/>
                    <tr class="user_file">
                        <td>
                            <input type="checkbox" name="selectedUserFile" value="${file.id}" id="chk-${file.id}"/>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${fn:endsWith(file.name, '.xml')}">
                                    <c:set var="fileType" value="link-xml"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="fileType" value="link-unknown"/>
                                </c:otherwise>
                            </c:choose>
                            <span class="file-download ${fileType}">
                                <c:choose>
                                    <c:when test="${downloadNotificationsRequired}">
                                        <c:set var="updateNote" value="(Updated through web form)"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="updateNote" value=""/>
                                    </c:otherwise>
                                </c:choose>
                                <a href="${downloadLink}" onclick="hideNotDownloadedNote('${idPrefix}');" title="Download file">
                                    ${file.name}<img alt="Download" src="<c:url value='/images/download-file.png'/>">
                                    <c:if test="${not empty updateNote}">
                                        <br/><span id="${idPrefix}not-downloaded" class="not-downloaded" style="color:red;text-decoration:none"> ${updateNote}</span>
                                    </c:if>
                                </a>
                            </span>
                        </td>
                        <td>
                            <c:forEach var="webForm" items="${allWebForms}">

                                <c:if test="${file.xmlSchema eq webForm.xmlSchema}">
                                    <div class="action"><strong><a href="<c:url value="${webForm.webformLink}&amp;instance=${downloadLink}&amp;fileId=${file.id}&amp;base_uri=${pageContext.request.contextPath}"/>">Edit
                                        with '${webForm.title}' web form</a></strong></div>
                                </c:if>
                            </c:forEach>
                            <sec:authorize access="hasRole('DEVELOPER')" var="isDeveloper"/>
                            <sec:authorize access="hasRole('ADMIN')" var="isAdmin"/>
                            <c:set var="developerOrAdmin" value="${isAdmin or isDeveloper}"/>
                            <c:set var="advancedConversionFound" value="${false}"/>

                            <c:if test="${not empty file.availableConversions or developerOrAdmin}">
                            <div class="action">
                                View file as:
                                <ul>
                                    <c:if test="${not empty file.availableConversions}">
                                        <c:forEach items="${file.availableConversions}" var="conversion">
                                            <c:choose>
                                                <c:when test="${conversion.basic}">
                                                    <li><a href="<c:url value="/download/convert?fileId=${file.id}&amp;conversionId=${conversion.id}"/>">${empty conversion.description ? conversion.resultType : conversion.description}</a></li>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="advancedConversionFound" value="${true}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </c:if>
                                </ul>
                                <c:if test="${developerOrAdmin or advancedConversionFound}">
                                    <div class="advanced-conversions-toggle" id="act-${file.id}" title="Advanced conversions">...
                                        <!-- iterate for advanced conversions first -->
                                        <c:if test="${not empty file.availableConversions and advancedConversionFound}">
                                            <c:forEach items="${file.availableConversions}" var="conversion">
                                                <c:if test="${!conversion.basic}">
                                                    <li><a href="<c:url value="/download/convert?fileId=${file.id}&amp;conversionId=${conversion.id}"/>">${empty conversion.description ? conversion.resultType : conversion.description}</a></li>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>
                                        <!-- add developer and admin options -->
                                        <c:if test="${developerOrAdmin}">
                                            <c:url value="/download/converted_user_file?fileId=${file.id}" var="conversionDownloadLink"/>
                                            <div class="advanced-conversions" id="advanced-conversions-${file.id}">
                                                <ul>
                                                    <li><a href="#" onclick="showJson('${conversionDownloadLink}')">View as JSON</a></li>
                                                    <li><a href="#" onclick="showJsonToXml('${conversionDownloadLink}')">View as XML</a></li>
                                                </ul>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:if>
                            </div>
                            </c:if>
                        </td>
                        <td>
                            <div class="info-container">
                                <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.updated}" />
                                <div class="info-area" id="info-area-${file.id}">
                                    File size: ${humanReadableFileSize}<br/>
                                    Created: <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.created}" /><br/>
                                    Downloaded: <span id="${idPrefix}downloaded"><c:choose>
                                    <c:when test="${not empty file.downloaded}">
                                        <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${file.downloaded}" />
                                    </c:when>
                                    <c:otherwise>
                                        never
                                    </c:otherwise>
                                </c:choose>
                                    </span>
                                </div>
                            </div>
                        </td>
                        <td><a class="info-toggle" id="${file.id}" title="File info"><img alt="File info" src="<c:url value='/images/info_icon.gif'/>"></a></td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
        <input type="submit" id="removeButton" value="Delete selected files"/>
        <input type="button" id="mergeButton" value="Merge selected files"/>
        <input type="button" id="editButton" value="Rename"/>
        </form>
</div>
</c:if>

<div class="dialogTemplate" id="edit-popup">
    <h2>Rename file<c:if test="${fn:length(userFileList.userFiles) gt 1}">s</c:if></h2>
    <c:url var="saveUrl" value="/save"/>
    <f:form id="editUserFileForm" modelAttribute="userFileList" action="${saveUrl}" method="POST" enctype="multipart/form-data" class="renameForm">
        <f:errors path="*" element="div" cssClass="error-msg"/>
        <c:forEach items="${userFileList.userFiles}" varStatus="vs">
            <table class="datatable">
                <tr>
                    <th scope="row"><label for="userFiles[${vs.index}].name">Name</label></th>
                    <td><f:input path="userFiles[${vs.index}].name" style="width:500px" class="required fileName"/></td>
                </tr>
            </table>
            <f:hidden path="userFiles[${vs.index}].id" />
        </c:forEach>
        <input type="submit" value="Save"/>
        <input type="button" onclick="$('#edit-popup').dialog('close');" value="Close"/>
    </f:form>
</div>
