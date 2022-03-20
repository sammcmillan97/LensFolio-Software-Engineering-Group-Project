/*
 * Accordion element behaviour.
 * Note that visibility of accordion element children is controlled by the element's styling.
 */


/**
 * Collapses an element which acts as an accordion
 * @param element The accordion element to collapse
 */
function collapse(element) {
    element.classList.remove("expanded");
    element.classList.add("collapsed");
}


/**
 * Expands an element which acts as an accordion
 * @param element The accordion element to expand
 */
function expand(element) {
    element.classList.remove("collapsed");
    element.classList.add("expanded");
}


/**
 * Toggles collapsed/expanded state of an element acting as an accordion
 * @param element The accordion element who's state to toggle
 */
function toggleAccordion(element) {
    if (element.classList.contains("collapsed")) {
        expand(element);
    } else {
        collapse(element);
    }
}


/**
 * Toggle project accordion when project name is clicked
 */
document.addEventListener('click', (e) => {
    let element = e.target;

    // Check if a project name was clicked
    if (element.classList.contains("project__name")) {
        // Get the project element of the project who's name was clicked
        // And toggle accordion
        let project = element.closest(".project");

        // If collapsing project accordion
        // Also collapse all of it's sprints
        if (project.classList.contains("expanded")) {
            let sprints = project.querySelectorAll(".sprint");
            sprints.forEach(collapse);
            collapse(project);
        } else {
            expand(project);
        }
    }
});

/**
 * Toggle sprint accordion when sprint label is clicked
 */
document.addEventListener('click', (e) => {
    let element = e.target;

    // Check if a project name was clicked
    if (element.classList.contains("sprint__name")) {
        // Get the sprint element of the sprint who's name was clicked
        // And toggle accordion
        let sprint = element.closest(".sprint");
        toggleAccordion(sprint);
    }
});