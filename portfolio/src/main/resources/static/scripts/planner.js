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
        if (rolesList[i] === "TEACHER" || rolesList[i] === "ADMIN") {
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


    calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
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
    });

    addSprintsToCalendar();
    calendar.render();
    if (paginationDate) {
        calendar.gotoDate(paginationDate);
        changeText('Changes Saved');
    } else {
        changeText('No Changes Made')
    }
});

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

function resizeSprint( eventDropInfo ) {

    //Create form to post data from calendar
    let form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', `/planner/editSprint/${projectId}/${eventDropInfo.oldEvent.id}`);

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
    h.setAttribute('id', 'yeet')
    con.appendChild(h);
    let cal = document.getElementById('calendar');
    cal.appendChild(con);
    moveChoiceTo(document.getElementById('yeet'), -1)
}