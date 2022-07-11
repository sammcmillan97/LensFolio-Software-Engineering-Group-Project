let ctrlPressed;
let shiftPressed;
let lastRow;
let currentTable;

// disable text selection
document.onselectstart = function() {
    return false;
}

document.addEventListener("keyup", function (event) {
    if (event.key === "Control" || event.key === "Meta") {
        ctrlPressed = false;
    } else if (event.key === "Shift") {
        shiftPressed = false;
    }
}, false)

document.addEventListener("keydown", function (event) {
    if (event.key === "Control" || event.key === "Meta") {
        ctrlPressed = true;
    } else if (event.key === "Shift") {
        shiftPressed = true;
    } else if (event.key === "Escape") {
        clearTableSelection()
    }
}, false)

function rowClick(currRow) {
    currentTable = currRow.parentNode.getElementsByTagName("tr")
    console.log(currentTable)
    if (ctrlPressed) {
        toggleRow(currRow)
    } else if (shiftPressed) {
        selectRowsBetween([lastRow.rowIndex, currRow.rowIndex])
    } else {
        clearTableSelection()
        toggleRow(currRow)
    }
}

function toggleRow(row) {
    row.className = row.className === 'selected' ? 'unselected' : 'selected';
    lastRow = row;
}

function selectRowsBetween(indexes) {
    indexes.sort(function(a, b) {
        return a - b;
    });

    for (let i = indexes[0]; i <= indexes[1]; i++) {
        currentTable[i-1].className = 'selected';
    }
}

function clearTableSelection() {
    for (let i = 0; i < currentTable.length; i++) {
        currentTable[i].className = 'unselected';
    }
}