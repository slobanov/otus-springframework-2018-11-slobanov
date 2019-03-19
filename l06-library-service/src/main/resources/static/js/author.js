var context_author;

window.onload = () => {
    var authorId = entity_id();
    $.get(`/api/v2/author/${authorId}`).done(accept_author);
    $.get(`/api/v2/author/${authorId}/books`).done(display_books)

    $("form#remove-author").attr("action", "javascript:remove_author()");
};

accept_author = (author) => {
    context_author = author;
    $("td#first-name").text(author.firstName);
    $("td#last-name").text(author.lastName);
};

remove_author = () => {
    $.ajax({
        url: `/api/v2/author/${context_author.id}`,
        type: 'DELETE',
        success: () => window.location.href = "/authors"
    });
};