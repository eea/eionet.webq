<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="toolribbon">
    <div id="lefttools">
        <a id="eealink" href="https://www.eea.europa.eu/">EEA</a>
        <a id="ewlink" href="http://www.ewindows.eu.org/">EnviroWindows</a>
    </div>
    <div id="righttools">
        <sec:authorize access="isAuthenticated()" var="authenticated"/>
        <sec:authorize access="hasAuthority('DEVELOPER')" var="isDeveloper"/>
        <sec:authorize access="hasAuthority('ADMIN')" var="isAdmin"/>
        <c:set var="developerOrAdmin" value="${isAdmin or isDeveloper}"/>
        <c:choose>
            <c:when test="${authenticated}">
                <sec:authentication property="name" var="userName"/>
                <a href="<c:url value="/logout"/>" id="logoutlink">Logout (${fn:escapeXml(userName)})</a>
            </c:when>
            <c:otherwise>
                <a href="<c:url value="/login"/>" id="loginlink">Login</a>
            </c:otherwise>
        </c:choose>
        <a id="printlink" title="Print this page" href="javascript:this.print();"><span>Print</span></a>
        <a id="fullscreenlink" href="javascript:toggleFullScreenMode()" title="Switch to/from full screen mode"><span>Switch to/from full screen mode</span></a>
        <a id="eionetlink" title="Go to Eionet portal" href="https://www.eionet.europa.eu/"><span>Eionet portal</span></a>
    </div>
</div>

<c:set var="appName" value="Web Questionnaires"/>

<div id="pagehead">
    <a href="/"><img src="<c:url value="/images/eea-print-logo.gif"/>" alt="Logo" id="logo" /></a>
    <div id="networktitle">Eionet</div>
    <div id="sitetitle">${fn:escapeXml(appName)}</div>
    <div id="sitetagline">This service is part of Reportnet</div>
</div>

<div id="menuribbon"></div>

<div class="breadcrumbtrail">
    <div class="breadcrumbhead">You are here:</div>
    <div class="breadcrumbitem eionetaccronym">
        <a href="https://www.eionet.europa.eu">Eionet</a>
    </div>
    <div class="breadcrumbitemlast">
        <a href="<c:url value="/"/>">${fn:escapeXml(appName)}</a>
    </div>
    <div class="breadcrumbtail"></div>
</div>

<div id="leftcolumn" class="localnav">
    <ul>
        <li><a href="<c:url value="/sessionfiles"/>">My session files</a></li>
        <c:if test="${developerOrAdmin}">
            <li><a href="<c:url value="/projects/"/>">Webform projects</a></li>
            <li><a href="<c:url value="/merge/modules"/>">Merging modules</a></li>
        </c:if>
        <c:if test="${isAdmin}">
            <li><a href="<c:url value="/users/view"/>">Add/replace user role</a></li>
            <li><a href="<c:url value="/known_hosts/"/>">Manage known hosts</a></li>
        </c:if>
    </ul>
</div>