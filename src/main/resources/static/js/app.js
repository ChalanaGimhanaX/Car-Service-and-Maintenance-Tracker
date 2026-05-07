document.addEventListener("DOMContentLoaded", () => {
    if (window.lucide) {
        lucide.createIcons();
    }

    document.querySelectorAll("[data-password-toggle]").forEach((toggle) => {
        toggle.addEventListener("click", () => {
            const target = document.querySelector(toggle.getAttribute("data-password-toggle"));
            if (!target) return;
            const nextType = target.getAttribute("type") === "password" ? "text" : "password";
            target.setAttribute("type", nextType);
            toggle.dataset.state = nextType === "password" ? "hidden" : "visible";
        });
    });
});

