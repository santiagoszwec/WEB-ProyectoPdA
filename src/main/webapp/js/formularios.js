"use strict";

document.addEventListener("submit", (evento) => {
    const form = evento.target;


    if (form.id === "formImagen" || evento.defaultPrevented) return;

    // Segundo clic mientras se envía: se ignora.
    if (form.dataset.enviando === "true") {
        evento.preventDefault();
        return;
    }

    const boton = evento.submitter || form.querySelector('[type="submit"]');
    if (!boton) return;

    form.dataset.enviando = "true";
    form.setAttribute("aria-busy", "true");

    const texto = boton.dataset.cargando
        || (form.method.toLowerCase() === "get" ? "Buscando…" : "Procesando…");
    boton.dataset.textoOriginal = boton.innerHTML;
    boton.innerHTML = '<span class="spinner-border spinner-border-sm me-1" aria-hidden="true"></span>' + texto;


    setTimeout(() => { boton.disabled = true; }, 0);
});

window.addEventListener("pageshow", (evento) => {
    if (!evento.persisted) return;
    document.querySelectorAll('form[data-enviando="true"]').forEach((form) => {
        form.dataset.enviando = "false";
        form.removeAttribute("aria-busy");
        form.querySelectorAll("[data-texto-original]").forEach((boton) => {
            boton.innerHTML = boton.dataset.textoOriginal;
            boton.disabled = false;
        });
    });
});