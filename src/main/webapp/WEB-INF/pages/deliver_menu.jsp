<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Edit with web form</h1>

<c:choose>
    <c:when test="${empty availableWebForms or empty xmlFiles}">
        <div class="error-msg">No documents were uploaded in this envelope that can be edited with web forms or no web forms available for this envelope!</div>
        <a href="${parameters.envelopeUrl}"title="go back to envelope">Go back to envelope</a>
    </c:when>
    <c:otherwise>
        <h2>The following web forms are available</h2>
        <c:forEach items="${availableWebForms}" var="webForm">
            <span>Existing data files in this envelope:</span>
            <table class="dataTable">
                <c:forEach items="${xmlFiles[webForm.xmlSchema]}" var="file">
                    <tr>
                        <th>File name</th>
                        <td>${file.title}</td>
                    </tr>
                    <tr>
                        <th>File link</th>
                        <td>${file.fullName}</td>
                    </tr>
                    <tr>
                        <th>Edit</th>
                        <td><a href="<c:url var="editLink" value="/startWebform?formId=${webForm.id}"/>" title="Edit with Web Form">Edit with web form ${webForm.title}</a></td>
                    </tr>
                </c:forEach>
            </table>
        </c:forEach>
    </c:otherwise>
</c:choose>
