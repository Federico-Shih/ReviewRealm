document.addEventListener("DOMContentLoaded", function(event) {
    document.querySelectorAll(".feedback-form").forEach(function (form) {
        form.addEventListener("submit", function (e) {
            baseurl = form.url.value;
            form.url.value = (window.location.pathname+window.location.search).replace(baseurl,"/");
        });

    });
});