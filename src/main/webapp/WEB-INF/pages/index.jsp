<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    function getSelectedFileValue() {
        var select = document.getElementById('selectFile');
        return select.options[select.selectedIndex].value;
    }
</script>
<style type="text/css">
    .container {
        padding-top:1em;
        width: 100%;
    }
    .col1 {
        float: left;
        width: 50%;
    }
    .col2 {
        float: right;
        width: 50%;
    }
    .container legend{
        font-weight:bold;
    }
    .files{
        padding-top:1em;
        clear:both;
    }
</style>

<h1>Web Questionnaires</h1>
<div class="container">
    <f:form modelAttribute="uploadForm" action="uploadXml" method="POST" enctype="multipart/form-data">
        <f:errors path="*" element="div" cssClass="error-msg"/>
        <div class="col1">
        <fieldset>
            <legend>Upload XML file</legend>

            <p>
                <label for="uploadedXmlFile">Select the file from My Computer</label>
                <f:input id="uploadedXmlFile" type="file" path="uploadedXmlFile"/>
            </p>

            <p>
                <input type="submit" value="Upload"/>
            </p>

        </fieldset>
        </div>
    </f:form>
<div class="col2">
    <fieldset>
        <legend>Start a new web form</legend>
        <p>
            <label for="selectFile">Select the web form</label><br/>
            <select id="selectFile" name="selectFile" title="Select new webform">
                <option value="<c:url value="/forms/habides-factsheet-v4.xhtml?base_uri=${pageContext.request.contextPath}"/>">Habides factsheet</option>
            </select>
        </p>
        <p>
            <input type="button" value="Start" onclick="window.location=getSelectedFileValue()"/>
        </p>
    </fieldset>
</div>
</div>
<c:if test="${not empty uploadedFiles}">
<div class="files">
    <h2>My XML files</h2>

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
                <c:url value="/download/user_file?fileId=${file.id}" var="downloadLink"/>
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
                        <!-- FIXME - make the XML Schema check dynamic when Webforms repo is implemented -->
                        <c:if test="${file.xmlSchema eq 'http://biodiversity.eionet.europa.eu/schemas/bernconvention/derogations.xsd'}">
                            <a href="<c:url value="/forms/habides-factsheet-v4.xhtml?instance=${downloadLink}&amp;fileId=${file.id}&amp;base_uri=${pageContext.request.contextPath}"/>">Edit
                                with web form</a>
                        </c:if>
                        <c:forEach items="${file.availableConversions}" var="conversion">
                            <a href="<c:url value="/download/convert?fileId=${file.id}&conversionId=${conversion.id}"/>">${conversion.description}</a>
                        </c:forEach>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
</div>
</c:if>
<footer></footer>
