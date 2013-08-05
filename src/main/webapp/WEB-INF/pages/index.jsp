<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
                            <a href="<c:url value="/forms/habides-factsheet-v4.xhtml?instance=${downloadLink}&fileId=${file.id}&base_uri=${pageContext.request.contextPath}"/>">Edit
                                with WebForm</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </fieldset>
    </c:if>
    <footer></footer>
