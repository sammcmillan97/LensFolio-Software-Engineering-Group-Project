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
    if (projectLastSprintEndDate !== null) {
        // Min end date is the day the last sprint ends
        minEndDate.setTime(projectLastSprintEndDate);
    } else {
        // Min end date is one day after start date
        minEndDate.setTime(startDate.getTime() + (24 * 60 * 60 * 1000));
    }

    document.getElementById("project-form__end-date-field").setAttribute('min', getDateString(minEndDate));
}


/**
 * Updates the latest start date which a project may be given.
 * A project may not start on or after the date on which it ends.
 * It must also start before any sprints
 */
function updateMaxStartDate() {
    let endDate = document.getElementById("project-form__end-date-field").valueAsNumber;
    endDate = new Date(endDate);


    let maxStartDate = new Date();
    if (projectFirstSprintStartDate !== null) {
        // Max start date is the day the first sprint starts
        maxStartDate.setTime(projectFirstSprintStartDate);
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

function callme(){
//This promise will resolve when the network call succeeds
//Feel free to make a REST fetch using promises and assign it to networkPromise
const options = {
method: 'POST'
};
var networkPromise = fetch('http://localhost:9000/projects/1/editing', options);

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