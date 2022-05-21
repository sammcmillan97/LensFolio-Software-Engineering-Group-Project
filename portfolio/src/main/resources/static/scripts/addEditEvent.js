/**
 * Returns the date stored by object, in string format (yyyy-MM-dd)
 * @param date The date object from which to extract a string formatted date
 * @returns {string} The string formatted date (yyyy-MM-dd) extracted from the date object
 */
const maxNumOfChars = 30;

/**
 * Simple function that updates char_count field to reflect how many characters are remaining for the
 * user to use
 */
function countCharacters() {
    let numOfEnteredChars = document.getElementById("event-form__name-field").value.length;
    let characterCounter = document.getElementById("char_count")
    characterCounter.textContent = String(maxNumOfChars - numOfEnteredChars) + "/30 characters remain";
}

/**
 * Gets the date in a string format
 * @param date is the date to be converted to a string format
 * @returns {string} format of the date
 */
function getDateString(date) {
    let days = date.getDate();
    let months = date.getMonth() + 1; //January is 0!
    let years = date.getFullYear();
    let hours = date.getHours();
    let minutes = date.getMinutes();
    let seconds = date.getSeconds();
    let milliseconds = date.getMilliseconds();

    // Ensure day and month are two characters wide
    if (days < 10) {
        days = '0' + days;
    }
    if (months < 10) {
        months = '0' + months;
    }

    return years + '-' + months + '-' + days + 'T' + hours + ':' + minutes;
}


/**
 * Updates the soonest end date which an event may be given.
 * An event may not end before the date on which it begins.
 */
function updateMinEndDate() {
    let startDate = document.getElementById("event-form__start-date-field").valueAsNumber;
    startDate = new Date(startDate);
    let minEndDate = new Date();
    minEndDate.setTime(startDate.getTime());
    document.getElementById("event-form__end-date-field").setAttribute('min', getDateString(minEndDate));
}

/**
 * Updates the latest start date which an event may be given.
 * An event may not start after the date on which it ends.
 */
function updateMaxStartDate() {
    let endDate = document.getElementById("event-form__end-date-field").valueAsNumber;
    endDate = new Date(endDate);
    let maxStartDate = new Date();
    maxStartDate.setTime(endDate.getTime());
    document.getElementById("event-form__start-date-field").setAttribute('max', getDateString(maxStartDate));
}

/**
 * Update the min end date and max start date of the event when the page loads
 */
window.addEventListener('load', (event) => {
    updateMinEndDate();
    updateMaxStartDate();
    countCharacters();
});