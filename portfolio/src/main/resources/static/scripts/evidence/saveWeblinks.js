let index;

/**
 * Gets web links from backend and updates DOM
 * @param id
 * @returns {Promise<void>}
 */
async function getWebLinks(id) {
    let url
    url = new URL (`${CONTEXT}/getWebLinks-${id}`);
    const updatedEvidence = await fetch(url, {
        method: "GET"
    }).then(res => {
        return res.text()
    })
    // Update the page with the new HTML content
    const evidenceWrapper = document.getElementById(`web-link__wrapper_${id}`)
    evidenceWrapper.innerHTML = updatedEvidence
}

/**
 * Uses the Javascript fetch API to send the updated web link from the
 * edit modal. Then updates the web link on the page with the updated information.
 */
async function saveWebLink(id) {
    bootstrap.Modal.getInstance(document.getElementById(`addingWeblink_${id}`)).hide()
    // Build the url with the repository information as parameters
    let url
    url = new URL (`${CONTEXT}/addWebLink-${id}`);
    url.searchParams.append("webLinkName", document.getElementById(`weblink-modal__name-field_${id}`).value)
    url.searchParams.append("webLink", document.getElementById(`weblink-modal__link-field_${id}`).value)
    url.searchParams.append("webLinkIndex", index);

    // Send a post request to update the web link
    // Receives the updated element HTML content as a response
    const updatedEvidence = await fetch(url, {
        method: "POST"
    }).then(res => {
        return res.text()
    })
    // Update the page with the new HTML content
    const evidenceWrapper = document.getElementById(`web-link__wrapper_${id}`)
    evidenceWrapper.innerHTML = updatedEvidence
    return false;
}

// Clears the edit modal
function clearModel(id) {
    console.log(id);
    document.getElementById(`weblink-modal__name-field_${id}`).value = "";
    document.getElementById(`weblink-modal__link-field_${id}`).value = "";
}

// Sets index that of the web link in the modal. The index is that from the evidence web links array.
function setIndex(i) {
    index = i;
}

// Sets the modal to have details from the web link to edit.
function editWebLink(name, link, safe, id) {
    let isTrueSet = (safe === 'true');
    if (name) {
        document.getElementById(`weblink-modal__name-field_${id}`).value = name;
    }
    if (isTrueSet) {
        document.getElementById(`weblink-modal__link-field_${id}`).value = "https://" + link;
    } else {
        document.getElementById(`weblink-modal__link-field_${id}`).value = "http://" + link;
    }
}