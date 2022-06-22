const currentName = $('#currentName');
const currentProfilePicture = $('#profilePictureForm');
const chatMessages = $('#chatMessages');
const messageSendBtn = $('#messageSendBtn');
const messageInput = $('#messageInput');

const leftSide = () => $('<div class="d-flex align-items-baseline mb-1 pe-2"><div class="float-start clearfix" style="width: 70%;"></div></div>');

const rightSide = () => $('<div class="d-flex align-items-baseline text-end justify-content-end mb-1 pe-2"><div class="float-end clearfix" style="width: 70%;"></div></div>');

const leftMessage = (message) => `
<div>
    <div class="card card-text d-inline-block p-2 px-3 m-1"
         style="border: 2px solid #d3d3d3">${message}</div>
</div>`;

const rightMessage = (message) => $(`
<div>
    <div class="card card-text d-inline-block p-2 px-3 m-1"
         style="border: 2px solid #0065bd">${message}</div>
</div>`);

const timeDiv = (t) => `
<div>
    <div class="small text-muted">${t}</div>
</div>`;

let lastMessageLeft = null;
let lastMessageRight = null;

function addLeftMessage({time, message}) {
    if (lastMessageLeft == null) {
        lastMessageRight = null;
        lastMessageLeft = leftSide();
        chatMessages.append(lastMessageLeft);
    }
    lastMessageLeft.children(":first").children(":last").remove();
    lastMessageLeft.children(":first").append(leftMessage(message));
    lastMessageLeft.children(":first").append(timeDiv(time));
}

function addRightMessage({time, message}) {
    if (lastMessageRight == null) {
        lastMessageLeft = null;
        lastMessageRight = rightSide();
        chatMessages.append(lastMessageRight);
    }
    lastMessageRight.children(":first").children(":last").remove();
    lastMessageRight.children(":first").append(rightMessage(message));
    lastMessageRight.children(":first").append(timeDiv(time));
}

function loadMessages(tumId) {
    $.ajax({
        type: "POST",
        url: "/messages/" + tumId,
        success: function (messages) {
            for (let message of messages) {
                if (message.sender === tumId) {
                    addLeftMessage(message)
                } else {
                    addRightMessage(message)
                }
            }
        }
    });
}

messageSendBtn.on('click', () => {
    let text = messageInput.val().trim();
    messageInput.val("");
    let tumId = $('#chats').attr("tumid");
    if (text !== "") {
        $.ajax({
            type: "POST",
            url: "/addMessage/" + tumId,
            data: {"text": text},
            success: function (msg) {
                addRightMessage(msg);
            }
        });
    }
});

$(document).ready(() => {
    let tumId = $('#chats').attr("tumid");
    loadMessages(tumId);
});
