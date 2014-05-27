<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="popup_setup.jsp"/>

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
    function anyTwoFilesSelected() {
        return $('input[name=selectedUserFile]:checked').length > 1;
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
        $("#removeButton").prop('disabled', !anyFileSelected());
    }

    function disableMergeButtonIfNoFilesSelected() {
        $("#mergeButton").prop('disabled', !anyTwoFilesSelected());
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
        disableMergeButtonIfNoFilesSelected();

        var onSelectFile = function() {
            disableActionButtonsIfNoFilesSelected();
            disableMergeButtonIfNoFilesSelected();
        }
        $('input[name=selectedUserFile]').change(onSelectFile);
    };

    $(init);

    function showJson(downloadLink) {
        viewFileSource(downloadLink, {headers: {Accept: "application/json"}});
    }

    function showJsonToXml(downloadLink) {
        viewFileSource(downloadLink, {headers: {Accept: "application/xml"}});
    }

    $(document).ready(function(){
        $('.info-toggle').hover(function(){
            $('.info-area').fadeIn(500)
        },function(){
            $('.info-area').fadeOut(500)
        })
    });
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
    .info-toggle {
        font-size: 16px;
        font-family: "Times New Roman",Georgia,Serif;
        text-align: center;
        color: #85A3A3;
        border: 1px solid #85A3A3;
        width: 20px;
        cursor: default;
        line-height: 20px;
        background-color: #fff;
        box-shadow: 2px 2px 2px #000;
        border-radius: 5px;
        display: inline-block;
    }
    .info-area{
        display:none;
    }
</style>
