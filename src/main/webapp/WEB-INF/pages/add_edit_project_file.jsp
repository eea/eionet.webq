<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<h1>Save webform</h1>
    <c:url var="saveUrl" value="/projects/${projectEntry.projectId}/webform/save"/>
    <f:form modelAttribute="projectFile" action="${saveUrl}" method="post" enctype="multipart/form-data">
        <f:errors path="*" element="div" cssClass="error-msg"/>
        <table class="datatable">
            <tr>
                <th scope="row"><label for="title">Title</label></th>
                <td><f:input path="title" style="width:500px"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="file">Web form</label></th>
                <td><f:input path="file" type="file"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="description">Description</label></th>
                <td><f:textarea path="description" cols="60"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="xmlSchema">Xml Schema</label></th>
                <td><f:input path="xmlSchema" style="width:500px"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="newXmlFileName">New xml file name</label></th>
                <td><f:input path="newXmlFileName" style="width:500px"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="emptyInstanceUrl">Empty instance XML URL</label></th>
                <td><f:input path="emptyInstanceUrl" style="width:500px"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="active">Active</label></th>
                <td><f:checkbox path="active" id="active"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="mainForm">Main form</label></th>
                <td><f:checkbox path="mainForm" id="mainForm"/></td>
            </tr>
            <tr>
                <th scope="row"><label for="userName">Username</label></th>
                <td><f:input path="userName"/></td>
            </tr>
        </table>
        <f:hidden path="id"/>
        <input type="submit" value="Save form"/>
        <input type="button" onclick="window.location = '<c:url value="/projects/${projectEntry.projectId}/view"/>'" value="Cancel"/>
    </f:form>

