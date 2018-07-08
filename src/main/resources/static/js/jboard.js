function showThreadForm() {
    $('#thread-form-modal').modal('show');
}


function showReplyForm(threadId) {
    var replyForm = $('#reply-form');
    replyForm.attr("action", replyForm.attr("data-board") + threadId + "/reply");
    $('#reply-form-modal').modal('show');
}


$(function () {

    $('#reply-form, #thread-form')
        .form({
            fields: {
                name: {
                    identifier: 'name',
                    rules: [
                        {
                            type: 'maxLength',
                            value: lengths.name,
                            prompt: 'Subject too long (allowed ' + lengths.name + ' chars)'
                        }
                    ]
                },
                tripcodePassword: {
                    identifier: 'password',
                    rules: [
                        {
                            type: 'maxLength',
                            value: lengths.tripcodePassword,
                            prompt: 'Subject too long (allowed ' + lengths.tripcodePassword + ' chars)'
                        }
                    ]
                },
                subject: {
                    identifier: 'subject',
                    rules: [
                        {
                            type: 'maxLength',
                            value: lengths.subject,
                            prompt: 'Subject too long (allowed ' + lengths.subject + ' chars)'
                        }
                    ]
                },
                body: {
                    identifier: 'body',
                    rules: [
                        {
                            type: 'maxLength',
                            value: lengths.post,
                            prompt: 'Subject too long (allowed ' + lengths.post + ' chars)'
                        }
                    ]
                }
            }
        })
    ;

    $('#registration-form')
        .form({
            fields: {
                username: {
                    identifier: 'username',
                    rules: [
                        {
                            type: 'minLength',
                            value: lengths.usernameMin,
                            prompt: 'Username too short (allowed ' + lengths.usernameMin + ' chars)'
                        },
                        {
                            type: 'maxLength',
                            value: lengths.username,
                            prompt: 'Subject too long (allowed ' + lengths.username + ' chars)'
                        }
                    ]
                },
                password: {
                    identifier: 'password',
                    rules: [
                        {
                            type: 'minLength',
                            value: lengths.passwordMin,
                            prompt: 'Password too short (must be at least ' + lengths.passwordMin + ' chars)'
                        }
                    ]
                },
                passwordRepeated: {
                    identifier: 'passwordRepeated',
                    rules: [
                        {
                            type: 'match',
                            value: 'password',
                            prompt: 'Passwords do not match'
                        }
                    ]
                },
                email: {
                    identifier: 'email',
                    rules: [
                        {
                            type: 'email',
                            prompt: 'Email is not valid'
                        }
                    ]
                }
            }
        })
    ;

    $(".item img").click(function (e) {
        e.preventDefault();
        $(this).toggleClass("fullsize");
    });

    $('.ui.checkbox')
        .checkbox()
    ;

});

