<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<fieldset>
    <legend>Project data</legend>
    <p>
        Project id: ${projectEntry.projectId}
    </p>
    <p>
        Project label: ${projectEntry.description}
    </p>
    <p>
        Project created: ${projectEntry.created}
    </p>
</fieldset>
<fieldset>
    <legend>Project files</legend>
</fieldset>

<fieldset>
    <legend>New webform</legend>
    <%--<f:form commandName="newWebform" action="webform/add" method="post">--%>
        <%----%>
    <%--</f:form>--%>
</fieldset>