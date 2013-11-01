function viewFileSource(downloadLink) {
    $.ajax({
        url: downloadLink,
        dataType: 'text'
    })
    .done(function (data) {
        var dataInTextArea = $('<textarea cols="120" rows="30" style="border:none;" readonly="readonly"/>').text(data);
        $('<div title="File content"/>').append(dataInTextArea).dialog({
            maxHeight: 800,
            width: 1000,
            modal: true
        });
    });
}