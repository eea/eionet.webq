<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui-1.10.3.custom.min.css"/>">
<script type="text/javascript" src="<c:url value="/js/file-content.js"/>"></script>

<style type="text/css">
    .dialogTemplate {
        display: none;
    }
</style>

<script type="text/javascript">
    function popup(id, maxHeight, width) {
        $("#" + id).dialog({
            resizable: false,
            maxHeight: maxHeight,
            width: width,
            modal: true
        });
    }

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
</script>