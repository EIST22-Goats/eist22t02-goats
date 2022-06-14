const sidebar = $('#notificationSidebar');
const notificationBtn = $('#notificationBtn');
const outDiv = $('#outDiv');
const notificationList = $('#notificationList');
const notificationCountBadge = $('#notificationCountBadge');

function loadNotifications() {
    notificationList.empty();
    $.ajax({
        type: "POST",
        url: "/getNotifications",
        success: function (data) {
            if (data.length === 0) {
                notificationCountBadge.fadeOut();
            } else {
                notificationCountBadge.fadeIn();
                notificationCountBadge.text(data.length);
            }

            for (let n of data) {
                notificationList.append($(`
                <div class="list-group-item list-group-item-action py-3 lh-sm" aria-current="true">
                    <div class="d-flex w-100 align-items-center justify-content-between">
                        <strong class="mb-1">` + n.title + `</strong>
                        <small>TODO</small>
                    </div>
                    <div class="col-10 mb-1 small">` + n.description + `</div>
                </div>`));
            }
        }
    });
}

notificationBtn.on('click', () => {
    outDiv.toggle('fade');
    sidebar.toggle("'slide', {direction: 'left' }, 500");
});

outDiv.on('click', () => {
    sidebar.toggle("'slide', {direction: 'left' }, 500");
    outDiv.toggle('fade');
});

loadNotifications();

setTimeout(() => {
    loadNotifications();
}, 10000);
