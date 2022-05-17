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
 * Updates the soonest end date which a sprint may be given.
 * A sprint may not end on or before the date on which it begins.
 */
function updateMinEndDate() {
    let startDate = document.getElementById("sprint-form__start-date-field").valueAsNumber;
    startDate = new Date(startDate);

    // Min end date is one day after start date
    let minEndDate = new Date();
    minEndDate.setTime(startDate.getTime() + (1 * 24 * 60 * 60 * 1000));

    document.getElementById("sprint-form__end-date-field").setAttribute('min', getDateString(minEndDate));
}

/**
 * Updates the latest start date which a sprint may be given.
 * A sprint may not start on or after the date on which it ends.
 */
function updateMaxStartDate() {
    let endDate = document.getElementById("sprint-form__end-date-field").valueAsNumber;
    endDate = new Date(endDate);

    // Max start date is one day before end date
    let maxStartDate = new Date();
    maxStartDate.setTime(endDate.getTime() - (1 * 24 * 60 * 60 * 1000));

    document.getElementById("sprint-form__start-date-field").setAttribute('max', getDateString(maxStartDate));
}

/**
 * Update the min end date and max start date of the sprint when the page loads
 */
window.addEventListener('load', (event) => {
    updateMinEndDate();
    updateMaxStartDate();
});

function callme(){
//This promise will resolve when the network call succeeds
//Feel free to make a REST fetch using promises and assign it to networkPromise
const options = {
method: 'POST'
};
var networkPromise = fetch('/projects/editing?id=1', options);

//This promise will resolve when 2 seconds have passed
var timeOutPromise = new Promise(function(resolve, reject) {
  // 2 Second delay
  setTimeout(resolve, 2000, 'Timeout Done');
});

Promise.all(
[networkPromise, timeOutPromise]).then(function(values) {
  console.log("We goin");
  callme();
});
}
callme();