let navOpen = false;

function openNav() {
    document.getElementById("site-navigation").classList.remove("collapsed");
    document.getElementById("site-navigation").classList.add("expanded");
    document.getElementById("page-content").classList.remove("expanded");
    document.getElementById("page-content").classList.add("constrict");
    document.getElementsByTagName("header").item(0).classList.add("constrict")
    document.getElementsByTagName("header").item(0).classList.remove("expanded")

}

function closeNav() {
    document.getElementById("site-navigation").classList.remove("expanded");
    document.getElementById("site-navigation").classList.add("collapsed");
    document.getElementById("page-content").classList.remove("constrict");
    document.getElementById("page-content").classList.add("expanded");
    document.getElementsByTagName("header").item(0).classList.add("expanded")
    document.getElementsByTagName("header").item(0).classList.remove("constrict")


}
function clickEvent() {
    if (navOpen === true) {
        navOpen = false;
        openNav();
    } else {
        navOpen = true;
        closeNav();
    }
}