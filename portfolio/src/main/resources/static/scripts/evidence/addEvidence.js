// If the evidence title/description are not valid, set the save button to disabled.
function checkValid() {
    if (evidenceId === -1) {
        document.getElementById('evidence-form__save').disabled =
            document.getElementById("evidence-form__title-field").value.length < 2
            || document.getElementById("evidence-form__title-field").value.length > 64
            || document.getElementById("evidence-form__description-field").value.length < 50
            || document.getElementById("evidence-form__description-field").value.length > 1024;
    } else {
        console.log(arraysMatch(originalEvidenceSkills, skillList));
        document.getElementById('evidence-form__save').disabled =
            document.getElementById("evidence-form__title-field").value.length < 2
            || document.getElementById("evidence-form__title-field").value.length > 64
            || document.getElementById("evidence-form__description-field").value.length < 50
            || document.getElementById("evidence-form__description-field").value.length > 1024
            || (document.getElementById("evidence-form__title-field").value===originalEvidenceTitle
                &&document.getElementById("evidence-form__description-field").value===originalEvidenceDescription
                &&document.getElementById("evidence-form__date-field").value===originalEvidenceDate
                &&document.getElementById("flex-check--quantitative").checked===(originalCategories.includes("Quantitative"))
                &&document.getElementById("flex-check--qualitative").checked===(originalCategories.includes("Qualitative"))
                &&document.getElementById("flex-check--service").checked===(originalCategories.includes("Service"))
                &&arraysMatch(originalEvidenceSkills, skillList)
                &&arraysMatch(originalEvidenceUsers, userList));
    }
}

let skillList = []
let userList = []

// Adds a skill to the list of skills. Makes sure it is not already present,
// and if the user has already entered that skill on another piece of evidence, make sure the capitalization is correct.
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
    checkValid();
}

function removeLastSkill() {
    skillList.pop();
    checkValid();
}

function removeSkill(skill) {
    skillList.splice(skillList.indexOf(skill), 1);
    checkValid();
}

// Adds a user to the list of users. Makes sure it is not already present.
// Users must be in the form of ids.
function addToUsers(user) {
    checkValid();
    for (const testUser of userList) {
        if (testUser === user) {
            return;
        }
    }
    userList.push(user);
}

function removeLastUser() {
    userList.pop();
}

function removeUser(user) {
    userList.splice(userList.indexOf(user), 1);
}

// Remove a skill tag when the 'x' button is clicked
function clickSkillXButton(tag) {
    removeSkill(tag);
    updateSkillTagsInDOM(skillList);

    if (skillList.length === 0) {
        document.getElementById("skills-input").placeholder = 'Add Skills';
    }
}

// Remove a user tag when the 'x' button is clicked
function clickUserXButton(tag) {
    removeUser(tag);
    updateUserTagsInDOM(userList);

    if (userList.length === 0) {
        document.getElementById("users-input").placeholder = 'Add Users';
    }
}

// Listen for input so the tags and autocomplete can be triggered
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
        updateSkillTagsInDOM(skillList);
        event.target.style.width = "80px";
    }
    if (skillList.length > 0) {
        event.target.placeholder = '';
    } else {
        event.target.placeholder = 'Add Skills';
    }
    autocompleteSkills(event); // Call the autocomplete function whenever the input changes
})

// Listen for input so the autocomplete can be triggered for the users field
document.getElementById("users-input").addEventListener("input", (event) => {
    event.target.style.width = event.target.value.length > 8 ? event.target.value.length + "ch" : "80px";
    if (userList.length > 0) {
        event.target.placeholder = '';
    } else {
        event.target.placeholder = 'Add Users';
    }
    autocompleteUsers(event); // Call the autocomplete function whenever the input changes
})

// Listen for key press so keys which do not change the input (backspace, enter, up, down) can be detected
document.getElementById("skills-input").addEventListener("keydown", (event) => {
    let skillText = event.target.value
    if (event.key === "Backspace" && skillText === "") {
        removeLastSkill();
        updateSkillTagsInDOM(skillList);

        if (skillList.length === 0) {
            event.target.placeholder = 'Add Skills';
        }
    }
    updateFocus(event);
})

// Listen for key press so keys which do not change the input (backspace, enter, up, down) can be detected
document.getElementById("users-input").addEventListener("keydown", (event) => {
    let userText = event.target.value
    if (event.key === "Backspace" && userText === "") {
        removeLastUser();
        updateUserTagsInDOM(userList);

        if (userList.length === 0) {
            event.target.placeholder = 'Add Users';
        }
    }
    updateFocus(event);
})

