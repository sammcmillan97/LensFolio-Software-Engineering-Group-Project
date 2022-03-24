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
 * Updates the soonest end date which a sprint may be given.
 * A project may not end on or before the date on which it begins.
 */
function updateMinSprintEndDate() {
    let startDate = document.getElementById("sprint-form__start-date-field").valueAsNumber;
    let endDate = new Date(startDate);
    endDate.setDate(endDate.getDate() + 1);

    document.getElementById("sprint-form__end-date-field").setAttribute('min', getDateString(endDate));
}

/**
 * Parses the dates into a readable format and calls the function to fill in the edit/add form
 * and make it visible
 *
 * @param parentProjectId The project's ID
 * @param sprintId The id of the sprint to be edited
 * @param name The project's name
 * @param description The project's description
 * @param startDate The project's start date as a string
 * @param endDate The project's end date as a string
 */
function editSprint(parentProjectId, sprintId, name, description, startDate, endDate) {

    let formTitle = "Edit Sprint";

    // Calculate the start and end dates and convert from UTC to NZT
    let start_date = new Date(Date.parse(startDate));
    start_date.setUTCHours(start_date.getUTCHours() - start_date.getTimezoneOffset() / 60);
    let end_date = new Date(Date.parse(endDate));
    end_date.setUTCHours(end_date.getUTCHours() - start_date.getTimezoneOffset() / 60);

    openSprintForm(formTitle, parentProjectId, sprintId, name, description, start_date, end_date);
}

/**
 * Calculates the default project values then calls the function to fill in the edit/add form
 * and make it visible
 *
 * @param parentProjectId The id of the Parent Project for which to add a sprint
 */
function addSprint(parentProjectId) {

    // Get the sprint number and set the sprint label
    let sprintCount = parseInt(document.getElementById("sprint-count").value);
    let label = "Sprint " + (sprintCount + 1);
    document.getElementById("sprint-form__label").value = label;

    // Assign default project values
    let sprintId = "";
    let name = label;
    let description = "";
    let formTitle = "Add Sprint";

    // Calculate the start and end dates
    // Start date is project start date or day after previous sprint end
    let startDate = new Date();
    startDate.setUTCHours(startDate.getUTCHours() - startDate.getTimezoneOffset() / 60);
    document.getElementById("sprint-form__start-date-field").setAttribute("min", getDateString(startDate));

    // Default end date is 3 weeks after start
    let endDate = new Date(startDate.getDate() + (3 * 7));

    openSprintForm(formTitle, parentProjectId, sprintId, name, description, startDate, endDate);
}


/**
 * Fills in the form elements then makes the form visible
 * Calculates the earliest dates that dates can be set to and applies limits to those inputs
 *
 * @param formTitle The title of the form (either 'Add Project' or 'Edit Project')
 * @param parentProjectId The project's ID
 * @param sprintId The id of the sprint to be edited
 * @param name The project's name
 * @param description The project's description
 * @param startDate The project's start date
 * @param endDate The project's end date
 */
function openSprintForm(formTitle, parentProjectId, sprintId, name, description, startDate, endDate) {

    // Set the form title
    document.getElementById("sprint-form__title").innerText = formTitle;

    // Populate form inputs with project name and description
    document.getElementById("sprint-form__name-field").value = name;
    document.getElementById("sprint-form__description-field").value = description;

    // Insert start and end dates
    document.getElementById("sprint-form__start-date-field").valueAsNumber = startDate;
    document.getElementById("sprint-form__end-date-field").valueAsNumber = endDate;

    // Set the id of the project being edited
    document.getElementById("sprint-form__project-id").value = parentProjectId;
    document.getElementById("sprint-form__sprint-id").value = sprintId;

    // Display the form and grey out other page content using overlay
    document.getElementById("sprint-form__popup").classList.remove("hidden");

    // Set minimum end date to the day after the current start date
    updateMinSprintEndDate();

    // Set the minimum start date to one year before today
    let min_date = new Date();
    min_date.setFullYear(min_date.getFullYear() - 1);
    document.getElementById("sprint-form__start-date-field").setAttribute('min', getDateString(min_date));
}


/**
 * Closes the Add/Edit projects form.
 * Always returns false, to signal that the form should not be submitted.
 * @returns {boolean} false
 */
function closeSprintForm() {
    // Hide the form and overlay by adding class 'hidden' to container
    document.getElementById("sprint-form__popup").classList.add("hidden");

    // Return false to prevent form submission
    return false;
}