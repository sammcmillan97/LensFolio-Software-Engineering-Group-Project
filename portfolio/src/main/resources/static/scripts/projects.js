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
    let days = date.getDate();
    let months = date.getMonth() + 1; //January is 0!
    let years = date.getFullYear();

    // Ensure day and month are two characters wide
    if (days < 10) {
        days = '0' + days;
    }
    if (months < 10) {
        months = '0' + months;
    }

    return years + '-' + months + '-' + days;
}


/**
 * Updates the soonest end date which a project may be given.
 * A project may not end on or before the date on which it begins.
 */
function updateMinEndDate() {
    let startDate = document.getElementById("add-edit__start-date-field").valueAsNumber;
    let endDate = new Date(startDate);
    endDate.setDate(endDate.getDate() + 1);

    document.getElementById("add-edit__end-date-field").setAttribute('min', getDateString(endDate));
}

/**
 * Parses the dates into a readable format and calls the function to fill in the edit/add form
 * and make it visible
 *
 * @param project_id The project's ID
 * @param project_name The project's name
 * @param project_description The project's description
 * @param start_date_string The project's start date as a string
 * @param end_date_string The project's end date as a string
 */
function editProject(project_id, project_name, project_description, start_date_string, end_date_string) {

    // Calculate the start and end dates and convert from UTC to NZT
    let start_date = new Date(Date.parse(start_date_string));
    start_date.setUTCHours(start_date.getUTCHours() - start_date.getTimezoneOffset() / 60);
    let end_date = new Date(Date.parse(end_date_string));
    end_date.setUTCHours(end_date.getUTCHours() - start_date.getTimezoneOffset() / 60);

    let form_title = "Edit Project";

    openAddEditForm(form_title, project_id, project_name, project_description, start_date, end_date);


}

/**
 * Calculates the default project values then calls the function to fill in the edit/add form
 * and make it visible
 */
function addProject() {

    // Calculate the start and end dates
    // Start date is today
    // End date is 8 months from now
    let start_date = new Date();
    start_date.setUTCHours(start_date.getUTCHours() - start_date.getTimezoneOffset() / 60);

    let end_date = new Date(start_date.getTime());
    end_date.setUTCMonth(end_date.getUTCMonth() + 8);

    // Assign default project values
    let project_id = "";
    let project_description = "";
    let project_name = "Project " + start_date.getFullYear();
    let form_title = "Add Project";


    openAddEditForm(form_title, project_id, project_name, project_description, start_date, end_date);
}


/**
 * Fills in the form elements then makes the form visible
 * Calculates the earliest dates that dates can be set to and applies limits to those inputs
 *
 * @param form_title The title of the form (either 'Add Project' or 'Edit Project')
 * @param project_id The project's ID
 * @param project_name The project's name
 * @param project_description The project's description
 * @param start_date The project's start date
 * @param end_date The project's end date
 */
function openAddEditForm(form_title, project_id, project_name, project_description, start_date, end_date) {

    // Set the form title
    document.getElementById("add-edit__title").innerHTML = "<h1>" + form_title + "</h1>"

    // Populate form inputs with project name and description
    document.getElementById("add-edit__name-field").value = project_name;
    document.getElementById("add-edit__description-field").value = project_description;

    // Insert start and end dates
    document.getElementById("add-edit__start-date-field").valueAsNumber = start_date;
    document.getElementById("add-edit__end-date-field").valueAsNumber = end_date;

    // Set the id of the project being edited
    document.getElementById("add-edit__project-id").value = project_id;

    // Display the form and grey out other page content using overlay
    document.getElementById("add-edit__popup").classList.remove("hidden");
    document.getElementById("add-edit__overlay").classList.remove("hidden");

    // Set minimum end date to the day after the current start date
    updateMinEndDate();

    // Set the minimum start date to one year before today
    let min_date = new Date();
    min_date.setFullYear(min_date.getFullYear() - 1);
    document.getElementById("add-edit__start-date-field").setAttribute('min', getDateString(min_date));
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