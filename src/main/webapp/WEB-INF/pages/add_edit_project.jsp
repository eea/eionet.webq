<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<h1>Save project</h1>
<f:form modelAttribute="projectEntry" action="save" method="post">
    <f:errors path="*" element="div" cssClass="error-msg"/>
    <table class="datatable">
        <tr>
            <th scope="row">
                <label for="projectId">Project id</label>
            </th>
            <td>
                <f:input id="projectId" path="projectId" type="text"/>
            </td>
        </tr>
        <tr>
            <th scope="row">
                <label for="label">Title</label>
            </th>
            <td>
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