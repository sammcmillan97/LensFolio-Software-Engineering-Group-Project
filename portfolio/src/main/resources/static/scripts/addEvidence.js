function checkEmpty() {
    document.getElementById('evidence-form__save').disabled =
        document.getElementById("evidence-form__title-field").value.length < 2
        || document.getElementById("evidence-form__title-field").value.length > 64
        || document.getElementById("evidence-form__description-field").value.length < 50
        || document.getElementById("evidence-form__description-field").value.length > 1024;
}

document.getElementById("evidence-form__skills-field").addEventListener("keydown", (event) =>{
    console.log(event);
})