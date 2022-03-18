document.addEventListener('click', (e) => {
    let element = e.target;

    if (element.classList.contains("project__name")) {
        let project = element.parentNode;
        if (project.classList.contains("collapsed")) {
            project.classList.remove("collapsed");
            project.classList.add("expanded");
        } else {
            project.classList.remove("expanded");
            project.classList.add("collapsed");
        }
    }
})