// Define variables that need to be used across functions
let calendarEl;
let calendar;

/**
 * Helper function to convert string of roles to a list
 * @param rolesListText
 * @returns {*[]}
 */
function convertTextToList(rolesListText) {
    let roles = [];
    let role = '';
    const arrayLength = rolesListText.length;
    for (let i = 1; i < arrayLength; i++) {
        if (rolesListText[i] === ',' || rolesListText[i] === ']'){
            roles.push(role);
            role = '';
        } else if (rolesListText[i] === ' '){
            //do nothing
        } else {
            role += rolesListText[i];
        }
    }
    return roles;
}

/**
 * Helper function to determine whether a user roles list contains admin or teacher
 * @returns {boolean}
 */
function isAdmin() {
    let isTeacherOrAdmin = false;
    let rolesListText = document.getElementById("user__rolesList").textContent;
    document.getElementById("user__rolesList").hidden = true;
    let rolesList = convertTextToList(rolesListText);
    const arrayLength = rolesList.length;
    for (let i = 0; i < arrayLength; i++) {
        if (rolesList[i] === "TEACHER" || rolesList[i] === "COURSE_ADMINISTRATOR") {
            isTeacherOrAdmin = true;
        }
    }
    return isTeacherOrAdmin;
}

/**
 * Adds an event listener to the page loading to create the calendar and set it to the project dates
 */
document.addEventListener('DOMContentLoaded', function() {

    const fullMonthStartDate = calculateFullMonthStartDate(projectStartDate);
    const fullMonthEndDate = calculateFullMonthEndDate(dayAfterProjectEndDate);
    $("body").tooltip({selector: '[data-toggle=tooltip]'});

    calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        themeSystem: 'bootstrap5',
        validRange: {
            start: fullMonthStartDate,
            end: fullMonthEndDate
        },
        events: [
            {
                name: 'Project',
                start: fullMonthStartDate,
                end: projectStartDate,
                display: 'background',
                backgroundColor: "#CCCCCC"
            },

            {
                name: 'Project',
                start: dayAfterProjectEndDate,
                end: fullMonthEndDate,
                display: 'background',
                backgroundColor: "#CCCCCC"
            },

            {
                title: projectName + " Starts",
                start: projectStartDate,
                end: projectStartDate,
                display: 'background',
                backgroundColor: "#F4EAE6"
            }, {
                title: projectName + " Ends",
                start: projectEndDate,
                end: projectEndDate,
                display: 'background',
                backgroundColor: "#F4EAE6"
            }
        ],
        initialView: 'dayGridMonth',
        initialDate: projectStartDate,
        //true when user is TEACHER or ADMIN
        editable: (isAdmin()),
        //disallow dragging entire event
        eventStartEditable: false,
        // lazyFetching: false,
        // showNonCurrentDates: false,
        //allow resizing of start/end date
        eventResizableFromStart: true,
        eventResizableFromEnd: true,

        eventOverlap: function( stillEvent, movingEvent) {
            return !(stillEvent.extendedProps.eventType === 'Sprint' && movingEvent.extendedProps.eventType === 'Sprint') && !(stillEvent.extendedProps.name === 'Project');
        },

        //Listens to sprint drag/drop
        eventResize: function (eventDropInfo) {
            resizeSprint( eventDropInfo );
        },

        eventDidMount: function (info) {
            console.log("Here: " + info.event.extendedProps.eventType)
            info.event.setProp("textColor", "black");
            if (info.event.extendedProps.eventType) {
                let parent = info.el.querySelector(".fc-event-title").parentElement;
                parent.style.display = 'flex';
                parent.style.justifyContent = 'flex-start';
                parent.style.alignItems = 'center';
                if (info.event.extendedProps.eventType === "daily-milestone") {
                    parent.parentElement.parentElement.parentElement.classList.add('milestonePlanner');
                    parent.insertBefore(createElementFromHTML(`<i data-toggle="tooltip"
                                                data-placement="top" data-html="true"
                                                title=${"'" + info.event.extendedProps.description + "'"} 
                                                class="bi bi-trophy-fill"></i>`), parent.firstChild);
                } else if (info.event.extendedProps.eventType === "daily-deadline") {
                    parent.parentElement.parentElement.parentElement.classList.add('deadlinePlanner');
                    parent.insertBefore(createElementFromHTML(`<i data-toggle="tooltip" 
                                                data-placement="top" data-html="true"
                                                title=${"'" + info.event.extendedProps.description + "'"} 
                                                class="bi bi-alarm-fill"></i>`), parent.firstChild);
                } else if (info.event.extendedProps.eventType === "daily-event") {
                    parent.parentElement.parentElement.parentElement.classList.add('eventPlanner');
                    parent.insertBefore(createElementFromHTML(`<i data-toggle="tooltip" 
                                                data-placement="top" data-html="true"
                                                title=${"'" + info.event.extendedProps.description + "'"} 
                                                class="bi bi-calendar-event-fill"></i>`), parent.firstChild);
                }
            }
        },
    });

    addSprintsToCalendar();
    addEventsToCalendar();
    calendar.render();
    let today = new Date();
    let time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
    if (paginationDate) {
        calendar.gotoDate(paginationDate);
        changeText('Changes Saved (last change made - ' + time + ')');
    } else {
        changeText('No Changes Made')
    }
});

