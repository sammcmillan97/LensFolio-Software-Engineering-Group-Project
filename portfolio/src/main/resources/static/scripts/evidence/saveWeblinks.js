/**
 * Uses the Javascript fetch API to send the updated repository information from the
 * edit modal. Then updates the repository information on the page with the updated information.
 */
async function saveWebLink(id) {
    bootstrap.Modal.getInstance(document.getElementById(`addingWeblink_${id}`)).hide()
    // Build the url with the repository information as parameters
    console.log("ID: " + id);
    let url
    url = new URL (`${CONTEXT}/addWebLink-${id}`);
    url.searchParams.append("webLinkName", document.getElementById(`weblink-modal__name-field_${id}`).value)
    url.searchParams.append("webLink", document.getElementById(`weblink-modal__link-field_${id}`).value)

    console.log(id);
    // Send a post request to update the group repository
    // Receives the updated element HTML content as a response
    const updatedEvidence = await fetch(url, {
        method: "POST"
    }).then(res => {
        console.log("OH BABY: " + res.text());
        return res.text()
    })
    console.log("YEETERS");
    // Update the page with the new HTML content
    const evidenceWrapper = document.getElementById("evidence__form-wrapper")
    evidenceWrapper.innerHTML = updatedEvidence
    return false;
}