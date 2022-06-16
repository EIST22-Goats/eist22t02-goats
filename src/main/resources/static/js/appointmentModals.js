const deleteAppointmentModalForm = $("#deleteAppointmentModalForm");
const changeDateModalForm = $("#changeDateModalForm");
const changeButton = $("#appointmentModalChangeBtn")
const changeAppointmentModalNameInput = $("#changeAppointmentModalNameInput")
const changeAppointmentModalDescriptionInput = $("#changeAppointmentModalDescriptionInput")
const changeAppointmentModalStartDateInput = $("#changeAppointmentModalStartDateInput")
const locationView = $("#locationView");
const roomView = $("#roomView");

const deleteAppointmentModal = $('#deleteAppointmentModal')
const changeAppointmentModal = $('#changeDateModal')
const createAppointmentModal = $('#newDateModal')
const appointmentModal = $('#timeslotModal')

function loadDeleteAppointmentModal(modal) {
    modal.on('show.bs.modal', function (event) {
        deleteAppointmentModalForm.action = "/deleteAppointment/" +
            event.relatedTarget.getAttribute('data-bs-appointment-id')
    })
}

function loadLocationPreview(modal) {

    let roomImageDiv = modal.find('.room-image-div');
    let roomImage = modal.find('.room-image');
    let roomImageMarker = modal.find('.room-image-marker');


    let locationPreviewIFrame = modal.find(".location-preview-iframe");
    let triggerLocationPreview = modal.find('.trigger-location-preview');
    let triggerRoomPreview = modal.find('.trigger-room-preview');

    let addressInput = modal.find(".address-input");
    triggerLocationPreview.on('click', () => {
        if (addressInput.val().length >= 3) {
            $.ajax({
                url: "/getIframeUrl?location=" + addressInput.val(),
                success: function (data) {
                    locationPreviewIFrame.attr("src", data["iframeUrl"]);
                    if (!roomImageDiv.hasClass("d-none")) {
                        roomImageDiv.addClass("d-none");
                    }
                }
            })
        }
    })

    let roomNameInput = modal.find(".room-name-input");
    triggerRoomPreview.on('click', () => {
        if (roomNameInput.val().length >= 3) {
            $.ajax({
                url: "/getIframeUrl?roomName=" + roomNameInput.val(),
                success: function (data) {
                    locationPreviewIFrame.attr("src", data["iframeUrl"]);
                    roomNameInput.val(data["roomId"]);
                    if ("roomImageUrl" in data) {
                        if (roomImageDiv.hasClass("d-none")) {
                            roomImageDiv.removeClass("d-none");
                        }
                        roomImage.attr("src", data["roomImageUrl"]);
                        if (roomImageMarker.hasClass("d-none")) {
                            roomImageMarker.removeClass("d-none");
                        }
                        roomImageMarker.css("left", data["markerX"] + "%");
                        roomImageMarker.css("top", data["markerY"] + "%");
                    } else {
                        if (!roomImageDiv.hasClass("d-none")) {
                            roomImageDiv.addClass("d-none");
                        }
                    }
                }
            })
        }
    })

    addressInput.on('input', () => {
        if (addressInput.val() !== "") {
            roomNameInput.attr("disabled", "");
        } else {
            roomNameInput.removeAttr("disabled");
        }
    })

    roomNameInput.on('input', () => {
        if (roomNameInput.val() !== "") {
            addressInput.attr("disabled", "");
        } else {
            addressInput.removeAttr("disabled");
        }
    })
}

function loadChangeAppointmentModal(modal) {
    modal.on('show.bs.modal', function (event) {
        let name = event.relatedTarget.getAttribute('data-bs-name')
        changeAppointmentModalNameInput.val(name);

        let description = event.relatedTarget.getAttribute('data-bs-description')
        changeAppointmentModalDescriptionInput.val(description);

        let startDate = event.relatedTarget.getAttribute('data-bs-startdate');
        changeAppointmentModalStartDateInput.val(startDate);

        let startTime = event.relatedTarget.getAttribute('data-bs-starttime');
        $("#changeAppointmentModalStartTimeInput").val(startTime);

        let endTime = event.relatedTarget.getAttribute('data-bs-endtime');
        $("#changeAppointmentModalEndTimeInput").val(endTime);

        let repetitions = event.relatedTarget.getAttribute('data-bs-repetitions');
        $("#changeAppointmentModalRepetitionsInput").val(repetitions);

        let address = event.relatedTarget.getAttribute("data-bs-address");
        let addressInput = $(modal).find(".address-input");
        addressInput.val(address);

        let roomName = event.relatedTarget.getAttribute("data-bs-roomname");
        let roomNameInput = modal.find(".room-name-input");
        roomNameInput.val(roomName);

        let appointmentId = event.relatedTarget.getAttribute("data-bs-appointment-id");
        let deleteButton = modal.find("#changeAppointmentModalDeleteBtn");
        deleteButton.attr("data-bs-appointment-id", appointmentId)

        changeDateModalForm.attr('action', "/updateAppointment/" + appointmentId);

        if (addressInput.val() !== "") {
            roomNameInput.attr("disabled", "");
        } else {
            roomNameInput.removeAttr("disabled");
        }

        if (roomNameInput.val() !== "") {
            addressInput.attr("disabled", "");
        } else {
            addressInput.removeAttr("disabled");
        }

        setTimeout(() => {
            showLocation(address, roomName, locationView, roomView);
        }, 100);

        loadLocationPreview(modal);
    })
}

