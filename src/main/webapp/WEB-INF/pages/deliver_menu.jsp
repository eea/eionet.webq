<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Edit with web form</h1>
<c:if test="${not parameters.authorizationSet}">
    <div class="error-msg">Warning! Could not find authentication info from HTTP request. It is not allowed to save data on Web Forms!</div>
</c:if>

<c:choose>
    <c:when test="${empty availableWebForms or empty xmlFiles}">
        <div class="error-msg">No documents were uploaded in this envelope that can be edited with web forms or no web forms available for this envelope!</div>
        <a href="${parameters.envelopeUrl}"title="go back to envelope">Go back to envelope</a>
    </c:when>
    <c:otherwise>
        <c:if test="${parameters.newFormCreationAllowed}">
            <h2>The following web forms are available</h2>
            <c:forEach items="${availableWebForms}" var="webForm">
                <a href="<c:url value="/startWebform?formId=${webForm.id}"/>" title="Fill new form">Fill new ${webForm.title} form</a><br />
            </c:forEach>
        </c:if>
        <h2>Existing data files in this envelope</h2>
        <c:forEach items="${availableWebForms}" var="webForm">
                <c:forEach items="${xmlFiles[webForm.xmlSchema]}" var="file">
                    <c:url var="editLink" value="/cdr/edit/file?formId=${webForm.id}&fileName=${file.title}&remoteFileUrl=${file.fullName}"/>
                    <a href="${editLink}" title="Edit with Web Form">Edit ${file.title} with web form ${webForm.title}</a><br />
                </c:forEach>
        </c:forEach>
    </c:otherwise>
</c:choose>
