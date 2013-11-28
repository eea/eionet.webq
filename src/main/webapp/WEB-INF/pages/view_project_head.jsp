<jsp:include page="popup_setup.jsp"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    function removeDialog(dialogElement, callUrl) {
        dialogElement.dialog({
            resizable: false,
            maxHeight: 300,
            modal: true,
            buttons: {
                "Delete" : function() {
                    window.location = callUrl;
                },
                Cancel: function() {
                    $(this).dialog("close");
                }
            }
        });
    }

    function view_file(popupElement, width) {
        popupElement.dialog({
            width: width,
            modal: true
        });
    }

    function removeFile(id) {
        removeDialog($("#remove-file"), "<c:url value="/projects/${projectEntry.projectId}/webform/remove/?fileId="/>" + id);
    }

    function removeProject() {
        removeDialog($("#remove-project"), "<c:url value="/projects/remove?projectId=${projectEntry.projectId}"/>");
    }
</script>
