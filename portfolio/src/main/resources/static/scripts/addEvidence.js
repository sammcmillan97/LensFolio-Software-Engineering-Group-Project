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

document.getElementById("skills-input").addEventListener("input", (event) => {
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
    }
})

document.getElementById("skills-input").addEventListener("keydown", (event) => {
    let skillText = event.target.value
    if (event.key === "Backspace" && skillText === "") {
        removeLastSkill();
        updateTagsInDOM(skillList);
    }
})

function updateTagsInDOM(tags) {
    let skills = "";
    for (const skill of tags) {
        skills += skill.replaceAll(" ", "_");
        skills += " ";
    }
    document.getElementById("evidence-form__hidden-skills-field").value = skills;

    let parent = document.getElementById("skill-container");
    parent.textContent = '';
    // let childList = parent.childNodes;
    // while ()
    // parent.childNodes = childList.slice(-2);
    // console.log(childList)
    // console.log(childList.length)
    // let listLength =
    // for (let i = 0; i < childList.length; i++) {
    //     if (i < childList.length -2) {
    //         console.log(childList[i]);
    //         parent.removeChild(childList[i]);
    //     }
    // }
    for (let i = 0; i < tags.length; i++) {
        let element = createElementFromHTML(`<div class="skill-tag"><p>${tags[i]}</p></div>`)
        parent.appendChild(element);
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