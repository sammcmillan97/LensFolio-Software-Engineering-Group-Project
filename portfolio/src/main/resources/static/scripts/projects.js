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
        let project = element.parentNode;
        toggleAccordion(project);
    }
});



/*
 * Add/Edit Project form behaviour
 */


/**
 * Returns the date stored by object, in string format (yyyy-mm-dd)
 * @param date The date object from which to extract a string formatted date
 * @returns {string} The string formatted date (yyyy-mm-dd) extracted from the date object
 */
function getDateString(date) {
    let dd = date.getDate();
    let mm = date.getMonth() + 1; //January is 0!
    let yyyy = date.getFullYear();

    // Ensure day of month 2 characters wide
    if (dd < 10) {
        dd = '0' + dd;
    }

    // Ensure month of year 2 characters wide
    if (mm < 10) {
        mm = '0' + mm;
    }

    return yyyy + '-' + mm + '-' + dd;
}


/**
 * Updates the soonest end date which a project may be given.
 * A project may not end on or before the date on which it begins.
 */
function updateMinEndDate() {
    let startDate = document.getElementById("add-edit__start-date-field").valueAsNumber;
    let endDate = new Date(startDate);
    endDate.setUTCDate(endDate.getUTCDate() + 1);

    document.getElementById("add-edit__end-date-field").setAttribute('min', getDateString(endDate));
}

// // Whenever the start date field of the Add/Edit Project form changes, update the end date field's minimum
// document.getElementById("add-edit__start-date-field").onchange = function () {
//     updateMinEndDate();
// };


/**
 * Opens the Add/Edit Project form, filling form inputs with the existing values of the desired project.
 *
 * @param id The project's id
 * @param name The project's name
 * @param description The project's description
 * @param startDateString The project's start date (Date object)
 * @param endDateString The project's end date (Date object)
 */
function openAddEditForm(id, name, description, startDate, endDate) {
    // Display the form and grey out other page content using overlay
    document.getElementById("add-edit__popup").classList.remove("hidden");
    document.getElementById("add-edit__overlay").classList.remove("hidden");

    // Set id of project being edited
    document.getElementById("add-edit__project-id").value = id;

    // Populate form inputs with provided values
    document.getElementById("add-edit__name-field").value = name;
    document.getElementById("add-edit__description-field").value = description;

    // Dates
    startDate.setUTCHours(startDate.getUTCHours() + 13);
    document.getElementById("add-edit__start-date-field").setAttribute('min', getDateString(startDate));
    document.getElementById("add-edit__start-date-field").valueAsNumber = startDate;

    endDate.setUTCHours(endDate.getUTCHours() + 13);
    document.getElementById("add-edit__end-date-field").setAttribute('min', getDateString(endDate));
    document.getElementById("add-edit__end-date-field").valueAsNumber = endDate;
}


/**
 * Closes the Add/Edit projects form.
 * Always returns false, to signal that the form should not be submitted.
 * @returns {boolean} false
 */
function closeAddEditForm() {
    // Hide the form and overlay by adding class 'hidden' to container
    document.getElementById("add-edit__popup").classList.add("hidden");

    // Return false to prevent form submission
    return false;
}

// // When the Add/Edit form discard button is clicked, close the form without submitting
// document.getElementById("add-edit__discard").onclick = function () {
//     return closeAddEditForm();
// };

/**
 * Opens the Add/Edit form for a new project, populating input fields with default project details.
 */
function addProject() {
    let startDate = new Date();
    startDate.setUTCHours(startDate.getUTCHours() + 13);
    let endDate = new Date(startDate.getTime());
    endDate.setUTCMonth(endDate.getUTCMonth() + 8);

    openAddEditForm(null, "Project " + startDate.getFullYear(), "", startDate, endDate);
}

// // When the Add Project button is clicked, open the Add/Edit form for a new project
// document.getElementById("new-project-button").onclick = function () {
//     addProject();
// }