<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<h2>Save project</h2>
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
                <f:textarea id="label" path="description"/>
            </td>
        </tr>
    </table>
    <f:hidden path="id"/>
    <input type="submit" value="Save project"/>
</f:form>