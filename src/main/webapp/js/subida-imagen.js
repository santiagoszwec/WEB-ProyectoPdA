"use strict";
const form = document.getElementById("formImagen");
const archivoImagen = document.getElementById("archivoImagen");
const imagenUrl = document.getElementById("imagenUrl");
const imagenPublicId = document.getElementById("imagenPublicId");
const previewImagen = document.getElementById("previewImagen");
const contenedorPreview = document.getElementById("contenedorPreview");
const estadoImagen = document.getElementById("estadoImagen");
const botonGuardar = document.getElementById("botonGuardar");
const quitarImagen = document.getElementById("quitarImagen");
const TEXTO_BOTON = botonGuardar.dataset.texto || botonGuardar.textContent;
const MAX_BYTES = 5 * 1024 * 1024;
let urlLocal = null;
let enviando = false;

function mostrarEstado(mensaje, esError = false) {
    estadoImagen.textContent = mensaje;
    estadoImagen.classList.toggle("texto-error", esError);
}
function liberarVistaPrevia() { if (urlLocal) URL.revokeObjectURL(urlLocal); urlLocal = null; }
function limpiarImagen() {
    liberarVistaPrevia();
    archivoImagen.value = ""; imagenUrl.value = ""; imagenPublicId.value = "";
    previewImagen.removeAttribute("src"); contenedorPreview.hidden = true; mostrarEstado("");
}
function validarArchivo(archivo) {
    if (!["image/jpeg", "image/png"].includes(archivo.type)) throw new Error("Seleccioná una imagen JPG o PNG.");
    if (archivo.size === 0 || archivo.size > MAX_BYTES) throw new Error("La imagen debe tener contenido y pesar hasta 5 MB.");
}

archivoImagen.addEventListener("change", () => {
    liberarVistaPrevia();
    imagenUrl.value = ""; imagenPublicId.value = "";          // una nueva selección reemplaza la anterior
    previewImagen.removeAttribute("src"); contenedorPreview.hidden = true;
    const archivo = archivoImagen.files[0];
    if (!archivo) { mostrarEstado(""); return; }
    try {
        validarArchivo(archivo);
        urlLocal = URL.createObjectURL(archivo);
        previewImagen.src = urlLocal; contenedorPreview.hidden = false;
        mostrarEstado("Imagen seleccionada. Se subirá al guardar.");
    } catch (error) { archivoImagen.value = ""; mostrarEstado(error.message, true); }
});
quitarImagen.addEventListener("click", limpiarImagen);

if (imagenUrl.value) {
    previewImagen.src = imagenUrl.value; contenedorPreview.hidden = false;
    mostrarEstado("La imagen ya está subida. Podés reintentar el guardado.");
}

async function subirImagen(archivo) {
    if (form.dataset.configurado !== "true") throw new Error("La subida de imágenes aún no está configurada.");
    validarArchivo(archivo);
    const datos = new FormData();
    datos.append("file", archivo);
    datos.append("upload_preset", form.dataset.uploadPreset);
    const controlador = new AbortController();
    const temporizador = setTimeout(() => controlador.abort(), 60000);
    try {
        const respuesta = await fetch("https://api.cloudinary.com/v1_1/" + form.dataset.cloudName + "/image/upload",
            { method: "POST", body: datos, signal: controlador.signal });
        const resultado = await respuesta.json();
        if (!respuesta.ok) throw new Error(resultado.error?.message || "Cloudinary no pudo recibir la imagen.");
        const prefijo = "https://res.cloudinary.com/" + form.dataset.cloudName + "/image/upload/";
        if (typeof resultado.secure_url !== "string" || !resultado.secure_url.startsWith(prefijo)
            || typeof resultado.public_id !== "string" || !resultado.public_id)
            throw new Error("Cloudinary no devolvió los datos esperados para la imagen.");
        return { url: resultado.secure_url, publicId: resultado.public_id };
    } finally { clearTimeout(temporizador); }
}

form.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    if (enviando || !form.reportValidity()) return;
    enviando = true;
    botonGuardar.disabled = true; archivoImagen.disabled = true; quitarImagen.disabled = true;
    form.setAttribute("aria-busy", "true");
    let enviadoAlServidor = false;
    try {
        const archivo = archivoImagen.files[0];
        if (archivo && !imagenUrl.value) {
            botonGuardar.textContent = "Subiendo imagen…";
            mostrarEstado("Subiendo la imagen. Esperá un momento…");
            const imagen = await subirImagen(archivo);
            imagenUrl.value = imagen.url;
            imagenPublicId.value = imagen.publicId;
        }
        botonGuardar.textContent = "Guardando…";
        HTMLFormElement.prototype.submit.call(form);
        enviadoAlServidor = true;
    } catch (error) {
        mostrarEstado(error.name === "AbortError"
            ? "La subida demoró demasiado. Revisá tu conexión e intentá nuevamente."
            : "No se pudo completar la subida: " + error.message, true);
    } finally {
        if (!enviadoAlServidor) {
            enviando = false;
            botonGuardar.disabled = false;
            archivoImagen.disabled = form.dataset.configurado !== "true";
            quitarImagen.disabled = false;
            botonGuardar.textContent = TEXTO_BOTON;
            form.removeAttribute("aria-busy");
        }
    }
});