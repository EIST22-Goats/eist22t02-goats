const passwordErrorPopover = new bootstrap.Popover($('#password2'), {trigger: 'manual'});

$('.checkSecondPasswordForm').on('submit', (e) => {
    let password1 = $('[name="password"]').val();
    let password2 = $('[name="password2"]').val();
    if (password1 !== password2) {
        e.preventDefault();
        passwordErrorPopover.show();
        setTimeout(() => {
            passwordErrorPopover.hide()
        }, 1000);
    }
});
