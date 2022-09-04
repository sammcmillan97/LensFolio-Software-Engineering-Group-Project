let toggle = 0;

function expandNav() {
    document.getElementById("site-navigation").classList.remove("collapsed");
    document.getElementById("site-navigation").classList.add("expanded");
    document.getElementById("page-content").classList.remove("expanded");
    document.getElementById("page-content").classList.add("constrict");
    document.getElementById("page-header").classList.add("constrict")
    document.getElementById("page-header").classList.remove("expanded")

}

function collapseNav() {
    document.getElementById("site-navigation").classList.remove("expanded");
    document.getElementById("site-navigation").classList.add("collapsed");
    document.getElementById("page-content").classList.remove("constrict");
    document.getElementById("page-content").classList.add("expanded");
    document.getElementById("page-header").classList.add("expanded")
    document.getElementById("page-header").classList.remove("constrict")


}
function toggleNavbarVisibility() {
    if (document.getElementById("site-navigation").classList.contains("collapsed")) {
        expandNav();
    } else {
        collapseNav();
    }
}

/**
 * Function to handle dropdown when arrow clicked.
 */
function dropDown() {
    let dropdown = document.getElementById("js-dropdown__list");
    if (toggle === 0) {
        dropdown.style.display = 'block';
        toggle = 1;
    } else {
        dropdown.style.display = 'none';
        toggle = 0;
    }
}

/**
 * Event listeners and function for checking when clicked off dropdown to close it.
 */
document.addEventListener("touchend", eventHandle)
document.addEventListener("click", eventHandle)
function eventHandle(e) {
    let dropdown = document.getElementById("js-dropdown__list");
    let classList = e.target.classList;
    if (!(classList.contains("dropdown") || classList.contains("clear-decoration") || classList.contains("navigation_icon")
        || classList.contains("dropdown__container") || classList.contains("dropdown-content")
        || classList.contains("site-navigation__sublabel") || classList.contains("site-navigation__sublink"))) {
        dropdown.style.display = 'none';
        toggle = 0;
    }
}

/**
 * Function handles hovering over dropdown open and close.
 */
document.addEventListener("mouseover", (e) => {
    let dropdown = document.getElementById("js-dropdown__list");
    let classList = e.target.classList;
    if (classList.contains("dropdown") || classList.contains("clear-decoration") || classList.contains("navigation_icon")
        || classList.contains("dropdown__container") || classList.contains("dropdown-content")
        || classList.contains("site-navigation__sublabel") || classList.contains("site-navigation__sublink")) {
        dropdown.style.display = 'block';
    } else if (toggle === 0) {
        dropdown.style.display = 'none';
    }
})