/**
 * Returns the date stored by object, in string format (yyyy-MM-dd)
 * @param date The date object from which to extract a string formatted date
 * @returns {string} The string formatted date (yyyy-MM-dd) extracted from the date object
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
    let startDate = document.getElementById("project-form__start-date-field").valueAsNumber;
    startDate = new Date(startDate);


    let minEndDate = new Date();
    if (restrictedEndDate !== null) {
        // Min end date is the day the last project date(event, evidence, sprint, etc) ends
        minEndDate.setTime(restrictedEndDate);
    } else {
        // Min end date is one day after start date
        minEndDate.setTime(startDate.getTime() + (24 * 60 * 60 * 1000));
    }

    document.getElementById("project-form__end-date-field").setAttribute('min', getDateString(minEndDate));
}


/**
 * Updates the latest start date which a project may be given.
 * A project may not start on or after the date on which it ends.
 * It must also start before any project dates (sprints, evidence, milestone)
 */
function updateMaxStartDate() {
    let endDate = document.getElementById("project-form__end-date-field").valueAsNumber;
    endDate = new Date(endDate);


    let maxStartDate = new Date();
    if (restrictedStartDate !== null) {
        // Max start date is the day of the first project date (sprint, evidence, milestone, etc)
        maxStartDate.setTime(restrictedStartDate);
    } else {
        // Max start date is one day before end date
        maxStartDate.setTime(endDate.getTime() - (24 * 60 * 60 * 1000));
    }


    document.getElementById("project-form__start-date-field").setAttribute('max', getDateString(maxStartDate));
}


/**
 * Update the min end date of the project when the page loads
 */
window.addEventListener('load', (event) => {
   updateMinEndDate();
   updateMaxStartDate();
});