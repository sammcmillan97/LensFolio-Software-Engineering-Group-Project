function hideElement(element) {
    element.classList.add("hidden");
}

/**
 * Hide Nickname field if empty
 */
window.addEventListener('load', (event) => {
    let nick = document.getElementById("profile__user-nickname");
    if (nick.textContent == "") {
        let ancestor = nick.closest("div.profile_body__content");
        hideElement(ancestor);
    }
});

/**
 * Hide Pronouns field if empty
 */
window.addEventListener('load', (event) => {
    let pronouns = document.getElementById("profile__user-pronouns");
    if (pronouns.textContent == "") {
        let ancestor = pronouns.closest("div.profile_body__content");
        hideElement(ancestor);
    }
});

/**
 * Hide Bio field if empty
 */
window.addEventListener('load', (event) => {
    let biography = document.getElementById("profile__user-biography");
    if (biography.textContent == "") {
        let ancestor = biography.closest("div.profile_body__content");
        hideElement(ancestor);
    }
});