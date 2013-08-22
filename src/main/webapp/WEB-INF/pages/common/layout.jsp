<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache" />
    <meta http-equiv="expires" content="-1" />
    <meta name="Publisher" content="EEA, The European Environment Agency" />

    <title>Web Questionnaires</title>

    <link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="http://www.eionet.europa.eu/styles/eionet2007/screen.css" media="screen" title="Eionet 2007 style" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/eionet2007.css"/>" media="screen" title="Eionet 2007 style" />
    <script type="text/javascript" src="<c:url value="/js/pageops.js"/>"></script>
</head>
<body>
<div id="container">
    <tiles:insertAttribute name="header" />
    <div id="leftcolumn" class="localnav">
        <ul>
            <li><a href="<c:url value="/"/>">My xml files</a></li>
            <li><a href="<c:url value="/projects/"/>">Webform projects</a></li>
        </ul>
    </div>
    <div id="workarea" class="documentContent">
        <c:if test="${not empty message}">
            <div id="message" class="system-msg">${message}</div>
        </c:if>
        <tiles:insertAttribute name="content" />
    </div>
    <tiles:insertAttribute name="footer" />
</div>
</body>
</html>
