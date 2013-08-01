<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
        <script type="text/javascript" src="<c:url value="/js/pageops.js"/>"></script>
    </head>
    <body>
        <div id="container">
            <div id="toolribbon">
                <div id="lefttools">
                    <a id="eealink" href="http://www.eea.europa.eu/">EEA</a>
                    <a id="ewlink" href="http://www.ewindows.eu.org/">EnviroWindows</a>
                </div>
                <div id="righttools">
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
            </div> <!-- pagehead -->

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


            <div id="leftcolumn" class="localnav">
                <ul>
                    <li><a href="<c:url value="/"/>">My xml file & new file upload</a></li>
                </ul>
            </div>

        <%--CONTENT--%>
            <div id="workarea" class="documentContent">
                <h1>Web Questionnaires</h1>
                <c:if test="${not empty message}">
                     <div id="message" class="success">${message}</div>
                </c:if>
                <form action="uploadXml" method="POST" enctype="multipart/form-data">
                    <fieldset>
                        <legend>Upload XML file</legend>

                        <p>
                            <label for="uploadedXmlFile">File</label><br/>
                            <input id="uploadedXmlFile" type="file" name="uploadedXmlFile"/>
                        </p>

                        <p>
                            <input type="submit" value="Upload"/>
                        </p>

                    </fieldset>
                </form>
                <c:if test="${not empty uploadedFiles}">
                <fieldset>
                    <legend>Uploaded XML files</legend>
                    <table class="datatable">
                        <thead>
                            <tr>
                                <th scope="col">File</th>
                                <th scope="col">File info</th>
                                <th scope="col">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${uploadedFiles}" var="file">
                                <c:url value="/download?fileId=${file.id}" var="downloadLink"/>
                                <tr>
                                    <td>
                                        <a href="${downloadLink}" title="Download file">${file.name}</a>
                                    </td>
                                    <td>
                                        File size: ${file.sizeInBytes} bytes<br/>
                                        Created: ${file.created}<br/>
                                        Updated: ${file.updated}
                                    </td>
                                    <td>
                                        <a href="<c:url value="/forms/habides-factsheet-v4.xhtml?instance=${downloadLink}&fileId=${file.id}&base_uri=${pageContext.request.contextPath}"/>">Edit with WebForm</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </fieldset>
                </c:if>
                <footer></footer>
            </div>
        <%--END CONTENT--%>

        <div id="pagefoot" style="max-width: none;">
            <p><a href="mailto:cr@eionet.europa.eu">E-mail</a> | <a href="mailto:helpdesk@eionet.europa.eu?subject=Feedback from the ${initParam.appDispName} website">Feedback</a></p>
            <p><a href="http://www.eea.europa.eu/"><b>European Environment Agency</b></a>
                <br/>Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark - Phone: +45 3336 7100</p>
        </div>
        </div>
    </body>
</html>
