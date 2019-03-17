window.onload = () => {
    $.get("/api/v2/genres").done(display_genres);

    $("form#add-genre-form").attr("action", "javascript:add_new_genre()");
};

display_genres = (genres) => genres.forEach(display_genre);
display_genre = (genre) => $("tbody#genres").append(genre_string(genre));

add_new_genre = () => {
    $.post(
        `/api/v2/genre/add`,
        { "genre": $(`form#add-genre-form input[name="genre"]`).val() },
        (genre) => window.location.href = `/genre/${genre.name}`
    );
};