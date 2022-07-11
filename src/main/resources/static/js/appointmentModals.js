const deleteAppointmentModalForm = $("#deleteAppointmentModalForm");

const createAppointmentModalForm = $("#createAppointmentModalForm");

const changeDateModalForm = $("#changeDateModalForm");
const changeButton = $("#appointmentModalChangeBtn")
const appointmentDataColumn = $("#appointmentDataColumn")
const changeAppointmentModalNameInput = $("#changeAppointmentModalNameInput")
const changeAppointmentModalDescriptionInput = $("#changeAppointmentModalDescriptionInput")
const changeAppointmentModalStartDateInput = $("#changeAppointmentModalStartDateInput")
const changeAppointmentModalStartTimeInput = $("#changeAppointmentModalStartTimeInput")
const changeAppointmentModalEndTimeInput = $("#changeAppointmentModalEndTimeInput")
const changeAppointmentModalRepetitionsInput = $("#changeAppointmentModalRepetitionsInput")
const changeAppointmentModalDeleteBtn = $("#changeAppointmentModalDeleteBtn")

const appointmentName = $("#appointmentName");
const appointmentDescription = $("#appointmentDescription")
const appointmentStartDate = $("#appointmentStartDate")
const appointmentEndTime = $("#appointmentEndTime")
const appointmentStartTime = $("#appointmentStartTime")
const appointmentRepetitions = $("#appointmentRepetitions")
const appointmentRoomName = $("#appointmentRoomName")
const appointmentRoomNameDiv = $("#appointmentRoomNameDiv")
const appointmentAddress = $("#appointmentAddress")
const appointmentAddressDiv = $("#appointmentAddressDiv")

const deleteAppointmentModal = $('#deleteAppointmentModal')
const changeAppointmentModal = $('#changeDateModal')
const createAppointmentModal = $('#newDateModal')
const appointmentModal = $('#appointmentModal')

function loadDeleteAppointmentModal(modal) {
    modal.on('show.bs.modal', function (event) {
        console.log("loadDeleteAppointmentModal")
        let appointmentId = event.relatedTarget.getAttribute('data-bs-appointment-id')

        let course_id = event.relatedTarget.getAttribute('data-bs-course')
        if (course_id === "-1") {
            deleteAppointmentModalForm.attr("action", "/deleteAppointment/" + appointmentId)
        } else {
            deleteAppointmentModalForm.attr("action", "/deleteCourseAppointment/" + appointmentId)
        }
    })
}

