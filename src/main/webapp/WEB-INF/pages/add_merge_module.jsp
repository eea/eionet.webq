<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<h1>Save merge module</h1>
<f:form modelAttribute="newMergeModule" action="save" method="post" enctype="multipart/form-data">
    <f:errors path="*" element="div" cssClass="error-msg"/>
    <table class="datatable">
        <tr>
            <th scope="row">
                <label for="name">Module name</label>
            </th>
            <td>
                <f:input id="name" path="name" type="text"/>
            </td>
        </tr>
        <tr>
            <th scope="row">
                <label for="title">Module title</label>
            </th>
            <td>
                <f:input id="title" path="title" size="35"/>
            </td>
        </tr>
        <tr>
            <th scope="row">
                <label for="xmlSchemas">Xml schemas</label>
            </th>
            <td id="xmlSchemas">
                <f:input cssStyle="display: block" path="xmlSchemas[0].xmlSchema"/>
            </td>
        </tr>
        <tr>
            <th></th>
            <td>
                <input id="addXmlSchema" type="button" value="Add xml schema field"/>
            </td>
        </tr>
        <tr>
            <th scope="row">
                <label for="xslFile">Xsl file</label>
            </th>
            <td>
                <f:input type="file" id="xslFile" path="xslFile"/>
            </td>
        </tr>
    </table>
    <f:hidden path="id"/>
    <input type="submit" value="Save module"/>
    <input type="button" onclick="window.location = '<c:url value="/merge/modules"/>'" value="Cancel"/>
</f:form>