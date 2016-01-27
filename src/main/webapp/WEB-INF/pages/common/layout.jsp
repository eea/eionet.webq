<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache" />
    <meta http-equiv="expires" content="-1" />
    <meta name="Publisher" content="EEA, The European Environment Agency" />

    <title>Web Questionnaires</title>

    <link rel="stylesheet" type="text/css" href="//www.eionet.europa.eu/styles/eionet2007/print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="//www.eionet.europa.eu/styles/eionet2007/handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="//www.eionet.europa.eu/styles/eionet2007/screen.css" media="screen" title="Eionet 2007 style" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/eionet2007.css"/>" media="screen" title="Eionet 2007 style" />
    <script type="text/javascript" src="<c:url value="/js/pageops.js"/>"></script>
    <tiles:insertAttribute name="specific_header" ignore="true"/>
</head>

<sec:authorize access="isAuthenticated()" var="authenticated"/>

<body ${!authenticated? "class=\"fullscreen\"" : ""}>
<div id="container">
    <tiles:insertAttribute name="header" />
    <div id="workarea" class="documentContent">
        <c:if test="${not empty message}">
            <div id="message" class="system-msg"><c:out value="${message}" escapeXml="true"/></div>
        </c:if>
        <tiles:insertAttribute name="content" />
    </div>
    <tiles:insertAttribute name="footer" />
</div>
<%-- FIXME log server info for debugging. --%>
<div style="display:none">java.net.InetAddress.getLocalHost(): <%=java.net.InetAddress.getLocalHost() %></div>
<div style="display:none">request.getRemoteAddr(): <%=request.getRemoteAddr() %></div>
<div style="display:none">request.getContextPath(): <%=request.getContextPath() %></div>
<div style="display:none">request.getPathInfo(): <%=request.getPathInfo() %></div>
<div style="display:none">request.getRequestURL(): <%=request.getRequestURL() %></div>
<div style="display:none">request.getRequestURI(): <%=request.getRequestURI() %></div>
<div style="display:none">request.getRemoteHost(): <%=request.getRemoteHost() %></div>
<div style="display:none">request.getServerName(): <%=request.getServerName() %></div>
<div style="display:none">request.getScheme(): <%=request.getScheme() %></div>
<div style="display:none">session.getId(): <%=session.getId() %></div>
</body>
</html>
