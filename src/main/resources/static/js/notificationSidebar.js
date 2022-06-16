const sidebar = $('#notificationSidebar');
const notificationBtn = $('#notificationBtn');
const outDiv = $('#outDiv');
const notificationList = $('#notificationList');
const notificationCountBadge = $('#notificationCountBadge');
const clearNotificationsBtn = $('#clearNotifications');

function loadNotifications() {
    notificationList.empty();
    $.ajax({
        type: "POST",
        url: "/getNotifications",
        success: function (data) {
            if (data.length === 0) {
                clearNotificationsBtn.addClass("d-none");
                notificationCountBadge.addClass("d-none");
                notificationList.append($(`
                <div class="list-group-item list-group-item-action py-3 lh-sm" aria-current="true">
                    <div class="d-flex w-100 align-items-center justify-content-between">
                        <strong class="mb-1">Du hast gerade keine Benachrichtigungen</strong>
                    </div>
                </div>`));
            } else {
                if (notificationCountBadge.hasClass("d-none")) {
                    notificationCountBadge.removeClass("d-none")
                }
                if (clearNotificationsBtn.hasClass("d-none")) {
                    clearNotificationsBtn.removeClass("d-none")
                }
                notificationCountBadge.text(data.length);
            }

            for (let n of data) {
                notificationList.append($(`
                <div class="list-group-item list-group-item-action py-3 lh-sm" aria-current="true">
                    <div class="d-flex w-100 align-items-center justify-content-between">
                        <strong class="mb-1">` + n.title + `</strong>
                        <small class="text-secondary">` + n.date + `</small>
                    </div>
                    <div class="col-10 mb-1 small">` + n.description + `</div>
                    <a class="stretched-link" href="` + n.link + `"></a>
                </div>`));
            }
        }
    });
}

notificationBtn.on('click', () => {
    outDiv.toggle('fade');
    sidebar.toggle("'slide', {direction: 'left'}, 500");
});

outDiv.on('click', () => {
    sidebar.fadeOut();
    outDiv.fadeOut();
});

clearNotificationsBtn.on('click', () => {
    $.ajax({
        type: "POST",
        url: "/clearNotifications",
        success: function () {
            loadNotifications();
        }
    });
});

function notificationUpdateLoop() {
    loadNotifications();
    setTimeout(() => {
        notificationUpdateLoop();
    }, 6000);
}

notificationUpdateLoop();
