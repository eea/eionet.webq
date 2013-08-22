<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<h1>Save webform</h1>
<fieldset>
    <c:url var="saveUrl" value="/projects/${projectEntry.projectId}/webform/save"/>
    <f:form modelAttribute="projectFile" action="${saveUrl}" method="post" enctype="multipart/form-data">
        <f:errors path="*" element="div" cssClass="error-msg"/>
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
            <label for="newXmlFileName">New xml file name(optional)</label>
            <f:input path="newXmlFileName"/>
        </p>
        <p>
            <label for="emptyInstanceUrl">The location of empty instance XML file(optional)</label>
            <f:input path="emptyInstanceUrl"/>
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
        <f:hidden path="id"/>
        <input type="submit" value="Save form"/>
        <input type="button" onclick="window.location = '<c:url value="/projects/${projectEntry.projectId}/view"/>'" value="Cancel"/>
    </f:form>
</fieldset>