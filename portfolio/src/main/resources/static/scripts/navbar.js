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