// Updates the tags shown before the skills input list to reflect the list of tags given.
function updateSkillTagsInDOM(tags) {
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
                                                              <p>${sanitizeHTML(tag)}</p>
                                                              <i class="bi bi-x" onclick="clickSkillXButton('${sanitizeHTML(tag)}')"></i>
                                                            </div>
                                                          </div>
                                                        </div>`)
        parent.insertBefore(element, skillInput);
    }
}

// Updates the tags shown before the users input list to reflect the list of tags given.
function updateUserTagsInDOM(tags) {
    let users = "";
    for (const user of tags) {
        users += user;
        users += " ";
    }
    document.getElementById("evidence-form__hidden-users-field").value = users;

    let parent = document.getElementById("user-container");
    while (parent.childNodes.length > 2) {
        parent.removeChild(parent.firstChild);
    }
    let userInput = parent.firstChild
    console.log(userInput);
    for (let tag of tags) {
        for (user of ALL_USERS) {
            if (tag === user.id) {
                let element = createElementFromHTML(`
                            <div class="user-tag-con">
                              <div class="user-tag">
                                <div class="user-tag-inside">
                                  <img class="user-tag-image" src=${user.profilePicture} alt="Profile Photo">
                                  <div class="user-tag-text" >
                                      <p class="user-tag-fullName">${sanitizeHTML(user.fullName)}</p>
                                      <p class="user-tag-nickname"> ${sanitizeHTML(user.username)}</p>
                                  </div>
                                  <i class="bi bi-x" onclick="clickUserXButton('${sanitizeHTML(tag)}')"></i>
                                </div>
                              </div>
                            </div>`)
                parent.insertBefore(element, userInput);
            }
        }
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

// Perform autocompleting on skills. This is a complex endeavour!
// Credit to w3schools for lighting the path on how to do this.
let focus; // Where the user is at any point in time in the autocomplete list.
function autocompleteSkills(event) {
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
        if (skill.substr(0, val.length).toLowerCase() === val.toLowerCase()) {
            let autocompleteItem = document.createElement("DIV");
            autocompleteItem.innerHTML = sanitizeHTML(skill.replaceAll("_", " "));
            autocompleteItem.innerHTML += "<input type='hidden' value='" + sanitizeHTML(skill) + "'>";
            // When the user clicks a link, destroy the autocomplete field.
            autocompleteItem.addEventListener("click", function(clickEvent) {
                event.target.value = "";
                addToSkills(clickEvent.target.getElementsByTagName("input")[0].value);
                updateSkillTagsInDOM(skillList);
                destroyAutocomplete();
            });
            autocompleteList.appendChild(autocompleteItem);
        }
    }
}

// Perform autocompleting on users. This is a complex endeavour!
function autocompleteUsers(event) {
    console.log(ALL_USERS);
    let val = event.target.value;
    /*close any already open lists of autocompleted values*/
    destroyAutocomplete();
    if (!val) { return; } // No need to autocomplete if there is nothing in the box
    focus = -1; // when a new character is pressed remove the autocomplete focus
    let autocompleteList = document.createElement("DIV");
    autocompleteList.setAttribute("id", event.target.id + "autocomplete-list");
    autocompleteList.setAttribute("class", "autocomplete-items");
    event.target.parentNode.appendChild(autocompleteList);
    for (let user of ALL_USERS) {
        if (user.fullName.substr(0, val.length).toLowerCase() === val.toLowerCase()) {
            let autocompleteItem = document.createElement("DIV");
            autocompleteItem.innerHTML = sanitizeHTML(user.fullName);
            autocompleteItem.innerHTML += "<input type='hidden' value='" + sanitizeHTML(user.id) + "'>";
            // When the user clicks a link, destroy the autocomplete field.
            autocompleteItem.addEventListener("click", function(clickEvent) {
                event.target.value = "";
                addToUsers(clickEvent.target.getElementsByTagName("input")[0].value);
                updateUserTagsInDOM(userList);
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
        if (event.target !== autocompleteItem) {
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

let skillsInput = document.getElementById("skills-input");
let skillsDiv = document.getElementById("skill-input-container")

let usersInput = document.getElementById("users-input");
let usersDiv = document.getElementById("user-input-container")

/**
 * allows clicking skills container to select the input and puts outline on div
 */
skillsDiv.addEventListener('click', (event) => {
    skillsInput.focus();
});
skillsInput.addEventListener('focus', (event) => {
    skillsDiv.style.outline = 'black solid 2px';
});
skillsInput.addEventListener('blur', (event) => {
    skillsDiv.style.outline = '';
});


/**
 * allows clicking skills container to select the input and puts outline on div
 */
usersDiv.addEventListener('click', (event) => {
    usersInput.focus();
});
usersInput.addEventListener('focus', (event) => {
    usersDiv.style.outline = 'black solid 2px';
});
usersInput.addEventListener('blur', (event) => {
    usersDiv.style.outline = '';
});



document.getElementById("skills-input").dispatchEvent(new Event('input', {
    bubbles: true,
    cancelable: true,
}))

document.getElementById("users-input").dispatchEvent(new Event('input', {
    bubbles: true,
    cancelable: true,
}))

function arraysMatch(original,newList) {
    //split the original string into a list
    let originalList = original.split(" ");
    //clone the list so that order is not affected
    let cloneNewList = newList;
    //remove additional null off the end of list
    originalList.pop();
    return (arraysEqual(originalList, cloneNewList))
}

function arraysEqual(a, b) {
    if (a === b) return true;
    if (a == null || b == null) return false;
    if (a.length !== b.length) return false;

    a.sort();
    b.sort();

    for (let i = 0; i < a.length; ++i) {
        if (a[i] !== b[i]) return false;
    }
    return true;
}

