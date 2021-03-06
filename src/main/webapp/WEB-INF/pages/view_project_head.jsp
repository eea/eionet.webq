<jsp:include page="popup_setup.jsp"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript">
    function view_file(popupElement, width) {
        popupElement.dialog({
            width: width,
            modal: true
        });
    }

    function webFormOpenDialog(id) {
        $('#openWebForm #webFormId').val(id);
        $('#openWebForm').dialog({
            width: 700,
            height: 250,
            modal: true
        });
    }

    function removeFile(id) {
        removeDialog($("#remove-file"), "<c:url value="/projects/${fn:escapeXml(projectEntry.projectId)}/webform/remove/?fileId="/>" + id);
    }

    function removeProject() {
        removeDialog($("#remove-project"), "<c:url value="/projects/remove?projectId=${fn:escapeXml(projectEntry.projectId)}"/>");
    }
</script>
