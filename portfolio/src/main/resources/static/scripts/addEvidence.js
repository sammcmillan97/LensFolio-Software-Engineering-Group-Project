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
        if (testSkill.toLowerCase() === skill.toLowerCase().replaceAll("_", " ")) {
            return;
        }
    }
    for (const testSkill of ALL_SKILLS) {
        if (testSkill.toLowerCase() === skill.toLowerCase()) {
            skillList.push(testSkill.replaceAll("_", " "));
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

function isInSkills(skill) {
    for (const testSkill of skillList) {
        if (testSkill.toLowerCase() === skill.toLowerCase()) {
            return true;
        }
    }
    return false;
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
    value = value.replace(/_+/g, '_');
    let skills = value.split(" ");
    let lastSkill = skills.pop();
    let shouldUpdateSkills = false;
    for (let skill of skills) {
        let trimmedSkill = skill.replaceAll("_", " ").trim().replaceAll(" ", "_");
        if (trimmedSkill !== "") {
            shouldUpdateSkills = true;
            addToSkills(trimmedSkill);
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
    let skillInput = parent.firstChild
    for (let tag of tags) {
        let element = createElementFromHTML(`<div class="skill-tag-con">
                                                          <div class="skill-tag">
                                                            <div class="skill-tag-inside">
                                                              <p>${tag}</p>
                                                              <i class="bi bi-x" onclick="clickXButton('${tag}')"></i>
                                                            </div>
                                                          </div>
                                                        </div>`)
        parent.insertBefore(element, skillInput);
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
let focus; // Where the user is at any point in time in the autocomplete list.
function autocomplete(event) {
    let val = event.target.value;
    /*close any already open lists of autocompleted values*/
    destroyAutocomplete();
    if (!val) { return; } // No need to autocomplete if there is nothing in the box
    focus = -1; // when a new character is pressed remove the autocomplete focus
    let autocompleteList = document.createElement("DIV");
    autocompleteList.setAttribute("id", event.target.id + "autocomplete-list");
    autocompleteList.setAttribute("class", "autocomplete-items");
    event.target.parentNode.appendChild(autocompleteList);
    for (let skill of ALL_SKILLS) {
        if (!isInSkills(skill) &&
        skill.substr(0, val.length).toLowerCase() === val.toLowerCase()) {
            let autocompleteItem = document.createElement("DIV");
            autocompleteItem.innerHTML = sanitizeHTML(skill.replaceAll("_", " "));
            autocompleteItem.innerHTML += "<input type='hidden' value='" + sanitizeHTML(skill) + "'>";
            // When the user clicks a link, destroy the autocomplete field.
            autocompleteItem.addEventListener("click", function(clickEvent) {
                event.target.value = "";
                addToSkills(clickEvent.target.getElementsByTagName("input")[0].value);
                updateTagsInDOM(skillList);
                destroyAutocomplete();
            });
            autocompleteList.appendChild(autocompleteItem);
        }
    }
}

// Updates the focus. This is called on every key press in the entry box and looks for up arrow, down arrow and enter.
function updateFocus(event) {
    let autocompleteList = document.getElementById(event.target.id + "autocomplete-list");
    if (autocompleteList) {
        autocompleteList = autocompleteList.getElementsByTagName("div");
    }
    if (event.keyCode === 40) { // DOWN moves the focus down
        focus++;
        addActive(autocompleteList);
    } else if (event.keyCode === 38) { // UP moves the focus up
        focus--;
        addActive(autocompleteList);
    } else if (event.keyCode === 13) { // ENTER adds a tag
        event.preventDefault(); // do not submit the form (the default action inside forms), instead just add a tag
        if (focus > -1) {
            if (autocompleteList) {
                autocompleteList[focus].click();
            }
        }
    }
}

// Makes the item the focus is on active
function addActive(autocompleteList) {
    if (!autocompleteList) {
        return;
    }
    removeActive(autocompleteList);
    if (focus >= autocompleteList.length) {
        focus = 0;
    }
    if (focus < 0) {
        focus = (autocompleteList.length - 1);
    }
    autocompleteList[focus].classList.add("autocomplete-active");
}

// Makes every autocomplete item no longer active
function removeActive(autocompleteList) {
    for (let autocompleteItem of autocompleteList) {
        autocompleteItem.classList.remove("autocomplete-active");
    }
}

// Destroys the autocomplete list
function destroyAutocomplete() {
    let autocompleteList = document.getElementsByClassName("autocomplete-items");
    for (let autocompleteItem of autocompleteList) {
        autocompleteItem.parentNode.removeChild(autocompleteItem);
    }
}

// When a user clicks somewhere, destroy the autocomplete list unless they clicked on the autocomplete list or skill input
document.addEventListener("click", function (event) {
    let autocompleteList = document.getElementsByClassName("autocomplete-items");
    for (let autocompleteItem of autocompleteList) {
        if (event.target !== autocompleteItem && event.target !== document.getElementById("skills-input")) {
            autocompleteItem.parentNode.removeChild(autocompleteItem);
        }
    }
});

// HTML sanitization courtesy of  https://portswigger.net/web-security/cross-site-scripting/preventing
function sanitizeHTML(string) {
	return string.replace(/[^\w. ]/gi, function (char) {
		return '&#' + char.charCodeAt(0) + ';';
	});
}

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