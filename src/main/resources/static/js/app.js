document.addEventListener("DOMContentLoaded", () => {
    renderIcons();

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

function renderIcons() {
    const icons = {
        "arrow-left": '<path d="M19 12H5"/><path d="m12 19-7-7 7-7"/>',
        ban: '<circle cx="12" cy="12" r="9"/><path d="m5 5 14 14"/>',
        "bell-ring": '<path d="M6 8a6 6 0 0 1 12 0c0 7 3 7 3 9H3c0-2 3-2 3-9"/><path d="M10 21h4"/><path d="M4 2 2 4"/><path d="m22 4-2-2"/>',
        "car-front": '<path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9L19 7c-.3-.9-1.1-1.5-2.1-1.5H7.1C6.1 5.5 5.3 6.1 5 7l-1.5 4.1C2.7 11.3 2 12.1 2 13v3c0 .6.4 1 1 1h2"/><path d="M6 17h12"/><path d="M6.5 14h.01"/><path d="M17.5 14h.01"/><path d="M6 17v2"/><path d="M18 17v2"/>',
        "chevron-left": '<path d="m15 18-6-6 6-6"/>',
        "chevron-right": '<path d="m9 18 6-6-6-6"/>',
        eye: '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/>',
        "layout-dashboard": '<rect width="7" height="9" x="3" y="3" rx="1"/><rect width="7" height="5" x="14" y="3" rx="1"/><rect width="7" height="9" x="14" y="12" rx="1"/><rect width="7" height="5" x="3" y="16" rx="1"/>',
        "log-in": '<path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><path d="m10 17 5-5-5-5"/><path d="M15 12H3"/>',
        "log-out": '<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="m16 17 5-5-5-5"/><path d="M21 12H9"/>',
        plus: '<path d="M12 5v14"/><path d="M5 12h14"/>',
        pencil: '<path d="M17 3a2.8 2.8 0 0 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/>',
        receipt: '<path d="M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2Z"/><path d="M8 7h8"/><path d="M8 12h8"/><path d="M8 17h5"/>',
        "rotate-ccw": '<path d="M3 12a9 9 0 1 0 3-6.7"/><path d="M3 3v6h6"/>',
        save: '<path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2Z"/><path d="M17 21v-8H7v8"/><path d="M7 3v5h8"/>',
        search: '<circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/>',
        "shield-check": '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10Z"/><path d="m9 12 2 2 4-4"/>',
        "square-pen": '<path d="M12 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.4 2.6a2.1 2.1 0 0 1 3 3L12 15l-4 1 1-4Z"/>',
        "trash-2": '<path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/>',
        truck: '<path d="M10 17h4V5H2v12h3"/><path d="M14 17h1"/><path d="M19 17h3v-6l-3-4h-5"/><circle cx="7.5" cy="17.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/>',
        user: '<path d="M19 21a7 7 0 0 0-14 0"/><circle cx="12" cy="7" r="4"/>',
        "user-plus": '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M19 8v6"/><path d="M22 11h-6"/>',
        users: '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.9"/><path d="M16 3.1a4 4 0 0 1 0 7.8"/>',
        wrench: '<path d="M14.7 6.3a4 4 0 0 0-5 5L3 18l3 3 6.7-6.7a4 4 0 0 0 5-5L15 12l-3-3Z"/>',
        x: '<path d="M18 6 6 18"/><path d="m6 6 12 12"/>'
    };

    document.querySelectorAll("[data-lucide]").forEach((icon) => {
        const name = icon.getAttribute("data-lucide");
        const paths = icons[name];
        if (!paths) return;

        const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        svg.setAttribute("viewBox", "0 0 24 24");
        svg.setAttribute("fill", "none");
        svg.setAttribute("stroke", "currentColor");
        svg.setAttribute("stroke-width", "2");
        svg.setAttribute("stroke-linecap", "round");
        svg.setAttribute("stroke-linejoin", "round");
        svg.setAttribute("aria-hidden", "true");
        svg.setAttribute("class", icon.getAttribute("class") || "");
        svg.innerHTML = paths;
        icon.replaceWith(svg);
    });
}

