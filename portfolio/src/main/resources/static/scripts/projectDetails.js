function checkResponse(data){
    var jsondata = JSON.parse(data);
    if (jsondata.refresh) {
        window.location.reload();
    }
    var editNotification = document.getElementById("editNotification");
    if (jsondata.edits.length === 0) {
        editNotification.className = "";
    } else {
        editNotification.className = "show";
    }
    var editHTML = "<p>";
    for (const edit in jsondata.edits) {
        editHTML += "<br><br>"
        editHTML += jsondata.edits[edit]
    }
    editHTML += "</p>";
    editNotification.innerHTML = editHTML;
}

function editPolling(){
//This promise will resolve when the network call succeeds
var networkPromise = fetch('projects-editStatus?id=' + document.getElementById("projectId").textContent);

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
//dummy fetch so that if the user reloads the page manually it does not reload for them again automatically
fetch('projects-editStatus?id=' + document.getElementById("projectId").textContent);
setTimeout(function () {
        editPolling();
    }, 1000);
