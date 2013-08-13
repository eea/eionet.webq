<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<h2>Upload project</h2>
<f:form modelAttribute="projectEntry" action="add" method="post">
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
                <f:textarea id="label" path="description"/>
            </td>
        </tr>
    </table>
    <input type="submit" value="Add project"/>
</f:form>

<h2>All projects</h2>
<c:if test="${not empty allProjects}">
    <table>
        <thead>
        <tr>
            <th>Id</th>
            <th>Description</th>
            <th>Created</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${allProjects}" var="project">
                <tr>
                    <td>${project.projectId}</td>
                    <td>${project.description}</td>
                    <td>${project.created}</td>
                    <td><a href="<c:url value="/projects/remove?projectId=${project.projectId}"/>">Remove</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
