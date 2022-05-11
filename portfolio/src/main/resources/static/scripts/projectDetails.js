function checkResponse(data){
    console.log(data[0])
    var editNotification = document.getElementById("editNotification");
    if (data[0] == "<") {
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
const update = {
title: 'A blog post by Kingsley',
body: 'Brilliant post on fetch API',
userId: 1,
};

const options = {
method: 'POST',
headers: {
'Content-Type': 'application/json',
},
body: JSON.stringify(update),
};
fetch('http://localhost:9000/projects/1/editing', options);
callme();