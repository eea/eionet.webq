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
            <c:set var="popup_id" value="host_${host.id}"/>
            <tr>
                <td><a href="#" onclick="viewHost('${popup_id}')">${host.hostName}</a></td>
                <td>${host.hostURL}</td>
                <td>${host.key}</td>
                <td class="dialogTemplate">
                    <div title="Host information" id="${popup_id}">
                        <table class="datatable" style="width:100%">
                            <tr>
                                <th scope="row">Host name</th>
                                <td>${host.hostName}</td>
                            </tr>
                            <tr>
                                <th scope="row">Host URL</th>
                                <td>${host.hostURL}</td>
                            </tr>
                            <tr>
                                <th scope="row">Authentication method</th>
                                <td>${host.authenticationMethod}</td>
                            </tr>
                            <tr>
                                <th scope="row">Key/Username</th>
                                <td>${host.key}</td>
                            </tr>
                            <tr>
                                <th scope="row">Ticket/password</th>
                                <td>${host.ticket}</td>
                            </tr>
                        </table>
                        <input type="button" onclick="window.location = '<c:url value="/known_hosts/update/${host.id}"/>'" value="Edit"/>
                        <input type="button" onclick="removeDialog($('#remove-host'), '<c:url value="/known_hosts/remove/${host.id}"/>');" value="Delete"/>
                        <input type="button" onclick="$('#${popup_id}').dialog('close');" value="Close"/>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div id="remove-host" title="Delete host?" class="dialogTemplate">
        <p>This host will be deleted. Are you sure?</p>
    </div>
</c:if>

