function checkResponse(data){
    console.log(data[0])
    var editNotification = document.getElementById("editNotification");
    if (data[0] == "1") {
        editNotification.style.display = "block";
    } else {
        editNotification.style.display = "none";
    }
}

function callme(){
//This promise will resolve when the network call succeeds
//Feel free to make a REST fetch using promises and assign it to networkPromise
var networkPromise = fetch('http://localhost:9000/projects/1/editStatus');

//This promise will resolve when 2 seconds have passed
var timeOutPromise = new Promise(function(resolve, reject) {
  // 2 Second delay
  setTimeout(resolve, 2000, 'Timeout Done');
});

networkPromise.then(response => response.text())
      .then(data => checkResponse(data));

Promise.all(
[networkPromise, timeOutPromise]).then(function(values) {
  console.log("Atleast 2 secs + TTL (Network/server)");
  callme();
});
}
callme();