<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<h1>Add known host</h1>
<c:url var="saveUrl" value="/known_hosts/add"/>
<sf:form action="${saveUrl}" modelAttribute="host" method="post">
    <sf:errors path="*" element="div" cssClass="error-msg"/>
    <table class="datatable">
        <tr>
            <th scope="row"><label for="hostURL">Host URL</label></th>
            <td><sf:input path="hostURL" style="width:200px"/></td>
        </tr>
        <tr>
            <th scope="row"><label for="hostName">Host name</label></th>
            <td><sf:input path="hostName" style="width:200px"/></td>
        </tr>
        <tr>
            <th scope="row"><label for="authenticationMethod">Host name</label></th>
            <td>
                <s:eval expression="T(eionet.webq.dto.KnownHostAuthenticationMethod).values()" var="authMethods"/>
                <sf:select path="authenticationMethod">
                    <sf:options items="${authMethods}"/>
                </sf:select>
            </td>
        </tr>
        <tr>
            <th scope="row"><label for="key">Key/Username</label></th>
            <td><sf:input path="key" style="width:200px"/></td>
        </tr>
        <tr>
            <th scope="row"><label for="ticket">Ticket/Password</label></th>
            <td><sf:input path="ticket" style="width:200px"/></td>
        </tr>
    </table>
    <input type="submit" value="Save"/>
</sf:form>