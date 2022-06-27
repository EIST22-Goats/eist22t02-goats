const chatMessages = $('#chatMessages');
const messageSendBtn = $('#messageSendBtn');
const messageInput = $('#messageInput');
const chatSearchInput = $('#chatSearchInput');
const chats = $('#chats');

const leftSide = () => $('<div class="d-flex align-items-baseline mb-1 pe-2"><div class="float-start clearfix" style="width: 70%;"></div></div>');

const rightSide = () => $('<div class="d-flex align-items-baseline text-end justify-content-end mb-1 pe-2"><div class="float-end clearfix" style="width: 70%;"></div></div>');

let lastId = -1;

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

function scrollDown() {
    chatMessages.animate({
        scrollTop: 10000000
    }, 200);
}

function loadMessages(tumId) {
    $.ajax({
        type: "POST",
        url: "/messages/" + tumId,
        success: function (messages) {
            let newMessages = lastId === -1;
            const oldLastId = lastId;

            for (let message of messages) {
                if (message["id"] === oldLastId) {
                    newMessages = true;
                } else if (newMessages) {
                    if (message.sender === tumId) {
                        addLeftMessage(message)
                    } else {
                        addRightMessage(message)
                    }
                }
                lastId = message["id"];
            }
            if (lastId !== oldLastId) {
                scrollDown();
            }
        }
    });
}

function sendMessage() {
    let text = messageInput.val().trim();
    messageInput.val("");
    let tumId = $('#chats').attr("tumid");
    if (text !== "") {
        $.ajax({
            type: "POST",
            url: "/addMessage/" + tumId,
            data: {"text": text},
            success: function () {
                let tumId = chats.attr("tumid");
                loadMessages(tumId);
            }
        });
    }
}

messageSendBtn.on('click', sendMessage);
messageInput.keypress((event) => {
    if (event.keyCode === 13) {
        sendMessage()
    }
});

function updateMessages(first = false) {
    let tumId = chats.attr("tumid");
    if (tumId != null) {
        loadMessages(tumId);
        if (first)
            scrollDown();
        setTimeout(updateMessages, 1000);
    }
}

chatSearchInput.on('input', () => {
    let searchText = chatSearchInput.val().trim().toLowerCase();
    if (searchText !== "") {
        for (let it of chats.children()) {
            it = $(it);
            let tumId = it.attr("data-tumid");
            if (tumId !== undefined) {
                let name = it.attr("data-name").toLowerCase();
                if (tumId.includes(searchText) || name.includes(searchText)) {
                    if (it.hasClass("d-none")) {
                        it.removeClass("d-none");
                    }
                } else {
                    if (!it.hasClass("d-none")) {
                        it.addClass("d-none");
                    }
                }
            }
        }
    } else {
        for (let it of chats.children()) {
            it = $(it);
            if (it.hasClass("d-none")) {
                it.removeClass("d-none");
            }
        }
    }
});

$(document).ready(() => {
    updateMessages(true);
});
