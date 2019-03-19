display_books = (books) => books.forEach(display_book);
display_book = (book) => $("tbody#books").append(book_string(book));
book_string = book => `
    <tr>
        <td><a href="/book/${book.isbn}">${book.isbn}</a></td>
        <td>${book.title}</td>
        <td>
            ${list_authors(book)}
        </td>
        <td>
            ${list_genres(book)}
        </td>
    </tr>
`;

author_display = author => `${author.firstName} ${author.lastName} [${author.id}]`;

author_string = author => `
    <div>
        <a href="/author/${author.id}">${author_display(author)}</a>
    </div>
`;

genre_string = genre => `
    <div>
        <a href="/genre/${genre.name}">${genre.name}</a>
    </div>
`;

list_string = (list, func) => list.map(func).join("\n");
list_authors = (book) => list_string(book.authors, author_string);
list_genres = (book) => list_string(book.genres, genre_string);

// ========================================

display_options = (id, list, value_mapper, text_mapper) => {
    var tag = $(`select#${id}`);
    tag.empty();
    list.forEach(el => add_option(tag, el, value_mapper, text_mapper));
};
add_option = (tag, obj, value_mapper, text_mapper) => {
    tag.append(`
        <option value="${value_mapper(obj)}">
            ${text_mapper(obj)}
        </option>
    `);
};

display_author_options = (authors) => display_options(
    "authorsIds",
    authors,
    a => a.id,
    a => author_display(a)
);

display_genre_options = (genres) => display_options(
    "genres",
    genres,
    g => g.name,
    g => g.name
);

// ========================================

entity_id = () => $(location).attr("href").split("/").slice(-1).pop();