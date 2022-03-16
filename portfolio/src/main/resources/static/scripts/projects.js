document.addEventListener('click', (e) => {
    let element = e.target;

    if (element.classList.contains("project__name")) {
        let project = element.parentNode;
        if (project.classList.contains("collapsed")) {
            project.classList.remove("collapsed");
            project.classList.add("expanded");
        } else {
            project.classList.remove("expanded");
            project.classList.add("collapsed");
        }
    }
});

function getDateString(date) {
    let dd = date.getDate();
    let mm = date.getMonth() + 1; //January is 0!
    let yyyy = date.getFullYear();

    if (dd < 10) {
        dd = '0' + dd;
    }

    if (mm < 10) {
        mm = '0' + mm;
    }

    return yyyy + '-' + mm + '-' + dd;
}

function updateMinEndDate() {
    let endDate = document.getElementById("add-edit__start-date-field").valueAsNumber;
    endDate = new Date(endDate);
    endDate.setUTCDate(endDate.getUTCDate() + 1);

    document.getElementById("add-edit__end-date-field").setAttribute('min', getDateString(endDate));
}

function openAddEditForm(id, name, description, startDateString, endDateString) {
    // Display the form and grey out other page content using overlay
    document.getElementById("add-edit__popup").classList.remove("hidden");
    document.getElementById("add-edit__overlay").classList.remove("hidden");

    // Set id of project being edited
    document.getElementById("add-edit__project-id").value = id;

    // Populate form inputs with provided values
    document.getElementById("add-edit__name-field").value = name;
    document.getElementById("add-edit__description-field").value = description;

    // Dates
    let startDate = new Date(Date.parse(startDateString));
    startDate.setUTCHours(startDate.getUTCHours() + 13);
    document.getElementById("add-edit__start-date-field").setAttribute('min', getDateString(startDate));
    document.getElementById("add-edit__start-date-field").valueAsNumber = startDate;

    let endDate = new Date(Date.parse(endDateString));
    endDate.setUTCHours(endDate.getUTCHours() + 13);
    document.getElementById("add-edit__end-date-field").setAttribute('min', getDateString(endDate));
    document.getElementById("add-edit__end-date-field").valueAsNumber = endDate;
}

function closeAddEditForm() {
    // Hide the form and overlay by adding class 'hidden' to container
    document.getElementById("add-edit__popup").classList.add("hidden");

    return false;
}

function addProject() {
    let startDate = new Date();
    startDate.setUTCHours(startDate.getUTCHours() + 13);
    let endDate = new Date(startDate.getTime());
    endDate.setUTCMonth(endDate.getUTCMonth() + 8);

    openAddEditForm(null, "Project " + startDate.getFullYear(), "", startDate.toDateString(), endDate.toDateString());
}