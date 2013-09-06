<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>

<script type="text/javascript">
    function getSelectedFileValue() {
        return $('#selectFile').val();
    }

    function notAllFilesDownloaded() {
        return $(".not-downloaded:visible").length > 0;
    }

    function hideNotDownloadedNote(prefix) {
        $("#" + prefix + "not-downloaded").hide();
        $("#" + prefix + "downloaded").text("Changed file downloaded.");
        showWarningIfNotAllFilesDownloaded();
    }

    function showWarningIfNotAllFilesDownloaded() {
        var warningElement = $("#not-downloaded-files-present");
        if (notAllFilesDownloaded()) {
            warningElement.show();
        } else {
            warningElement.hide();
        }
    }

    $(showWarningIfNotAllFilesDownloaded);
</script>


