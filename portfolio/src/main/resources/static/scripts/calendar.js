
document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        visibleRange: {
            start: startDate,
            end: endDate
        },
        initialView: 'dayGridMonth'
    });
    calendar.render();
});

function initaliseCalendar(startDate, endDate) {
    calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        visibleRange: {
            start: startDate,
            end: endDate

        },
        initialView: 'dayGridMonth'
    });
    calendar.render();
}

function limitDateRange (startDate, endDate) {
    calendar.fullCalendar('validRange', {
        start: startDate,
            end: endDate
})

}