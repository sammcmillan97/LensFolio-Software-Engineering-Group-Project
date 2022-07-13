let ctrlPressed;
let shiftPressed;
let lastRow;
let currentTable;

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

/**
 * Unselects all items in the current table as unselected
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