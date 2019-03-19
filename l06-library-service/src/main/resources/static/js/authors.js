window.onload = () => {
    $.get("/api/v2/author").done(display_authors);

    $("form#add-author-form").attr("action", "javascript:add_new_author()");
};

display_authors = (authors) => authors.forEach(display_author);
display_author  = (author) => $("tbody#authors").append(author_string(author));

author_string = (author) => `
    <tr>
        <td class="col-2">
            <a href="/author/${author.id}">${author.id}</a>
        </td>
        <td>${author.firstName}</td>
        <td>${author.lastName}</td>
    </tr>
`;

// ========================================

form_input_value = name => $(`form#add-author-form input[name="${name}"]`).val();
add_new_author = () => {
    var first_name = form_input_value("firstName");
    var last_name = form_input_value("lastName");

    $.post(
        "/api/v2/author",
        {
            "firstName": first_name,
            "lastName": last_name
        },
        (author) => window.location.href = `/author/${author.id}`
    );
};