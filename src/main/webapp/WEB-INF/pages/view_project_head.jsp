<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui-1.10.3.custom.min.css"/>">
<script type="text/javascript">
    function removeDialog(dialogElement, callUrl) {
        dialogElement.dialog({
            resizable: false,
            maxHeight: 300,
            modal: true,
            buttons: {
                "Remove" : function() {
                    window.location = callUrl;
                },
                Cancel: function() {
                    $(this).dialog("close");
                }
            }
        });
    }

    function view_file(popupElement) {
        popupElement.dialog({
            height: 450,
            width: 500,
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