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
    let startDate = document.getElementById("sprint-form__start-date-field").valueAsNumber;
    startDate = new Date(startDate);

    let minEndDate = new Date();
    minEndDate.setDate(startDate.getDate() + 1);

    document.getElementById("sprint-form__end-date-field").setAttribute('min', getDateString(minEndDate));
}

/**
 * Update the min end date of the project when the page loads
 */
window.addEventListener('load', (event) => {
    updateMinEndDate();
});