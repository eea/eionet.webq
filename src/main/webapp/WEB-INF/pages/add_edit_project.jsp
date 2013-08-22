<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<h1>Save project</h1>
<f:form modelAttribute="projectEntry" action="save" method="post">
    <f:errors path="*" element="div" cssClass="error-msg"/>
    <table>
        <tr>
            <td>
                <label for="projectId">Project id</label>
                <f:input id="projectId" path="projectId" type="text"/>
            </td>
        </tr>
        <tr>
            <td>
                <label for="label">Project label</label>
                <f:input id="label" path="description" size="35"/>
            </td>
        </tr>
    </table>
    <f:hidden path="id"/>
    <input type="submit" value="Save project"/>
    <c:choose>
        <c:when test="${not empty projectEntry.projectId}">
            <c:set var="cancelLink" value="/projects/${projectEntry.projectId}/view"/>
        </c:when>
        <c:otherwise>
            <c:set var="cancelLink" value="/projects/"/>
        </c:otherwise>
    </c:choose>

    <input type="button" onclick="window.location = '<c:url value="${cancelLink}"/>'" value="Cancel"/>
</f:form>