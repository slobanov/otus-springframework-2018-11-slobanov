window.onload = () => {

    var isError = new URLSearchParams(window.location.search).has("error");
    if (isError) {
        $("main").prepend(errorBar)
    }
};

errorBar = `
    <div class="col-4 error-bar">Incorrect username or password.</div>
`