let index;

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
 * Uses the Javascript fetch API to send the updated repository information from the
 * edit modal. Then updates the repository information on the page with the updated information.
 */
async function saveWebLink(id) {
    bootstrap.Modal.getInstance(document.getElementById(`addingWeblink_${id}`)).hide()
    // Build the url with the repository information as parameters
    let url
    url = new URL (`${CONTEXT}/addWebLink-${id}`);
    url.searchParams.append("webLinkName", document.getElementById(`weblink-modal__name-field_${id}`).value)
    url.searchParams.append("webLink", document.getElementById(`weblink-modal__link-field_${id}`).value)

    // Send a post request to update the group repository
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

function setIndex(i) {
    index = i;
}

function editWebLink(name, link, safe, id) {
    let isTrueSet = (safe === 'true');
    console.log(name+link+safe+id)
    if (name) {
        document.getElementById(`weblink-modal__name-field_${id}`).value = name;
    }
    if (isTrueSet) {
        document.getElementById(`weblink-modal__link-field_${id}`).value = "https://" + link
    } else {
        document.getElementById(`weblink-modal__link-field_${id}`).value = "http://" + link
    }
}