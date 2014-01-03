<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<h1>Add known host</h1>
<c:url var="saveUrl" value="/known_hosts/save"/>
<form:form action="${saveUrl}" modelAttribute="knownHost" method="post">
    <form:errors path="*" element="div" cssClass="error-msg"/>
    <table class="datatable">
        <tr>
            <th scope="row"><label for="hostName">Host name</label></th>
            <td><form:input path="hostName" style="width:500px"/></td>
        </tr>
        <tr>
            <th scope="row"><label for="hostURL">Host URL</label></th>
            <td><form:input path="hostURL" style="width:500px"/></td>
        </tr>
        <tr>
            <th scope="row"><label for="authenticationMethod">Auth. method</label></th>
            <td>
                <s:eval expression="T(eionet.webq.dto.KnownHostAuthenticationMethod).values()" var="authMethods"/>
                <form:select path="authenticationMethod">
                    <form:options items="${authMethods}"/>
                </form:select>
            </td>
        </tr>
        <tr>
            <th scope="row"><label for="key">Key/Username</label></th>
            <td><form:input path="key" style="width:200px"/></td>
        </tr>
        <tr>
            <th scope="row"><label for="ticket">Ticket/Password</label></th>
            <td><form:input path="ticket" style="width:200px"/></td>
        </tr>
    </table>
    <form:hidden path="id"/>
    <input type="submit" value="Save"/>
    <input type="button" onclick="window.location = '<c:url value="/known_hosts/"/>'" value="Cancel">
</form:form>