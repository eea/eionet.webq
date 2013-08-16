<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
    table thead tr {
        font-weight: bold;
    }
</style>
<fieldset>
    <legend>Project data</legend>
    <p>
        Project id: ${projectEntry.projectId}
    </p>
    <p>
        Project label: ${projectEntry.description}
    </p>
    <p>
        Project created: ${projectEntry.created}
    </p>
</fieldset>
<fieldset>
    <legend>Project files</legend>
    <c:if test="${not empty allProjectFiles}">
        <table>
            <%--TODO errors--%>
            <thead>
                <tr>
                    <td>Title</td>
                    <td>Description</td>
                    <td>Xml schema</td>
                    <td>Active?</td>
                    <td>Main form?</td>
                    <td>Username</td>
                    <td>Actions</td>
                </tr>
            </thead>
            <c:forEach items="${allProjectFiles}" var="projectFile">
                <tr>
                    <td>${projectFile.title}</td>
                    <td>${projectFile.description}</td>
                    <td>${projectFile.xmlSchema}</td>
                    <td>${projectFile.active}</td>
                    <td>${projectFile.mainForm}</td>
                    <td>${projectFile.userName}</td>
                    <td><a href="<c:url value="/projects/${projectEntry.projectId}/webform/remove/?fileId=${projectFile.id}"/>">Remove</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</fieldset>

<fieldset>
    <legend>New webform</legend>
    <f:form commandName="webFormUpload" action="webform/new" method="post" enctype="multipart/form-data">
        <p>
            <label for="title">Title</label>
            <f:input path="title"/>
        </p>
        <p>
            <label for="file">Web form</label>
            <f:input path="file" type="file"/>
        </p>
        <p>
            <label for="description">Description</label>
            <f:input path="description"/>
        </p>
        <p>
            <label for="xmlSchema">Xml schema(optional)</label>
            <f:input path="xmlSchema"/>
        </p>
        <p>
            <label for="active">Active?</label>
            <f:checkbox path="active"/>
        </p>
        <p>
            <label for="mainForm">Main form?</label>
            <f:checkbox path="mainForm"/>
        </p>
        <p>
            <label for="userName">Username</label>
            <f:input path="userName"/>
        </p>
        <input type="submit" value="Add new form"/>
    </f:form>
</fieldset>