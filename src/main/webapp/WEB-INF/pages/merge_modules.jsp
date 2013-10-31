<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<div id="drop-operations">
    <h2>Operations</h2>
    <ul>
        <li><span><a title="Add module" href="<c:url value="/merge/module/add"/>">Add merging module</a></span></li>
    </ul>
</div>
<h1>Merging modules</h1>

<c:if test="${not empty allMergeModules}">
    <form action="<c:url value="/merge/modules/remove"/>" method="post">
        <table class="datatable">
            <thead>
            <tr>
                <th></th>
                <th>Title</th>
                <th>Supported XML Schemas</th>
                <th>Username</th>
                <th>Updated</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${allMergeModules}" var="module">
                <c:set var="popup_id" value="merge-module-${module.id}"/>
                <tr>
                    <td><input type="checkbox" value="${module.id}" name="modulesToRemove"></td>
                    <td onclick="module_view('${popup_id}')"><a href="#"><c:out value="${module.title}" escapeXml="true"/></a></td>
                    <td>
                        <c:forEach items="${module.xmlSchemas}" var="xmlSchema">
                            <c:out value="${xmlSchema.xmlSchema}" escapeXml="true"/> <br />
                        </c:forEach>
                    </td>
                    <td>${module.userName}</td>
                    <td><fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${module.updated}" /></td>
                </tr>
                <tr class="dialogTemplate">
                    <td>
                        <div title="File for project '${module.title}'" id="${popup_id}">
                            <table class="datatable" style="width:100%">
                                <tr>
                                    <th scope="row">Title</th>
                                    <td>${module.title}</td>
                                </tr>
                                <tr>
                                    <th scope="row">Xsl file</th>
                                    <td><a href="<c:url value="/download/merge/file/${module.xslFile.name}"/>">${module.xslFile.name}</a></td>
                                </tr>
                                <tr>
                                    <th scope="row">File size</th>
                                    <s:eval expression="T(org.apache.commons.io.FileUtils).byteCountToDisplaySize(module.xslFile.sizeInBytes)" var="humanReadableFileSize"/>
                                    <td>${humanReadableFileSize} (${module.xslFile.sizeInBytes} bytes)</td>
                                </tr>
                                <tr>
                                    <th scope="row">Created</th>
                                    <td>
                                        <fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${module.created}" />
                                    </td>
                                </tr>
                                <tr>
                                    <th scope="row">Last modified</th>
                                    <td><fmt:formatDate pattern="dd MMM yyyy HH:mm:ss" value="${module.updated}" /></td>
                                </tr>
                                <c:forEach var="schema" items="${module.xmlSchemas}">
                                    <tr>
                                        <th>Xml Schema</th>
                                        <td>${schema.xmlSchema}</td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <th scope="row">Username</th>
                                    <td>${module.userName}</td>
                                </tr>
                            </table>
                            <input type="button" onclick="window.location = '<c:url value="/merge/module/edit/${module.id}"/>'" value="Edit"/>
                            <input type="button" onclick="$('#${popup_id}').dialog('close');" value="Close"/>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <input type="submit" value="Remove selected modules">
    </form>
</c:if>
