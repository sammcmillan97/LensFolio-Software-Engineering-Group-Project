const shortNameMaxNumChars = 20;
const longNameMaxNumChars = 30;


/**
 * Simple function that updates char_count_short_name field to reflect how many characters are remaining for the
 * user to use
 */
function countShortNameCharacters() {
    let numOfEnteredChars = document.getElementById("group-form__short_name-field").value.length;
    let characterCounter = document.getElementById("char-count__short_name")
    characterCounter.textContent = String(shortNameMaxNumChars - numOfEnteredChars) + "/" + shortNameMaxNumChars + " characters remain";
}

/**
 * Simple function that updates char_count_short_name field to reflect how many characters are remaining for the
 * user to use
 */
function countLongNameCharacters() {
    let numOfEnteredChars = document.getElementById("group-form__long_name-field").value.length;
    let characterCounter = document.getElementById("char-count__long_name")
    characterCounter.textContent = String(longNameMaxNumChars - numOfEnteredChars) + "/" + longNameMaxNumChars + " characters remain";
}

/**
 * Update the character counts when the page loads
 */
window.addEventListener('load', (event) => {
    countLongNameCharacters();
    countShortNameCharacters();
});