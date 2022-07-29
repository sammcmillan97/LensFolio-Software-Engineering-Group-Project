/**
 * Takes the evidence html element and makes it expanded
 * @param evidence html element
 */
function expandEvidence(evidence) {
    evidence.getElementsByClassName("evidence__details collapse")[0].className = "evidence__details collapse show"
    evidence.getElementsByClassName("evidence__title")[0].setAttribute("aria-expanded", "true")
}