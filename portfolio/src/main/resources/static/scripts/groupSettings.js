async function updateGroupRepositoryElement() {
    // Build the url
    let url
    url = new URL (`${CONTEXT}/groupSettings-${GROUP_ID}-repository`)

    // Send a get request to fetch the updated group repository
    // Receives the updated element HTML content as a response
    const response = await fetch(url, {
        method: "GET"
    }).then(res => {
        return res.text()
    })

    const groupRepositoryWrapper = document.getElementById("repository_container")
    groupRepositoryWrapper.innerHTML = response
}

async function saveGroupRepositorySettings() {
    // Build the url with the repository information as parameters
    let url
    url = new URL (`${CONTEXT}/groupSettings-${GROUP_ID}-repository`)
    url.searchParams.append("repositoryName", document.getElementById("group_repository_name").value)
    url.searchParams.append("repositoryApiKey", document.getElementById("group_repository_api_key").value)
    url.searchParams.append("repositoryId", document.getElementById("group_repository_id").value)
    url.searchParams.append("repositoryServerUrl", document.getElementById("group_repository_server_url").value)


    // Send a post request to update the group repository
    // Receives the updated element HTML content as a response
    const response = await fetch(url, {
        method: "POST"
    }).then(res => {
        return res.text()
    })

    const groupRepositoryWrapper = document.getElementById("repository_container")
    groupRepositoryWrapper.innerHTML = response
}