function createElementFromHTML(htmlString) {
    let template = document.createElement('template');
    template.innerHTML = htmlString.trim();
    return template.content.firstChild;
}

/**
 * Calculates the date of the beginning of the given month
 * @param startDate The start date of a project
 * @returns {string} A date string of the beginning of the given month
 */
function calculateFullMonthStartDate(startDate) {
    return startDate.slice(0,8) + "01"
}

/**
 * Calculates the date of the beginning of the month after the given month
 * Date is not inclusive in fullcalendar library, hence why we need the day
 * after the end of the month.
 * endDate is already the day after the project ends, hence why we don't need
 * to calculate anything if endDate is already the first of the month
 * @param endDate The end date of a project
 * @returns {string} A date string of the beginning of the month after the given month
 */
function calculateFullMonthEndDate(endDate) {
    let month;

    // Check if project end date is already the first of the next month (end date is already day after the project end date)
    if (endDate.slice(8) !== "01") {
        // Increase the month by 1
        month = parseInt(endDate.slice(5, 7), 10) + 1;
    } else {
        return endDate;
    }

    // Ensure the month has two digits
    if (month <= 9) {
        month = "0" + month.toString();
    } else {
        month.toString();
    }

    // Calculate and return the end date
    return endDate.slice(0,5) + month + "-" + "01";
}

/**
 * Adds all the sprints in the list created by thymeleaf to the calendar
 */
function addSprintsToCalendar() {
    for (let sprint of sprints) {
        console.log(sprint)
        calendar.addEvent(sprint);
    }
}

function addEventsToCalendar() {
    for (let event of events) {
        calendar.addEvent(event)
    }
}

function resizeSprint( eventDropInfo ) {

    //Create form to post data from calendar
    let form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', `editPlanner-${eventDropInfo.oldEvent.id}-${projectId}`);

    //Add inputs to form
    let startInput = document.createElement('input');
    startInput.setAttribute('type', 'hidden');
    startInput.setAttribute('name', 'startDate');
    startInput.setAttribute('value', `${eventDropInfo.event.start}`);
    form.appendChild(startInput);
    let endInput = document.createElement('input');
    endInput.setAttribute('type', 'hidden');
    endInput.setAttribute('name', 'endDate');
    endInput.setAttribute('value', `${eventDropInfo.event.end}`);
    form.appendChild(endInput);

    if ( (eventDropInfo.event.end.getTime() - eventDropInfo.oldEvent.end.getTime()) > 0  || (eventDropInfo.event.end.getTime() - eventDropInfo.oldEvent.end.getTime()) < 0 ) {
        let pagDate = document.createElement('input');
        pagDate.setAttribute('type', 'hidden');
        pagDate.setAttribute('name', 'paginationDate');
        pagDate.setAttribute('value', `${eventDropInfo.event.end}`);
        form.appendChild(pagDate);
    } else {
        let pagDate = document.createElement('input');
        pagDate.setAttribute('type', 'hidden');
        pagDate.setAttribute('name', 'paginationDate');
        pagDate.setAttribute('value', `${eventDropInfo.event.start}`);
        form.appendChild(pagDate);
    }

    //Submit form to post data to /planner/editSprint/{sprintId} endpoint.
    document.body.appendChild(form);
    form.submit();
}

function moveChoiceTo(elem_choice, direction) {

    let span = elem_choice.parentNode,
        td = span.parentNode;

    if (direction === -1 && span.previousElementSibling) {
        td.insertBefore(span, span.previousElementSibling);
    } else if (direction === 1 && span.nextElementSibling) {
        td.insertBefore(span, span.nextElementSibling.nextElementSibling)
    }
}

function changeText(text) {
    let con = document.createElement('div');
    let h = document.createElement('h2');
    con.classList.add('update');
    h.classList.add('update-text');
    h.innerText = text;
    h.setAttribute('id', 'text-change')
    con.appendChild(h);
    let cal = document.getElementById('calendar');
    cal.appendChild(con);
    moveChoiceTo(document.getElementById('text-change'), -1)
}

function checkResponse(data){
    var jsondata = JSON.parse(data);
    if (jsondata.refresh) {
        window.location.reload();
    }
}

function editPolling(){
//This promise will resolve when the network call succeeds
    var networkPromise = fetch('/projects-editStatus?id=' + projectId);

//This promise will resolve when 2 seconds have passed
    var timeOutPromise = new Promise(function(resolve, reject) {
        setTimeout(resolve, 2000, 'Timeout Done');
    });

    networkPromise.then(response => response.text())
        .then(data => checkResponse(data));

    Promise.all(
        [networkPromise, timeOutPromise]).then(function(values) {
        editPolling();
    });
}

$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})
//dummy fetch so that if the user reloads the page manually it does not reload for them again automatically
fetch('/projects-editStatus?id=' + projectId);
setTimeout(function () {
    editPolling();
}, 1000);
