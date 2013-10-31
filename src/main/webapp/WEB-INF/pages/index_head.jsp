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

    function anyFileSelected() {
        return $('input[name=selectedUserFile]:checked').length > 0;
    }

    function showStartWebformArea() {
        $("#startWebformArea").show();
    }

    function hideFilesTableIfNoFilesPresent() {
        if (!$("tr.user_file").length) {
            $("div.files").hide();
        }
    }

    function disableActionButtonsIfNoFilesSelected() {
        $("#mergeButton, #removeButton").prop('disabled', !anyFileSelected());
    }

    var init = function() {
        $("#uploadButton").click(function () {
            $("#startWebformArea").hide();
            $("#userFile").click().change(function () {
                $("#newFileSubmit").click();
            });
        });
        hideFilesTableIfNoFilesPresent();
        $("#startWebformArea").hide();
        showWarningIfNotAllFilesDownloaded();
        $("#mergeButton").click(function () {
            var actionForm = $("form#actionForm");
            actionForm.attr("action", "<c:url value="/download/merge/files"/>");
            actionForm.submit();
        });
        disableActionButtonsIfNoFilesSelected();
        $('input[name=selectedUserFile]').change(disableActionButtonsIfNoFilesSelected);
    };

    $(init);

</script>
<style type="text/css">
    .container {
        width: 100%;
    }
    .col1 {
        float: left;
        width: 100%;
    }
    .col2 {
        float: left;
        width: 100%;
    }
    .container legend {
        font-weight:bold;
    }
    .files {
        padding-top:1em;
        clear:both;
    }
    .action {
        margin-bottom:0.5em;
    }
    .hidden {
        display: none;
    }
</style>
