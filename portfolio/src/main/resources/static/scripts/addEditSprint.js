/**
 * Updates the soonest end date which a sprint may be given.
 */
function updateMinEndDate() {
    let startDate = document.getElementById("sprint-form__start-date-field").valueAsNumber;
    document.getElementById("sprint-form__end-date-field").setAttribute('min', startDate);
}

/**
 * Updates the latest start date which a sprint may be given.
 */
function updateMaxStartDate() {
    let endDate = document.getElementById("sprint-form__end-date-field").valueAsNumber;
    document.getElementById("sprint-form__start-date-field").setAttribute('max', getDateString(endDate));
}

/**
 * Update the min end date and max start date of the sprint when the page loads
 */
window.addEventListener('load', (event) => {
    updateMinEndDate();
    updateMaxStartDate();
});