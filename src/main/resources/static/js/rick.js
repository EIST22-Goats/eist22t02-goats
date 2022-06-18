let rickClickCounter = 0;

$('#rick').on('click', () => {
    rickClickCounter++;
    if (rickClickCounter % 3 === 0) {
        $('#rickModal').modal("show");
        const rickVideo = $("#rickVideo");
        const req = rickVideo[0].requestFullScreen || rickVideo[0].webkitRequestFullScreen || rickVideo[0].mozRequestFullScreen;
        req.call(rickVideo[0]);
        const symbol = rickVideo[0].src.indexOf("?") > -1 ? "&" : "?";
        rickVideo[0].src += symbol + "autoplay=1";
    }
});
