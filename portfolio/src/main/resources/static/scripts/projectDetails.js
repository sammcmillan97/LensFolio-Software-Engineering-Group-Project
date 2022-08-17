function checkResponse(data){
    let jsondata = JSON.parse(data);
    if (jsondata.refresh) {
        updatePage();
    }
    let editNotification = document.getElementById("edit-notification");
    if (jsondata.edits.length === 0) {
        editNotification.className = "";
    } else {
        editNotification.className = "show";
    }
    let editHTML = "<p>";
    for (const edit in jsondata.edits) {
        editHTML += "<br><br>"
        editHTML += jsondata.edits[edit]
    }
    editHTML += "</p>";
    editNotification.innerHTML = editHTML;
}

function editPolling(){
    //This promise will resolve when the network call succeeds
    let networkPromise = fetch('projects-editStatus?id=' + document.getElementById("projectId").textContent);

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
//dummy fetch so that if the user reloads the page manually it does not reload for them again automatically
fetch('projects-editStatus?id=' + document.getElementById("projectId").textContent);

setTimeout(function () {
        editPolling();
    }, 1000);


/**
 * Takes note of the currently active tab/carousel on the page, fetches updated data from the controller
 * then updates the page and keeps the user on the same tab/carousel
 */
async function updatePage() {
    const currentProjectId = document.getElementById("projectId").textContent;

    // Figure out the current active tab/carousel
    let currentCarouselIndex = 0;
    const carouselItems = document.getElementsByClassName("carousel-item")
    for (let i = 0; i < carouselItems.length; i++) {
        if (carouselItems[i].className.includes("active")) {
            currentCarouselIndex = i;
            break
        }
    }

    // Fetch the new page data from the controller
    const newPage = document.createElement('html')
    newPage.innerHTML = await fetch("projectDetails-" + currentProjectId).then((response) => {
        return response.text()
    })

    // Unmark the default active tab/carousel (if it's not the required one)
    // Mark the required tab/carousel as active (if it's not already)
    const newCarouselItems = newPage.getElementsByClassName("carousel-item")
    for (let i = 0; i < newCarouselItems.length; i++) {
        if (newCarouselItems[i].className.includes("active") && i !== currentCarouselIndex) {
            newCarouselItems[i].className = "carousel-item"
        } else if (!newCarouselItems[i].className.includes("active") && i === currentCarouselIndex) {
            newCarouselItems[i].classList.add("active")
        }
    }

    // Update the page with the new data
    document.body.innerHTML = newPage.innerHTML
}