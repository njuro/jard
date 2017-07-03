$(document).ready(function () {
    $('#thread-form-btn').click(function () {
        $('#thread-form-modal').modal('show');
    });

    $('.reply-form-btn').click(function () {
        var replyForm = $('#reply-form');
        replyForm.attr("action", replyForm.attr("data-board") + $(this).attr("data-thread-id") + "/reply");
        $('#reply-form-modal').modal('show');
    });
});