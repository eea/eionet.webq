<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="drop-operations">
    <h2>Operations</h2>
    <ul>
        <li><span><a title="Add known host" href="<c:url value="/known_hosts/add"/>">Add known host</a></span></li>
    </ul>
</div>
<h1>Known hosts</h1>

<c:if test="${not empty allKnownHosts}">
    <table class="datatable">
        <thead>
        <tr>
            <th>Host name</th>
            <th>Host URL</th>
            <th>Key</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${allKnownHosts}" var="host">
            <tr>
                <td><a href="<c:url value="/known_hosts/${host.id}/view"/>">${host.hostName}</a></td>
                <td>${host.hostURL}</td>
                <td>${host.key}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>

