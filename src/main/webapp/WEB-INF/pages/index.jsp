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
    </head>
    <body>
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
    </body>
</html>
