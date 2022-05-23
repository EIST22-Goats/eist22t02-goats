let tumIdRegex = new RegExp(/^[^aeiouAEIOU\d\W][aeiou]\d\d[^aeiouAEIOU\d\W][aeiou][^aeiouAEIOU\d\W]$/);

const tumIdPopover = new bootstrap.Popover($('#tumId'), {trigger: 'manual'});

$('.checkTumIdForm').on('submit', (e) => {
    if (!tumIdRegex.test($('[name="tumId"]').val())) {
        e.preventDefault();
        tumIdPopover.show();
        setTimeout(() => {
            tumIdPopover.hide()
        }, 1000);
    }
});
