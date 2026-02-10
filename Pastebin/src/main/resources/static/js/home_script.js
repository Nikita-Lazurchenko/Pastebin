document.addEventListener("DOMContentLoaded", () => {
    const body = document.querySelector("body"),
        nav = document.querySelector("nav"),
        modeToggle = document.querySelector(".dark-light"),
        sidebarOpen = document.querySelector(".sidebarOpen"),
        siderbarClose = document.querySelector(".siderbarClose");

    let getMode = localStorage.getItem("mode");
    if (getMode && getMode === "dark-mode") {
        body.classList.add("dark");
        modeToggle.classList.add("active"); // Чтобы иконка тоже соответствовала
    }

    modeToggle.addEventListener("click", () => {
        modeToggle.classList.toggle("active");
        body.classList.toggle("dark");

        if (!body.classList.contains("dark")) {
            localStorage.setItem("mode", "light-mode");
        } else {
            localStorage.setItem("mode", "dark-mode");
        }
    });

    if (sidebarOpen) {
        sidebarOpen.addEventListener("click", () => {
            nav.classList.add("active");
        });
    }

    if (siderbarClose) {
        siderbarClose.addEventListener("click", () => {
            nav.classList.remove("active");
        });
    }


    body.addEventListener("click", e => {
        let clickedElm = e.target;
        if (!clickedElm.closest(".menu") && !clickedElm.closest(".sidebarOpen")) {
            nav.classList.remove("active");
        }
    });
});