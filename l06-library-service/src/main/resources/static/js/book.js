var context_book;

window.onload = () => {
    var book_promise = $.get(`/api/v2/book/${entity_id()}`);

    book_promise.then(accept_book);
    book_promise.then(display_suitable_author_options);
    book_promise.then(display_suitable_genre_options);

    $("form#remove-book").attr("action", "javascript:remove_book()");
    $("form#add-author").attr("action", "javascript:add_author()");
    $("form#add-genre").attr("action", "javascript:add_genre()");
    $("form#add-comment").attr("action", "javascript:add_comment()");
};

accept_book = (book) => {
    context_book = book;

    display_comments(book);

    $("td#isbn").text(book.isbn);
    $("td#title").text(book.title);

    display_authors(book);
    display_genres(book);
};

author_block = (book) => list_authors(book);
display_authors = (book) => {
    var td_authors = $("td#authors");
    td_authors.empty();
    td_authors.append(author_block(book));
};

genre_block = (book) => list_genres(book);
display_genres = (book) => {
    var td_genres = $("td#book-genres");
    td_genres.empty();
    td_genres.append(genre_block(book)); 
};

display_comments = (book) => {
    var td_comments = $("td#comments");
    td_comments.empty();
    $.get(`/api/v2/comments/${book.isbn}`).done(
        comments => td_comments.append(list_string(comments, comment_string))
    );
};

comment_string = (comment) => `
    <div>
        <span>${comment.text}</span>
    </div>
`;

// ========================================

remove_book = () => {
    $.ajax({
        url: `/api/v2/book/${context_book.isbn}`,
        type: 'DELETE',
        success: () => window.location.href = "/books"
    });
};

display_suitable_author_options = (book) => {
    $.get(`/api/v2/authors`).done(authors => {
        var bookAuthorIds = book.authors.map(a => a.id);
        display_author_options(authors.filter(a => bookAuthorIds.indexOf(a.id) === -1));
    });
};

display_suitable_genre_options = (book) => {
    $.get(`/api/v2/genres`).done(genres => {
        var bookGenres = book.genres.map(a => a.name);
        display_genre_options(genres.filter(g => bookGenres.indexOf(g.name) === -1));
    });
};

add_author = () => {
    var authorId = $("select#authorsIds option:selected").val();
    $.post(
        `/api/v2/book/${context_book.isbn}/addAuthor`,
        { "authorId": authorId },
        (book) => {
            context_book = book;
            display_authors(book);
            display_suitable_author_options(book);
            $("form#add-author").trigger("reset");
        }
    );
};

add_genre = () => {
    var genre = $("select#genres option:selected").val();
    $.post(
        `/api/v2/book/${context_book.isbn}/addGenre`,
        { "genre": genre },
        (book) => {
            context_book = book;
            display_genres(book);
            display_suitable_genre_options(book);
            $("form#add-genre").trigger("reset");
        }
    );
};

add_comment = () => {
    var comment_text = $("textarea#comment-text").val();
    $.post(
        `/api/v2/book/${context_book.isbn}/addComment`,
        { "comment": comment_text },
        (book) => {
            context_book = book;
            display_comments(book);
            $("form#add-comment").trigger("reset");
        }
    );
}