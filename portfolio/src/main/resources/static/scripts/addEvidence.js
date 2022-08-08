function checkEmpty() {
    document.getElementById('evidence-form__save').disabled =
        document.getElementById("evidence-form__title-field").value.length < 2
        || document.getElementById("evidence-form__title-field").value.length > 64
        || document.getElementById("evidence-form__description-field").value.length < 50
        || document.getElementById("evidence-form__description-field").value.length > 1024;
}

let skillList = []

function addToSkills(skill) {
    for (const testSkill of skillList) {
        if (testSkill.toLowerCase() === skill.toLowerCase()) {
            return;
        }
    }
    for (const testSkill of ALL_SKILLS) {
        if (testSkill.toLowerCase() === skill.toLowerCase()) {
            skillList.push(testSkill);
            return;
        }
    }
    skillList.push(skill.replaceAll("_", " "));
}

function removeLastSkill() {
    skillList.pop();
}

function removeSkill(skill) {
    skillList.splice(skillList.indexOf(skill), 1);
}

function clickXButton(tag) {
    removeSkill(tag);
    updateTagsInDOM(skillList);

    if (skillList.length === 0) {
        document.getElementById("skills-input").placeholder = 'Add Skills';
    }
}

document.getElementById("skills-input").addEventListener("input", (event) => {
    event.target.style.width = event.target.value.length > 8 ? event.target.value.length + "ch" : "80px";
    let value = event.target.value;
    let skills = value.split(" ");
    let lastSkill = skills.pop();
    let shouldUpdateSkills = false;
    for (const skill of skills) {
        if (skill !== "") {
            shouldUpdateSkills = true;
            addToSkills(skill);
            console.log("add " + skill + " to DOM, nice job!");
        }
    }
    lastSkill = lastSkill.slice(0, 50);
    document.getElementById("skills-input").value = lastSkill;
    if (shouldUpdateSkills) {
        updateTagsInDOM(skillList);
        event.target.style.width = "80px";
    }
    if (skillList.length > 0) {
        event.target.placeholder = '';
    } else {
        event.target.placeholder = 'Add Skills';
    }
    autocomplete(event); // Call the autocomplete function whenever the input changes
})


document.getElementById("skills-input").addEventListener("keydown", (event) => {
    let skillText = event.target.value
    if (event.key === "Backspace" && skillText === "") {
        removeLastSkill();
        updateTagsInDOM(skillList);

        if (skillList.length === 0) {
            event.target.placeholder = 'Add Skills';
        }
    }
    updateFocus(event);
})

function updateTagsInDOM(tags) {
    let skills = "";
    for (const skill of tags) {
        skills += skill.replaceAll(" ", "_");
        skills += " ";
    }
    document.getElementById("evidence-form__hidden-skills-field").value = skills;

    let parent = document.getElementById("skill-container");
    while (parent.childNodes.length > 2) {
        parent.removeChild(parent.firstChild);
    }
    let input = parent.firstChild
    for (let i = 0; i < tags.length; i++) {
        let element = createElementFromHTML(`<div class="skill-tag-con">
                                                          <div class="skill-tag">
                                                            <div class="skill-tag-inside">
                                                              <p>${tags[i]}</p>
                                                              <i class="bi bi-x" onclick="clickXButton('${tags[i]}')"></i>
                                                            </div>
                                                          </div>
                                                        </div>`)
        parent.insertBefore(element, input);
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

// Perform autocompleting. This is a complex endeavour!
// Credit to w3schools for lighting the path on how to do this.
var focus; // Where the user is at any point in time in the autocomplete list.
function autocomplete(event) {
    let val = event.target.value;
    /*close any already open lists of autocompleted values*/
    destroyAutocomplete();
    if (!val) { return; } // No need to autocomplete if there is nothing in the box
    focus = -1;
    /*create a DIV element that will contain the items (values):*/
    let a = document.createElement("DIV");
    a.setAttribute("id", event.target.id + "autocomplete-list");
    a.setAttribute("class", "autocomplete-items");
    /*append the DIV element as a child of the autocomplete container:*/
    event.target.parentNode.appendChild(a);
    for (i = 0; i < ALL_SKILLS.length; i++) {
        /*check if the item starts with the same letters as the text field value:*/
        if (ALL_SKILLS[i].substr(0, val.length).toUpperCase() == val.toUpperCase()) {
            /*create a DIV element for each matching element:*/
            let b = document.createElement("DIV");
            /*make the matching letters bold:*/
            b.innerHTML = "<strong>" + ALL_SKILLS[i].substr(0, val.length) + "</strong>";
            b.innerHTML += ALL_SKILLS[i].substr(val.length);
            /*insert a input field that will hold the current array item's value:*/
            b.innerHTML += "<input type='hidden' value='" + ALL_SKILLS[i] + "'>";
            // When the user clicks a link, destroy the autocomplete field.
            b.addEventListener("click", function(clickEvent) { // TODO max 10?
                event.target.value = clickEvent.target.getElementsByTagName("input")[0].value;
                destroyAutocomplete();
            });
            a.appendChild(b);
        }
    }
}

/*execute a function presses a key on the keyboard:*/
function updateFocus(event) {
    var x = document.getElementById(this.id + "autocomplete-list");
    if (x) x = x.getElementsByTagName("div");
    if (event.keyCode == 40) { // DOWN moves the focus down
        focus++;
        addActive(x);
    } else if (event.keyCode == 38) { // UP moves the focus up
        focus--;
        addActive(x);
    } else if (event.keyCode == 13) { // ENTER submits
        event.preventDefault(); // do not submit the form, instead just add a tag
        if (focus > -1) {
            if (x) {
                x[focus].click();
            }
        }
    }
}

function addActive(x) {
    /*a function to classify an item as "active":*/
    if (!x) {
        return;
    }
    /*start by removing the "active" class on all items:*/
    removeActive(x);

    // this code wraps the user's focus if they go off either end of the list
    if (focus >= x.length) {
        focus = 0;
    }
    if (currentFocus < 0) {
        focus = (x.length - 1);
    }
    x[currentFocus].classList.add("autocomplete-active");
}

function removeActive(x) {
    /*a function to remove the "active" class from all autocomplete items:*/
    for (var i = 0; i < x.length; i++) {
        x[i].classList.remove("autocomplete-active");
    }
}
function destroyAutocomplete() {
    var x = document.getElementsByClassName("autocomplete-items");
    for (var i = 0; i < x.length; i++) {
        x[i].parentNode.removeChild(x[i]);
    }
}

// when a user clicks somewhere, destroy the autocomplete unless they clicked on the autocomplete or skill input
document.addEventListener("click", function (event) {
    var x = document.getElementsByClassName("autocomplete-items");
    for (var i = 0; i < x.length; i++) {
        if (event.target != x[i] && event.target != document.getElementById("skills-input")) {
            x[i].parentNode.removeChild(x[i]);
        }
    }
});



let input = document.getElementById("skills-input");
let div = document.getElementById("skill-input-container")

/**
 * allows clicking skills container to select the input and puts outline on div
 */
div.addEventListener('click', (event) => {
    input.focus();
});
input.addEventListener('focus', (event) => {
    div.style.outline = 'black solid 2px';
});
input.addEventListener('blur', (event) => {
    div.style.outline = '';
});