function loadAppointmentModal(modal) {
    modal.on('show.bs.modal', function (event) {

        let changeable = event.relatedTarget.getAttribute('data-bs-changeable').trim();
        if (changeable.trim() === "true") {
            changeButton.removeClass("d-none");
        } else {
            changeButton.addClass("d-none");
        }

        let appointmentId = event.relatedTarget.getAttribute('data-bs-appointment-id');
        changeButton.attr('data-bs-appointment-id', appointmentId)

        let name = event.relatedTarget.getAttribute('data-bs-name')
        changeButton.attr('data-bs-name', name)
        $("#appointmentName").text(name);

        let description = event.relatedTarget.getAttribute('data-bs-description')
        changeButton.attr('data-bs-description', description)
        $("#appointmentDescription").text(description);

        let startDate = event.relatedTarget.getAttribute('data-bs-startdate');
        changeButton.attr('data-bs-startdate', startDate)
        $("#appointmentStartDate").text(startDate);

        let startTime = event.relatedTarget.getAttribute('data-bs-starttime');
        changeButton.attr('data-bs-starttime', startTime)
        $("#appointmentStartTime").text(startTime);

        let endTime = event.relatedTarget.getAttribute('data-bs-endtime');
        changeButton.attr('data-bs-endtime', endTime)
        $("#appointmentEndTime").text(endTime);

        let repetitions = event.relatedTarget.getAttribute('data-bs-repetitions');
        changeButton.attr('data-bs-repetitions', repetitions)
        $("#appointmentRepetitions").text(repetitions);

        let roomName = event.relatedTarget.getAttribute('data-bs-roomname');
        changeButton.attr('data-bs-roomname', roomName)
        $("#appointmentRoomName").text(roomName);

        if (roomName === null) {
            $("#appointmentRoomNameDiv").addClass("d-none");
            roomView.addClass("d-none");
        } else {
            $("#appointmentRoomNameDiv").removeClass("d-none");
            roomView.removeClass("d-none");
        }

        let address = event.relatedTarget.getAttribute('data-bs-address');
        changeButton.attr('data-bs-address', address)
        $("#appointmentAddress").text(address);
        if (address === null) {
            $("#appointmentAddressDiv").addClass("d-none");
        } else {
            $("#appointmentAddressDiv").removeClass("d-none");
        }

        if (address !== null || roomName !== null) {
            $("#timeslotModal").addClass("modal-lg");
            $("#appointmentDataColumn").addClass("col-4");
            $("#locationViewDiv").removeClass("d-none");
        } else {
            $("#timeslotModal").removeClass("modal-lg");
            $("#appointmentDataColumn").removeClass("col-4");
            $("#locationViewDiv").addClass("d-none");
        }

        setTimeout(() => {
            showLocation(address, roomName, locationView, roomView);
        }, 100);
    })
}

function showLocation(address, roomName, locationView, roomView) {
    if (address !== null) {
        $.ajax({
            url: "/getIframeUrl?location=" + address,
            success: function (data) {
                locationView.attr("src", data["iframeUrl"]);
            }
        })
    } else if (roomName !== null) {
        $.ajax({
            url: "/getIframeUrl?roomName=" + roomName,
            success: function (data) {
                roomView.attr("src", data["roomImageUrl"]);
                locationView.attr("src", data["iframeUrl"]);
            }
        })
    }
}

loadDeleteAppointmentModal(deleteAppointmentModal);
loadChangeAppointmentModal(changeAppointmentModal);
loadLocationPreview(createAppointmentModal);
loadAppointmentModal(appointmentModal);