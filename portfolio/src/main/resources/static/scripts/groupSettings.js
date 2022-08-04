/**
 * Uses the Javascript fetch API to fetch updated repository information
 * then updates the page to reflect the new information.
 */
async function updateGroupRepositoryElement(firstLoad) {
    // Build the url
    let url
    url = new URL (`${CONTEXT}/groupSettings-${GROUP_ID}-repository`)
    url.searchParams.append("firstLoad", firstLoad)

    // Send a get request to fetch the updated group repository
    // Receives the updated element HTML content as a response
    const updatedRepositoryInformation = await fetch(url, {
        method: "GET"
    }).then(res => {
        return res.text()
    })

    // Update the page with the new HTML content
    const groupRepositoryWrapper = document.getElementById("repository_container")
    groupRepositoryWrapper.innerHTML = updatedRepositoryInformation
}

/**
 * Uses the Javascript fetch API to send the updated repository information from the
 * edit modal. Then updates the repository information on the page with the updated information.
 */
async function saveGroupRepositorySettings() {
    bootstrap.Modal.getInstance(document.getElementById("group-repository-settings-modal")).hide()
    // Build the url with the repository information as parameters
    let url
    url = new URL (`${CONTEXT}/groupSettings-${GROUP_ID}-repository`)
    url.searchParams.append("repositoryName", document.getElementById("group_repository_name").value)
    url.searchParams.append("gitlabAccessToken", document.getElementById("group_repository_api_key").value)
    url.searchParams.append("gitlabProjectId", document.getElementById("group_repository_id").value)
    url.searchParams.append("gitlabServerUrl", document.getElementById("group_repository_server_url").value)


    // Send a post request to update the group repository
    // Receives the updated element HTML content as a response
    const updatedRepositoryInformation = await fetch(url, {
        method: "POST"
    }).then(res => {
        return res.text()
    })

    // Update the page with the new HTML content
    const groupRepositoryWrapper = document.getElementById("repository_container")
    groupRepositoryWrapper.innerHTML = updatedRepositoryInformation
    return false;
}