<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h1>Merge modules</h1>
<div id="drop-operations">
    <h2>Operations</h2>
    <ul>
        <li><span><a title="Add module" href="<c:url value="/merge/module/add"/>">Add merge module</a></span></li>
    </ul>
</div>

<c:if test="${not empty allMergeModules}">
    <table class="datatable">
        <thead>
        <tr>
            <th>Title</th>
            <th>Supported XML schemas</th>
            <th>Username</th>
            <th>Created</th>
            <th>Updated</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${allMergeModules}" var="module">
            <tr>
                <td><a href="<c:url value="/merge/module/${module.name}/view"/>">${module.title}</a></td>
                <td><c:forEach items="${module.xmlSchemas}" var="xmlSchema">
                        ${xmlSchema.xmlSchema} <br />
                    </c:forEach>
                </td>
                <td>${module.userName}</td>
                <td><fmt:formatDate pattern="dd MMM yyyy" value="${module.created}" /></td>
                <td><fmt:formatDate pattern="dd MMM yyyy" value="${module.updated}" /></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
