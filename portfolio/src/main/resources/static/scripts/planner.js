// Define variables that need to be used across functions
let calendarEl;
let calendar;

let calendarSprints = []
let calendarEvents = []

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
        //disallow dragging entire event
        eventStartEditable: false,
        //allow resizing of start/end date
        eventResizableFromStart: true,

        eventOverlap: function( stillEvent, movingEvent) {
            return !(stillEvent.extendedProps.eventType === 'Sprint' && movingEvent.extendedProps.eventType === 'Sprint') && stillEvent.extendedProps.name !== 'Project';
        },

        //Listens to sprint drag/drop
        eventResize: function (eventDropInfo) {
            resizeSprint( eventDropInfo );
        },

        // Hovers over sprint changing colour.
        eventMouseEnter: function( info ) {
            if ((info.event.extendedProps.eventType !== 'Sprint') || !isAdmin()) {
                return;
            }
            $(".fc-event").css("cursor", "pointer");
            if (!info.event.extendedProps.selected) {
                info.event.setExtendedProp("oldBackground", info.event.backgroundColor);
                info.event.setProp("backgroundColor", colorLuminance(info.event.backgroundColor, -0.1));
            }
        },

        // Changes sprint color back when mouse leaves.
        eventMouseLeave: function( info ) {
            if ((info.event.extendedProps.eventType !== 'Sprint') || !isAdmin()) {
            return;
            }
            $(".fc-event").css("cursor", "default");
            if (!info.event.extendedProps.selected) {
                info.event.setProp("backgroundColor", info.event.extendedProps.oldBackground);
            }
        },

        // Selects sprint to allow resizing.
        eventClick: function (info) {
            if ((info.event.extendedProps.eventType !== 'Sprint') || !isAdmin()) {
                return;
            }
            if (!info.event.extendedProps.selected) {
                info.event.setExtendedProp("selected", true);
                info.event.setProp("durationEditable", true);
                info.event.setProp("backgroundColor", colorLuminance(info.event.backgroundColor, -0.1));
            } else {
                info.event.setExtendedProp("selected", false);
                info.event.setProp("durationEditable", false);
                info.event.setProp("backgroundColor", info.event.extendedProps.oldBackground);
            }
        },

        // Shapes event html after added to DOM.
        eventDidMount: function (info) {
            info.event.setProp("textColor", "black");
            if (info.event.extendedProps.eventType === 'Sprint' || !info.event.extendedProps.eventType) {
                return;
            }
            shapeIcons( info );
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

/**
 * This function adds icons to calendar and changes the css of the event the icon is for.
 * @param info
 */
function shapeIcons( info ) {
    let iconParent = info.el.querySelector(".fc-event-title").parentElement;
    let iconEventContainer = iconParent.parentElement.parentElement.parentElement;
    let iconGridCell = iconParent.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement
    // Aligns icon with event title, counter number next to icon.
    iconParent.style.display = 'flex';
    iconParent.style.justifyContent = 'flex-start';
    iconParent.style.alignItems = 'center';
    // These styles won't work with the __PlannerGrid classes added later.
    iconEventContainer.style.border = '0';
    iconEventContainer.style.backgroundColor = 'transparent';
    iconEventContainer.style.position = 'absolute';
    // Sets the cell to min height so milestones, deadline and events icons always stay on cell.
    iconGridCell.style.minHeight = '135px';
    // Format milestones, deadline and events calendar events to add icons.
    if (info.event.extendedProps.eventType === "daily-milestone") {
        iconEventContainer.classList.add('milestonePlannerGrid');
        let node = createElementFromHTML(`<i data-toggle="tooltip"
                                            data-placement="top" data-html=true
                                            class="bi bi-trophy-fill"></i>`);
        node.title = info.event.extendedProps.description
        iconParent.insertBefore(node, iconParent.firstChild);
    } else if (info.event.extendedProps.eventType === "daily-deadline") {
        iconEventContainer.classList.add('deadlinePlannerGrid');
        let node = createElementFromHTML(`<i data-toggle="tooltip" 
                                            data-placement="top" data-html="true"
                                            class="bi bi-alarm-fill"></i>`);
        node.title = info.event.extendedProps.description
        iconParent.insertBefore(node, iconParent.firstChild);
    } else if (info.event.extendedProps.eventType === "daily-event") {
        iconEventContainer.classList.add('eventPlannerGrid');
        let node = createElementFromHTML(`<i data-toggle="tooltip" 
                                            data-placement="top" data-html="true" 
                                            class="bi bi-calendar-event-fill"></i>`);
        node.title = info.event.extendedProps.description
        iconParent.insertBefore(node, iconParent.firstChild);
    }
}

/**
 * Creates node element from html string.
 * @param htmlString
 * @returns {ChildNode}
 */
function createElementFromHTML(htmlString) {
    let template = document.createElement('template');
    template.innerHTML = htmlString.trim();
    return template.content.firstChild;
}

/**
 * Pulled from Craig Buckler's post. https://www.sitepoint.com/javascript-generate-lighter-darker-color/
 * Adapted slightly to account for deprecated javascript.
 * @param hex
 * @param lum
 * @returns {string}
 */
function colorLuminance(hex, lum) {
    // validate hex string
    hex = String(hex).replace(/[^0-9a-f]/gi, '');
    if (hex.length < 6) {
        hex = hex[0]+hex[0]+hex[1]+hex[1]+hex[2]+hex[2];
    }
    lum = lum || 0;
    // convert to decimal and change luminosity
    let rgb = "#", c, i;
    for (i = 0; i < 3; i++) {
        c = parseInt(hex.substring(i*2,i*2+2), 16);
        c = Math.round(Math.min(Math.max(0, c + (c * lum)), 255)).toString(16);
        rgb += ("00"+c).substring(c.length);
    }
    return rgb;
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
    }

    // Calculate and return the end date
    return endDate.slice(0,5) + month + "-" + "01";
}

