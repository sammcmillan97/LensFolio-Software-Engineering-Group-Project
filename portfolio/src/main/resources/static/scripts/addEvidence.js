function checkEmpty() {
    document.getElementById('evidence-form__save').disabled =
        document.getElementById("evidence-form__title-field").value.length < 2
        || document.getElementById("evidence-form__title-field").value.length > 64
        || document.getElementById("evidence-form__description-field").value.length < 50
        || document.getElementById("evidence-form__description-field").value.length > 1024;
}

document.getElementById("evidence-form__skills-field").addEventListener("input", (event) => {
    let value = event.target.value;
    let skills = value.split(" ");
    let lastSkill = skills.pop();
    for (const skill of skills) {
        if (skill !== "") {
            console.log("add " + skill + " to DOM, nice job!")
        }
    }
    document.getElementById("evidence-form__skills-field").value = lastSkill;
})

document.getElementById("evidence-form__skills-field").addEventListener("keydown", (event) => {
    let skillText = event.target.value
    if (event.key === "Backspace" && skillText === "") {
        console.log("destroy tag if one exists")
    }
})