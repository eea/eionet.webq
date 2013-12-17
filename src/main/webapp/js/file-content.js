function viewFileSource(downloadLink) {
    viewFileSource(downloadLink);
}

function viewFileSource(downloadLink, setup) {
    $.ajax($.extend({ url: downloadLink, dataType: 'text'}, setup))
        .done(function (data) {
            var dataInTextArea = $('<textarea cols="120" rows="30" style="border:none;" readonly="readonly"/>').text(data.toString());
            $('<div title="File content"/>').append(dataInTextArea).dialog({
                maxHeight: 800,
                width: 1000,
                modal: true
            });
        });
}