function loadLocationInputs(modal) {
    let addressInput = modal.find(".address-input");
    let roomNameInput = modal.find(".room-name-input");

    modal.find('.trigger-location-preview').on('click', () => {
        if (addressInput.val().length >= 3) {
            showLocation(modal, addressInput.val())
        }
    })

    modal.find('.trigger-room-preview').on('click', () => {
        if (roomNameInput.val().length >= 3) {
            showRoom(modal, roomNameInput.val())
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
        console.log("show change modal")

        let name = event.relatedTarget.getAttribute('data-bs-name')
        changeAppointmentModalNameInput.val(name);

        let description = event.relatedTarget.getAttribute('data-bs-description')
        changeAppointmentModalDescriptionInput.val(description);

        let startDate = event.relatedTarget.getAttribute('data-bs-startdate');
        changeAppointmentModalStartDateInput.val(startDate);

        let startTime = event.relatedTarget.getAttribute('data-bs-starttime');
        changeAppointmentModalStartTimeInput.val(startTime);

        let endTime = event.relatedTarget.getAttribute('data-bs-endtime');
        changeAppointmentModalEndTimeInput.val(endTime);

        let repetitions = event.relatedTarget.getAttribute('data-bs-repetitions');
        changeAppointmentModalRepetitionsInput.val(repetitions);

        let address = event.relatedTarget.getAttribute("data-bs-address");
        let addressInput = $(modal).find(".address-input");
        addressInput.val(address);

        let roomName = event.relatedTarget.getAttribute("data-bs-roomname");
        let roomNameInput = modal.find(".room-name-input");
        roomNameInput.val(roomName);

        let appointmentId = event.relatedTarget.getAttribute("data-bs-appointment-id");
        changeAppointmentModalDeleteBtn.attr("data-bs-appointment-id", appointmentId)

        let course_id = event.relatedTarget.getAttribute('data-bs-course')
        changeAppointmentModalDeleteBtn.attr("data-bs-course", course_id)
        if (course_id === "-1") {
            changeDateModalForm.attr('action', "/updateAppointment/" + appointmentId);
        } else {
            changeDateModalForm.attr('action', "/updateCourseAppointment/" + appointmentId);
        }

        if (roomName !== null) {
            roomNameInput.removeAttr("disabled");
            addressInput.attr("disabled", "");
        } else {
            addressInput.removeAttr("disabled");
            roomNameInput.attr("disabled", "");
        }

        showLocation(modal, address, roomName);
        setTimeout(() => {
            showLocation(modal, address, roomName);
        }, 100);

        loadLocationInputs(modal);
    })
}

function loadCreateAppointmentModal(modal) {
    loadLocationInputs(modal)

    modal.on('show.bs.modal', function (event) {
        console.log("show creation modal")

        let course_id = event.relatedTarget.getAttribute('data-bs-course')
        if (course_id === "-1") {
            createAppointmentModalForm.attr("action", "/createAppointment")
        } else {
            createAppointmentModalForm.attr("action", "/createCourseAppointment/"+course_id)
        }
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
        appointmentName.text(name);

        let description = event.relatedTarget.getAttribute('data-bs-description')
        changeButton.attr('data-bs-description', description)
        appointmentDescription.text(description);

        let startDate = event.relatedTarget.getAttribute('data-bs-startdate');
        changeButton.attr('data-bs-startdate', startDate)
        appointmentStartDate.text(startDate);

        let startTime = event.relatedTarget.getAttribute('data-bs-starttime');
        changeButton.attr('data-bs-starttime', startTime)
        appointmentStartTime.text(startTime);

        let endTime = event.relatedTarget.getAttribute('data-bs-endtime');
        changeButton.attr('data-bs-endtime', endTime)
        appointmentEndTime.text(endTime);

        let repetitions = event.relatedTarget.getAttribute('data-bs-repetitions');
        changeButton.attr('data-bs-repetitions', repetitions)
        appointmentRepetitions.text(repetitions);

        let roomName = event.relatedTarget.getAttribute('data-bs-roomname');
        changeButton.attr('data-bs-roomname', roomName)
        appointmentRoomName.text(roomName);

        let address = event.relatedTarget.getAttribute('data-bs-address');
        changeButton.attr('data-bs-address', address)
        appointmentAddress.text(address);

        let roomViewDiv = modal.find(".roomViewDiv")
        let locationViewDiv = modal.find(".locationViewDiv")

        if (roomName !== null) {
            appointmentRoomNameDiv.removeClass("d-none");
            roomViewDiv.removeClass("d-none");
        } else {
            appointmentRoomNameDiv.addClass("d-none");
            roomViewDiv.addClass("d-none");
        }

        if (address !== null) {
            appointmentAddressDiv.removeClass("d-none");
        } else {
            appointmentAddressDiv.addClass("d-none");
        }


        if (address !== null || roomName !== null) {
            appointmentModal.addClass("modal-lg");
            appointmentDataColumn.addClass("col-4");
            locationViewDiv.removeClass("d-none");
        } else {
            appointmentModal.removeClass("modal-lg");
            appointmentDataColumn.removeClass("col-4");
            locationViewDiv.addClass("d-none");
        }

        showLocation(modal, address, roomName);
        setTimeout(() => {
            showLocation(modal, address, roomName);
        }, 100);
    })
}

function showLocation(modal, address, roomName) {
    let roomImageDiv = modal.find('.roomViewDiv');
    if (address !== null) {
        if (!roomImageDiv.hasClass("d-none")) {
            roomImageDiv.addClass("d-none");
        }
        showAddress(modal, address)
    } else if (roomName !== null) {
        showRoom(modal, roomName)
    }
}

function showAddress(modal, address) {
    let locationView = modal.find(".locationView")
    $.ajax({
        url: "/getIframeUrl?location=" + address,
        success: function (data) {
            locationView.attr("src", data["iframeUrl"]);
        }
    })
}

function showRoom(modal, roomName) {
    let roomImageDiv = modal.find('.roomViewDiv');
    let roomImage = modal.find('.roomView');
    let roomImageMarker = modal.find('.roomMarker');
    let locationPreviewIFrame = modal.find(".locationView");

    $.ajax({
        url: "/getIframeUrl?roomName=" + roomName,
        success: function (data) {
            locationPreviewIFrame.attr("src", data["iframeUrl"]);
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

window.onload(() => {
    loadDeleteAppointmentModal(deleteAppointmentModal);
    loadChangeAppointmentModal(changeAppointmentModal);
    loadCreateAppointmentModal(createAppointmentModal);
    loadAppointmentModal(appointmentModal);
})