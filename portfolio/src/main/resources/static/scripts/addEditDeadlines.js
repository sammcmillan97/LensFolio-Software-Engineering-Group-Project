const maxNumOfChars = 30;

/**
 * Simple function that updates char_count field to reflect how many characters are remaining for the
 * user to use
 */
function countCharacters() {
    let numOfEnteredChars = document.getElementById("form__name-field").value.length;
    let characterCounter = document.getElementById("char_count")
    characterCounter.textContent = String(maxNumOfChars - numOfEnteredChars) + "/30 characters remain";
}

/**
 * Update the min end date and max start date of the event when the page loads
 */
window.addEventListener('load', (event) => {
    countCharacters();
});