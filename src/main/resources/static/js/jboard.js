function showThreadForm() {
    $('#thread-form-modal').modal('show');
}


function showReplyForm(threadId) {
    var replyForm = $('#reply-form');
    replyForm.attr("action", replyForm.attr("data-board") + threadId + "/reply");
    $('#reply-form-modal').modal('show');
}


$(function () {

    $('.ui.form')
        .form({
            fields: {
                name: {
                    identifier: 'name',
                    rules: [
                        {
                            type: 'maxLength',
                            value: lengths.name,
                            prompt: 'Subject too long (allowed ' + lengths.name + ' chars)',
                        }
                    ]
                },
                password: {
                    identifier: 'password',
                    rules: [
                        {
                            type: 'maxLength',
                            value: lengths.password,
                            prompt: 'Subject too long (allowed ' + lengths.password + ' chars)',
                        }
                    ]
                },
                subject: {
                    identifier: 'subject',
                    rules: [
                        {
                            type: 'maxLength',
                            value: lengths.subject,
                            prompt: 'Subject too long (allowed ' + lengths.subject + ' chars)',
                        }
                    ]
                },
                body: {
                    identifier: 'body',
                    rules: [
                        {
                            type: 'maxLength',
                            value: lengths.post,
                            prompt: 'Subject too long (allowed ' + lengths.post + ' chars)',
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
});

