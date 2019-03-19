var context_genre;

window.onload = () => {
    context_genre = entity_id();
    accept_genre(context_genre);

    $.get(`/api/v2/genre/${context_genre}/books`).done(display_books);
    $("form#remove-genre").attr("action", "javascript:remove_genre()");
};

accept_genre = (genre) => {
    $("td#genre").text(decodeURIComponent(genre));
};

remove_genre = () => {
    $.ajax({
        url: `/api/v2/genre/${context_genre}`,
        type: 'DELETE',
        success: () => window.location.href = "/genres"
    });
};