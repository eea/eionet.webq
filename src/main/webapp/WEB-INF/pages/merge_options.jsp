<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Merge options</h1>

<h2>Merge selected files:</h2>

<form id="mergeForm" action="<c:url value="/download/merge/files"/>" method="POST">
    <ul>
        <c:forEach items="${userFiles}" var="file">
            <li><c:out value="${file.name}" /></li>
            <input name="selectedUserFile" type="text" hidden="hidden" value="${file.id}"/>
        </c:forEach>
    </ul>

    <h2>Select merging module:</h2>
    <select name="mergeModule">
        <c:forEach items="${mergeModules}" var="module">
            <option value="${module.id}">'<c:out value="${module.title}" />' module</option>
        </c:forEach>
    </select>

    <div style="clear: both;"></div>
    <input type="submit" value="Merge"/>
</form>