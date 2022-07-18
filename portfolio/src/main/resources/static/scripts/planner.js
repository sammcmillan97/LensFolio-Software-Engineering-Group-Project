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
                let parentElement = info.el.querySelector(".fc-event-title").parentElement;
                parentElement.style.display = 'flex';
                parentElement.style.justifyContent = 'flex-start';
                parentElement.style.alignItems = 'center';
                // if (info.event.extendedProps.eventType === "daily-milestone") {
                //     parentElement.insertBefore(createElementFromHTML(`
                //                     <svg data-toggle="tooltip" data-bs-placement="top" data-html="true" title=${"'" + info.event.extendedProps.description + "'"}
                //                    xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trophy-fill calendarOccasion" viewBox="0 0 16 16">
                //                    <path d="M2.5.5A.5.5 0 0 1 3 0h10a.5.5 0 0 1 .5.5c0 .538-.012 1.05-.034 1.536a3 3 0 1 1-1.133 5.89c-.79 1.865-1.878 2.777-2.833 3.011v2.173l1.425.356c.194.048.377.135.537.255L13.3 15.1a.5.5 0 0 1-.3.9H3a.5.5 0 0 1-.3-.9l1.838-1.379c.16-.12.343-.207.537-.255L6.5 13.11v-2.173c-.955-.234-2.043-1.146-2.833-3.012a3 3 0 1 1-1.132-5.89A33.076 33.076 0 0 1 2.5.5zm.099 2.54a2 2 0 0 0 .72 3.935c-.333-1.05-.588-2.346-.72-3.935zm10.083 3.935a2 2 0 0 0 .72-3.935c-.133 1.59-.388 2.885-.72 3.935z"/>
                //                    </svg>`), parentElement.firstChild);
                // } else if (info.event.extendedProps.eventType === "daily-deadline") {
                //     parentElement.insertBefore(createElementFromHTML(`
                //                             <svg data-toggle="tooltip" data-bs-placement="top" title=${"'" + info.event.extendedProps.description + "'"}
                //                     xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hourglass-split calendarOccasion" viewBox="0 0 16 16">
                //                     <path d="M2.5 15a.5.5 0 1 1 0-1h1v-1a4.5 4.5 0 0 1 2.557-4.06c.29-.139.443-.377.443-.59v-.7c0-.213-.154-.451-.443-.59A4.5 4.5 0 0 1 3.5 3V2h-1a.5.5 0 0 1 0-1h11a.5.5 0 0 1 0 1h-1v1a4.5 4.5 0 0 1-2.557 4.06c-.29.139-.443.377-.443.59v.7c0 .213.154.451.443.59A4.5 4.5 0 0 1 12.5 13v1h1a.5.5 0 0 1 0 1h-11zm2-13v1c0 .537.12 1.045.337 1.5h6.326c.216-.455.337-.963.337-1.5V2h-7zm3 6.35c0 .701-.478 1.236-1.011 1.492A3.5 3.5 0 0 0 4.5 13s.866-1.299 3-1.48V8.35zm1 0v3.17c2.134.181 3 1.48 3 1.48a3.5 3.5 0 0 0-1.989-3.158C8.978 9.586 8.5 9.052 8.5 8.351z"/>
                //                     </svg>`), parentElement.firstChild);
                // } else if (info.event.extendedProps.eventType === "daily-event") {
                //     parentElement.insertBefore(createElementFromHTML(`
                //                         <svg data-toggle="tooltip" data-bs-placement="top" title=${"'" + info.event.extendedProps.description + "'"}
                //                     xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-calendar3-event-fill calendarOccasion" viewBox="0 0 16 16">
                //                     <path fill-rule="evenodd" d="M2 0a2 2 0 0 0-2 2h16a2 2 0 0 0-2-2H2zM0 14V3h16v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2zm12-8a1 1 0 1 0 2 0 1 1 0 0 0-2 0z"/>
                //                     </svg>`), parentElement.firstChild);
                // }

                if (info.event.extendedProps.eventType === "daily-milestone") {
                    //parentElement.classList.add('milestonePlanner');
                    parentElement.insertBefore(createElementFromHTML(`<i data-toggle="tooltip"
                                                data-placement="top" data-html="true"
                                                title=${"'" + info.event.extendedProps.description + "'"} 
                                                class="bi bi-trophy-fill"></i>`), parentElement.firstChild);
                } else if (info.event.extendedProps.eventType === "daily-deadline") {
                    //parentElement.classList.add('deadlinePlanner');
                    parentElement.insertBefore(createElementFromHTML(`<i data-toggle="tooltip" 
                                                data-placement="top" data-html="true"
                                                title=${"'" + info.event.extendedProps.description + "'"} 
                                                class="bi bi-alarm-fill"></i>`), parentElement.firstChild);
                } else if (info.event.extendedProps.eventType === "daily-event") {
                    //parentElement.classList.add('eventPlanner');
                    parentElement.insertBefore(createElementFromHTML(`<i data-toggle="tooltip" 
                                                data-placement="top" data-html="true"
                                                title=${"'" + info.event.extendedProps.description + "'"} 
                                                class="bi bi-calendar-event-fill"></i>`), parentElement.firstChild);
                }
            }
        },

        //Changes the html of events, deadlines and milestones so they can be represented as icons on the planner
        // eventDidMount : function(info) {
        //     if (info.event.extendedProps.eventType === "daily-milestone") {
        //         info.el.innerHTML = `<col>
        //                             <div data-toggle="tooltip" data-placement="top" data-html="true" title=${"'" + info.event.extendedProps.description + "'"}>
        //                             <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-trophy-fill" viewBox="0 0 16 16">
        //                              <path d="M2.5.5A.5.5 0 0 1 3 0h10a.5.5 0 0 1 .5.5c0 .538-.012 1.05-.034 1.536a3 3 0 1 1-1.133 5.89c-.79 1.865-1.878 2.777-2.833 3.011v2.173l1.425.356c.194.048.377.135.537.255L13.3 15.1a.5.5 0 0 1-.3.9H3a.5.5 0 0 1-.3-.9l1.838-1.379c.16-.12.343-.207.537-.255L6.5 13.11v-2.173c-.955-.234-2.043-1.146-2.833-3.012a3 3 0 1 1-1.132-5.89A33.076 33.076 0 0 1 2.5.5zm.099 2.54a2 2 0 0 0 .72 3.935c-.333-1.05-.588-2.346-.72-3.935zm10.083 3.935a2 2 0 0 0 .72-3.935c-.133 1.59-.388 2.885-.72 3.935z"/>
        //                              </svg>
        //                              ${info.event.title}
        //                              </div></col>`
        //     }
        //      else if (info.event.extendedProps.eventType === "daily-deadline") {
        //         info.el.innerHTML = `<col>
        //                                     <div data-toggle="tooltip" data-placement="top" data-html="true"  title=${"'" + info.event.extendedProps.description + "'"}>
        //                                     <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-alarm-fill" viewBox="0 0 16 16">
        //                                     <path d="M6 .5a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 0 1H9v1.07a7.001 7.001 0 0 1 3.274 12.474l.601.602a.5.5 0 0 1-.707.708l-.746-.746A6.97 6.97 0 0 1 8 16a6.97 6.97 0 0 1-3.422-.892l-.746.746a.5.5 0 0 1-.707-.708l.602-.602A7.001 7.001 0 0 1 7 2.07V1h-.5A.5.5 0 0 1 6 .5zm2.5 5a.5.5 0 0 0-1 0v3.362l-1.429 2.38a.5.5 0 1 0 .858.515l1.5-2.5A.5.5 0 0 0 8.5 9V5.5zM.86 5.387A2.5 2.5 0 1 1 4.387 1.86 8.035 8.035 0 0 0 .86 5.387zM11.613 1.86a2.5 2.5 0 1 1 3.527 3.527 8.035 8.035 0 0 0-3.527-3.527z"/>
        //                                     </svg>
        //                                     ${info.event.title}
        //                                     </div></col>`
        //     }
        //     else if(info.event.extendedProps.eventType === "daily-event") {
        //         info.el.innerHTML = `<col>
        //                                 <div data-toggle="tooltip" data-placement="top" data-html="true" title=${"'" + info.event.extendedProps.description + "'"}>
        //                                 <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-calendar-event-fill" viewBox="0 0 16 16">
        //                                 <path d="M4 .5a.5.5 0 0 0-1 0V1H2a2 2 0 0 0-2 2v1h16V3a2 2 0 0 0-2-2h-1V.5a.5.5 0 0 0-1 0V1H4V.5zM16 14V5H0v9a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2zm-3.5-7h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1a.5.5 0 0 1 .5-.5z"/>
        //                                 </svg>
        //                                 ${info.event.title}
        //                                 </div></col>`
        //     }
        // },
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

$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})