let ctrlPressed;
let shiftPressed;
let lastRow;
let currentTable;
let clipboard;
let copiedRow;
let ghostElement

// disable text selection
document.onselectstart = function() {
    return false;
}

/**
 * Key released event listener
 * Marks control/command or shift pressed variables as false if they've been released
 */
document.addEventListener("keyup", function (event) {
    if (event.key === "Control" || event.key === "Meta") {
        ctrlPressed = false;
    } else if (event.key === "Shift") {
        shiftPressed = false;
    }
}, false)

/**
 * Key pressed down event listener
 * Marks the control/command or shift pressed variables as true if they've been pressed
 * If control/command is down and A is pressed, it selects all in the current table
 */
document.addEventListener("keydown", function (event) {
    if (event.key === "Control" || event.key === "Meta") {
        ctrlPressed = true;
    } else if (event.key === "Shift") {
        shiftPressed = true;
    } else if (event.key === "Escape") {
        clearTableSelection()
    } else if (event.key === "a" && ctrlPressed) {
        selectAllInTable()
    }
}, false)

document.addEventListener("dragstart", function (e) {
    clearTableSelection()
    selectRows(clipboard)
})

function allowDrop(ev) {
    ev.preventDefault();
}

async function copyMembers(currRow) {
    currentTable = currRow.parentNode.getElementsByTagName("tr")
    copiedRow = currRow
    clipboard = []
    // Ensure the clicked row is selected
    if (currRow.className !== "selected") {
        clipboard.push(currRow)
    } else {
        for (let row of currentTable) {
            if (row.className === "selected") {
                clipboard.push(row)
            }
        }
    }
}

function getGroupId(row) {

}

function pasteMembers(currRow) {
    const newTable = currRow.parentNode

    if (currentTable !== newTable.getElementsByTagName("tr")) {
        let userIds = []
        let userId
        for (let element of clipboard) {
            userId = element.getElementsByClassName("user_id")[0].innerText
            userIds.push(parseInt(userId, 10))
        }
        console.log(userIds)
    }
}

/**
 * Occurs when a row is clicked on
 * Takes the clicked on row, checks if Control or Shift are being held and performs the relevant selection action
 * @param currRow the row that was clicked on
 */
function rowClick(currRow) {
    // Set the current table to the one that the clicked on row belongs to
    currentTable = currRow.parentNode.getElementsByTagName("tr")
    if (ctrlPressed) { // Toggle the clicked on row if Control is pressed
        toggleRow(currRow)
    } else if (shiftPressed) { // Select all rows between (inclusive) the current and last clicked on rows
        selectRowsBetween([lastRow.rowIndex, currRow.rowIndex])
    } else { // Otherwise, clear any other selected rows and mark the clicked on row
        clearTableSelection()
        toggleRow(currRow)
    }
}

/**
 * Takes a row and toggles it from selected to unselected, or from unselected to selected
 * @param row the row to have selection toggled
 */
function toggleRow(row) {
    row.className = row.className === 'selected' ? 'unselected' : 'selected';
    lastRow = row;
}

/**
 * Takes a list of two indexes, sorts them, then marks all rows of the table between (inclusive) those indices as selected
 * @param indexes two table indexes in random order
 */
function selectRowsBetween(indexes) {
    indexes.sort(function(a, b) {
        return a - b;
    });

    for (let i = indexes[0]; i <= indexes[1]; i++) {
        currentTable[i-1].className = 'selected';
    }
}

function selectRows(rows) {
    for (let row of rows) {
        row.className = "selected"
    }
}

/**
 * Unselects all items in the current table
 */
function clearTableSelection() {
    for (let i = 0; i < currentTable.length; i++) {
        currentTable[i].className = 'unselected';
    }
}

/**
 * Selects all items in the current table
 */
function selectAllInTable() {
    for (let i = 0; i < currentTable.length; i++) {
        currentTable[i].className = 'selected';
    }
}

/**
 * Unselects all items in all tables
 */
function clearAllTableSelections() {
    if (currentTable) {
        // Fetch all tables by getting all elements with the same class name as the current table
        const allTables = document.getElementsByClassName(currentTable[0].parentNode.parentNode.className)

        let tableRows;
        // Iterate through each table, fetch the table elements, then mark them all as unselected
        for (const table of allTables) {
            tableRows = table.getElementsByTagName("tr");

            // Mark each item in the table as unselected
            for (let i = 1; i < tableRows.length; i++) { // starts at 1 to skip header row
                tableRows[i].className = 'unselected';
            }
        }
    }
}