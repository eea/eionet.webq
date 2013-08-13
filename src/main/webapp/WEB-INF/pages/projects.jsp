<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<div class="container">
<f:form modelAttribute="projectEntry" action="add" method="post">
    <f:errors path="*" element="div" cssClass="error-msg"/>
    <fieldset>
        <legend>Upload project</legend>

        <p>
            <f:errors path="id" element="div"/>
            <label for="projectId">Project id</label>
            <f:input id="projectId" path="id" type="text"/>
        </p>

        <p>
            <label for="label">Project label</label>
            <f:textarea id="label" path="description" type="text"/>
        </p>

        <p>
            <input type="submit" value="Add project"/>
        </p>

    </fieldset>
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
                    <td>${project.id}</td>
                    <td>${project.description}</td>
                    <%--TODO created for project--%>
                    <%--<td>${project.created}</td>--%>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
</div>
