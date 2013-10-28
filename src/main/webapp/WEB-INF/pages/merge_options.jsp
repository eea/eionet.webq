<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Merge options</h1>

<h2>Merge selected files with</h2>
<form id="mergeForm" action="<c:url value="/download/merge/files"/>" method="POST">
    <c:forEach items="${userFiles}" var="file">
        <input name="selectedUserFile" type="text" hidden="hidden" value="${file.id}"/>
    </c:forEach>
    <input type="text" id="mergeModule" name="mergeModule" hidden="hidden" value=""/>
    <c:forEach items="${mergeModules}" var="module">
        <input type="button" id="${module.id}" value="'${module.title}' module">
    </c:forEach>
</form>

<input type="button" onclick="window.location='<c:url value="/"/>'" value="Cancel"/>
