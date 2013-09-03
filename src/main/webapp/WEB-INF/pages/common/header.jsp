<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="toolribbon">
    <div id="lefttools">
        <a id="eealink" href="http://www.eea.europa.eu/">EEA</a>
        <a id="ewlink" href="http://www.ewindows.eu.org/">EnviroWindows</a>
    </div>
    <div id="righttools">
        <a href="<c:url value="/projects/"/>" id="loginlink">Login</a>
        <a id="printlink" title="Print this page" href="javascript:this.print();"><span>Print</span></a>
        <a id="fullscreenlink" href="javascript:toggleFullScreenMode()" title="Switch to/from full screen mode"><span>Switch to/from full screen mode</span></a>
        <%--TODO about page--%>
        <a id="acronymlink" href="about.action" title="About ${initParam.appDispName}"><span>About</span></a>
        <form action="http://search.eionet.europa.eu/search.jsp" method="get">
            <div id="freesrchform"><label for="freesrchfld">Search</label>
                <input type="text" id="freesrchfld" name="query"/>
                <input id="freesrchbtn" type="image" src="<c:url value="/images/button_go.gif"/>" alt="Go"/>
            </div>
        </form>
    </div>
</div>

<c:set var="appName" value="Web Questionnaire v2"/>

<div id="pagehead">
    <a href="/"><img src="<c:url value="/images/eea-print-logo.gif"/>" alt="Logo" id="logo" /></a>
    <div id="networktitle">Eionet</div>
    <div id="sitetitle">${appName}</div>
    <div id="sitetagline">This service is part of Reportnet</div>
</div>

<div id="menuribbon">
    <%@ include file="/WEB-INF/pages/dropdownmenus.txt" %>
</div>

<div class="breadcrumbtrail">
    <div class="breadcrumbhead">You are here:</div>
    <div class="breadcrumbitem eionetaccronym">
        <a href="http://www.eionet.europa.eu">Eionet</a>
    </div>
    <div class="breadcrumbitemlast">${appName}</div>
    <div class="breadcrumbtail"></div>
</div>