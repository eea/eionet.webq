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
    <form action="<c:url value="/merge/modules/remove"/>" method="post">
        <table class="datatable">
            <thead>
            <tr>
                <th></th>
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
                    <td><input type="checkbox" value="${module.id}" name="modulesToRemove"></td>
                    <td><a href="<c:url value="/merge/module/edit/${module.id}"/>"><c:out value="${module.title}" escapeXml="true"/></a></td>
                    <td>
                        <c:forEach items="${module.xmlSchemas}" var="xmlSchema">
                            <c:out value="${xmlSchema.xmlSchema}" escapeXml="true"/> <br />
                        </c:forEach>
                    </td>
                    <td>${module.userName}</td>
                    <td><fmt:formatDate pattern="dd MMM yyyy" value="${module.created}" /></td>
                    <td><fmt:formatDate pattern="dd MMM yyyy" value="${module.updated}" /></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <input type="submit" value="Remove selected modules">
    </form>
</c:if>