/**
 * Adds all the sprints in the list created by thymeleaf to the calendar
 */
function addSprintsToCalendar() {
    for (let sprint of sprints) {
        calendarSprints.push(calendar.addEvent(sprint));
    }
}

/**
 * Adds Events/Deadlines/Milestones to the FC for adding icons
 */
function addEventsToCalendar() {
    for (let event of events) {
        calendarEvents.push(calendar.addEvent(event));
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

/**
 * Fetches updated sprints, events, deadlines and milestones, then updates the calendar with these new events
 */
async function updatePage() {

    // Clear the lists of sprints and events
    sprints = []
    events = []

    // Fetch updated sprints, events, deadlines and milestones
    await updateSprints()
    await updateEvents()
    await updateDeadlines()
    await updateMilestones()

    // Remove all old sprints and events from the calendar
    for (let sprint of calendarSprints) {
        sprint.remove()
    }
    for (let event of calendarEvents) {
        event.remove()
    }

    // Add the new sprints and events to the calendar then render them
    addSprintsToCalendar()
    addEventsToCalendar()
    calendar.render()
}

/**
 * Fetches an updated list of sprints, then parses them into a format readable by FullCalendar, then
 * adds them to the list of sprints
 */
async function updateSprints() {
    const sprintsList = await fetch("/getSprintsList?projectId=" + projectId).then((response) => {
        return response.json()
    })

    let sprint;
    let tempSprint;
    for (let i = 0; i < sprintsList.length; i++) {
        sprint = sprintsList[i]
        tempSprint = {
            id: sprint.id,
            title: sprint.label,
            eventType: 'Sprint',
            start: sprint.startDateCalendarString,
            end: sprint.dayAfterEndDateCalendarString,
            // Set colour of the sprint.
            color: sprintEventColours[i % sprintEventColours.length]
        }
        sprints.push(tempSprint);
    }
}

/**
 * Fetches an updated list of events, then parses them into a format readable by FullCalendar, then
 * adds them to the list of all events
 */
async function updateEvents() {
    const eventsList = await fetch("/getEventsList?projectId=" + projectId).then((response) => {
        return response.json()
    })

    let event;
    let tempEvent;
    for (let key in eventsList) {
        event = eventsList[key]
        tempEvent = {
            id: event.id,
            title: event.numberOfEvents,
            eventType: event.type,
            start: event.date,
            description: event.description,
        }
        events.push(tempEvent);
    }
}

/**
 * Fetches an updated list of deadlines, then parses them into a format readable by FullCalendar, then
 * adds them to the list of all events
 */
async function updateDeadlines() {
    const deadlinesList = await fetch("/getDeadlinesList?projectId=" + projectId).then((response) => {
        return response.json()
    })

    let deadline;
    let tempDeadline;
    for (let key in deadlinesList) {
        deadline = deadlinesList[key]
        tempDeadline = {
            id: deadline.id,
            title: deadline.numberOfEvents,
            eventType: deadline.type,
            start: deadline.date,
            description: deadline.description,
        }
        events.push(tempDeadline);
    }
}

/**
 * Fetches an updated list of milestones, then parses them into a format readable by FullCalendar, then
 * adds them to the list of all events
 */
async function updateMilestones() {
    const milestonesList = await fetch("/getMilestonesList?projectId=" + projectId).then((response) => {
        return response.json()
    })

    let milestone;
    let tempMilestone;
    for (let key in milestonesList) {
        milestone = milestonesList[key]
        tempMilestone = {
            id: milestone.id,
            title: milestone.numberOfEvents,
            eventType: milestone.type,
            start: milestone.date,
            description: milestone.description,
        }
        events.push(tempMilestone);
    }
}

/**
 * Used for live updating the planner, refreshes the page when a sprint, event, deadline or milestone has been added/edited
 */
function checkResponse(data){
    let jsondata = JSON.parse(data);
    if (jsondata.refresh) {
        // update the
        updatePage()
    }
}

/**
 * Used in live updating for checking if the current project is being edited
 */
function editPolling(){
//This promise will resolve when the network call succeeds
    let networkPromise = fetch('projects-editStatus?id=' + projectId);

//This promise will resolve when 2 seconds have passed
    let timeOutPromise = new Promise(function(resolve, reject) {
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
fetch('projects-editStatus?id=' + projectId);
setTimeout(function () {
    editPolling();
}, 1000);
