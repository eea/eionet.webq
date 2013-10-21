<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<h1>Save merging module</h1>
<c:url value="/merge/module/save" var="actionUrl"/>
<f:form id="saveModule" modelAttribute="mergeModule" action="${actionUrl}" method="post" enctype="multipart/form-data">
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
                <label for="xmlSchemas">XML Schemas</label>
            </th>
            <td id="xmlSchemas">
                <c:url value="/images/delete.gif" var="imageLink"/>
                <c:set var="removeLink"
                       value="<a class=\"removeSchema\" href=\"#\"><img alt=\"Remove schema\" title=\"Remove schema\" src=\"${imageLink}\"></a>"/>
                <c:choose>
                    <c:when test="${empty mergeModule.xmlSchemas}">
                        <div style="display: block;">
                            <f:input size="50" path="xmlSchemas[0].xmlSchema"/> ${removeLink}
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach varStatus="status" items="${mergeModule.xmlSchemas}">
                                <div style="display: block;">
                                    <f:input size="50" path="xmlSchemas[${status.index}].xmlSchema"/> ${removeLink}
                                </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th></th>
            <td>
                <input id="addXmlSchema" type="button" value="Add another XML Schema"/>
            </td>
        </tr>
        <c:if test="${mergeModule.xslFile.sizeInBytes gt 0}">
            <tr>
                <th scope="row">Current file</th>
                <td>
                    <s:eval expression="T(org.apache.commons.io.FileUtils).byteCountToDisplaySize(mergeModule.xslFile.sizeInBytes)" var="humanReadableFileSize"/>
                    <a href="<c:url value="/download/merge/file/${mergeModule.name}"/>">${mergeModule.xslFile.name}</a>
                </td>
            </tr>
            <tr>
                <th scope="row">Modified</th>
                <td>
                    <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${mergeModule.updated}"/>
                </td>
            </tr>
            <tr>
                <th scope="row">Size</th>
                <td>
                        ${humanReadableFileSize} (${mergeModule.xslFile.sizeInBytes} bytes)
                </td>
            </tr>
        </c:if>
        <tr>
            <th scope="row">
                <label for="xslFile">XSL file</label>
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