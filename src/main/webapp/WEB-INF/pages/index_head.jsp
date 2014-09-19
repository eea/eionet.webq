<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="popup_setup.jsp"/>
<script type="text/javascript" src="<c:url value="/js/jquery.validate.min.js"/>"></script>

<script type="text/javascript">

    $.validator.addMethod("fileName", function(value, element) {
        return this.optional(element) || /^[\w,\s-]+\.[A-Za-z]+$/.test(value);
    }, "Invalid file name");

    $.validator.addClassRules("fileName", {fileName: true});

    $().ready(function() {
        $("#editUserFileForm").validate();
    });

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
        $("#removeButton, #editButton").prop('disabled', !anyFileSelected());
    }

    function disableMergeButtonIfNoFilesSelected() {
        $("#mergeButton").prop('disabled', !anyTwoFilesSelected());
    }

    var init = function() {
        $("#uploadButton").click(function () {
            $("#startWebformArea").hide();
            $("#userFile").on("change", function () {
                $("#uploadForm").submit();
            });
            $("#userFile").click();
        });
        hideFilesTableIfNoFilesPresent();
        $("#startWebformArea").hide();
        showWarningIfNotAllFilesDownloaded();
        $("#mergeButton").click(function () {
            var actionForm = $("form#actionForm");
            actionForm.attr("action", "<c:url value="/download/merge/files"/>");
            actionForm.submit();
        });
        $("#editButton").click(function () {
            var actionForm = $("form#actionForm");
            actionForm.attr("action", "<c:url value="/edit"/>");
            actionForm.submit();
        });
        disableActionButtonsIfNoFilesSelected();
        disableMergeButtonIfNoFilesSelected();

        var onSelectFile = function() {
            disableActionButtonsIfNoFilesSelected();
            disableMergeButtonIfNoFilesSelected();
        };
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
            $("#info-area-" + $(this).attr("id")).fadeIn(500);
        },function(){
            $("#info-area-" + $(this).attr("id")).fadeOut(500);
        })
    });
    $(document).ready(function(){
        $(".arrow-up").hide();
        $('.advanced-conversions-toggle').click(function (){
            $("#advanced-conversions-" + $(this).attr("id").substring(4)).slideToggle(500);
            $(this).find(".arrow-up, .arrow-down").toggle();
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
        cursor: default;
        display: inline-block;
    }
    .info-area{
        display:none;
    }
    .advanced-conversions{
        display:none;
    }
    .arrow-up{margin-left:10px;width:25px;display:inline-block;}
    .arrow-down{margin-left:10px;width:25px;display:inline-block;}

    form.renameForm label.error, label.error {
        color: red;
    }
</style>
