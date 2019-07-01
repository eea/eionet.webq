<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<h1>Edit with web form</h1>
<c:if test="${not parameters.authorizationSet}">
    <div class="error-msg">Warning! Could not find authentication info from HTTP request. It is not allowed to save data on Web Forms!</div>
</c:if>

<c:choose>
    <c:when test="${empty availableWebForms}">
        <div class="error-msg">No documents were uploaded in this envelope that can be edited with web forms or no web forms available for this envelope!</div>
    </c:when>
    <c:otherwise>
        <c:if test="${parameters.newFormCreationAllowed}">
            <h2>The following web forms are available</h2>
            <ul>
            <c:forEach items="${availableWebForms}" var="webForm">
                <li><a href="<c:url value="cdr/add/file?formId=${webForm.id}"/>" title="Start a new web form">Create new data file</a> with '<strong>${fn:escapeXml(webForm.title)}</strong>' web form</li>
            </c:forEach>
            </ul>
        </c:if>
        <c:if test="${not empty xmlFiles}">
            <h2>Existing data files in this envelope</h2>
            <ul>
            <c:forEach items="${availableWebForms}" var="webForm">
                <c:forEach items="${xmlFiles[webForm.xmlSchema]}" var="file">
                    <c:url var="editLink" value="cdr/edit/file?formId=${webForm.id}&fileName=${file.title}&remoteFileUrl=${file.fullName}"/>
                    <li><a href="${fn:escapeXml(editLink)}" title="Edit with Web Form">Edit <strong>${fn:escapeXml(file.title)}</strong></a> with '${fn:escapeXml(webForm.title)}' web form</li>
                </c:forEach>
            </c:forEach>
            </ul>
        </c:if>
        <br />
    </c:otherwise>
</c:choose>
<a href="${fn:escapeXml(parameters.envelopeUrl)}" title="Go back to envelope page">Back to envelope</a>
