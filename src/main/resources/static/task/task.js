async function ajaxWrapper(url, formData) {
    try {
        return $.ajax({
            url: url,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
        });
    } catch (err) {
        console.log(err);
        return undefined;
    }
}
async function ajaxJsonWrapper(url, jsonData, csrf) {
    try {
        return $.ajax({
            url : url,
            type : "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("X-CSRF-TOKEN", csrf);
            },
            data : JSON.stringify(jsonData),
            processData: false,
            contentType: "application/json; charset=utf-8",
        });
    } catch (err) {
        console.log(err);
        return undefined;
    }
}