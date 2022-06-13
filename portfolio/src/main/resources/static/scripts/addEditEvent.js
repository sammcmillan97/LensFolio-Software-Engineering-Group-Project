
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
 * Updates the soonest end date which an event may be given.
 * An event may not end before the date on which it begins.
 */
function updateMinEndDate() {
    let startDate = document.getElementById("event-form__start-date-field").value;
    document.getElementById("event-form__end-date-field").setAttribute('min', startDate);
}

/**
 * Updates the latest start date which an event may be given.
 * An event may not start after the date on which it ends.
 */
function updateMaxStartDate() {
    let endDate = document.getElementById("event-form__end-date-field").value;
    document.getElementById("event-form__start-date-field").setAttribute('max', endDate);
}

/**
 * Update the min end date and max start date of the event when the page loads
 */
window.addEventListener('load', (event) => {
    updateMinEndDate();
    updateMaxStartDate();
    countCharacters();
});