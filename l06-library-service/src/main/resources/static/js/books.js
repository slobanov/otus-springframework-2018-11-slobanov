window.onload = () => {
    $.get("/api/v2/books").done(display_books);
    $.get("/api/v2/authors").done(display_author_options);
    $.get("/api/v2/genres").done(display_genre_options);

    [checkAuthors, checkGenres].map(f => $("form#add-book-form").submit(f));
    $("form#add-book-form").attr("action", "javascript:add_new_book()");
}

// ========================================

form_input_value = name => $(`form#add-book-form input[name="${name}"]`).val();
form_selected_values = name => $(`form#add-book-form select[name=${name}] option:selected`).get().map(o => o.value);

add_new_book = (q) => {
    var isbn = form_input_value("isbn");
    var title = form_input_value("title");
    var authorIds = form_selected_values("authorIds");
    var genres = form_selected_values("genres");


    $.ajax({
        type: "POST",
        url: "/api/v2/book/add",
        traditional: true,
        data: {
            "isbn": isbn,
            "title": title,
            "authorIds": authorIds,
            "genres": genres
        },
        success: book => window.location.href = `/book/${book.isbn}`
    });

};

// ========================================

checkSelect = (name) => {
    var selectTag = $(`#${name}`);
    var isValid = $(`#${name} > option:selected`).length > 0;
    if (!isValid) {
        selectTag.addClass("error");
    } else {
        selectTag.removeClass("error");
    }
    return isValid;
};

checkAuthors = () => checkSelect("authorsIds");
checkGenres = () => checkSelect("genres");