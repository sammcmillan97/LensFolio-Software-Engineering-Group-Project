function editSending(){
//This promise will resolve when the network call succeeds
const options = {
method: 'POST'
};

var networkPromise = fetch('projects-editing?id=' + document.getElementById("projectId").textContent + '&name=' + document.getElementById("nameOfEdited").textContent, options);

//This promise will resolve when 2 seconds have passed
var timeOutPromise = new Promise(function(resolve, reject) {
  setTimeout(resolve, 2000, 'Timeout Done');
});

Promise.all(
[networkPromise, timeOutPromise]).then(function(values) {
  editSending();
});
}
editSending();