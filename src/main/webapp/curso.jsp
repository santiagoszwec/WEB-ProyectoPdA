<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">

        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <a href="${pageContext.request.contextPath}/misCursos"
               class="btn btn-outline-secondary mb-3">
                ← Volver a mis cursos
            </a>

            <h1 class="mb-2">
                <c:out value="${curso.nombre}" />
            </h1>

            <p class="text-muted mb-4">
                Año: <c:out value="${curso.anio}" />
                · Semestre: <c:out value="${curso.semestre}" />
                · Créditos: <c:out value="${curso.creditos}" />
                 <c:if test="${not empty estadoCurso}">
                    · Estado:
                    <c:choose>
                        <c:when test="${estadoCurso == 'Cursando'}"><span class="badge text-bg-primary">Cursando</span></c:when>
                        <c:when test="${estadoCurso == 'Cursada'}"><span class="badge text-bg-secondary">Cursada</span></c:when>
                        <c:when test="${estadoCurso == 'Aprobada'}"><span class="badge text-bg-success">Aprobada</span></c:when>
                    </c:choose>
                 </c:if>
            </p>

            <c:if test="${estadoCurso == 'Cursando'}">
                <a href="${pageContext.request.contextPath}/progreso?curso=${curso.id}"
                   class="btn btn-primary mb-4">
                    Mi progreso
                </a>
            </c:if>

            <c:if test="${not empty mensajeExito}">
                            <div class="alert alert-success" role="alert">
                                <c:out value="${mensajeExito}" />
                            </div>
            </c:if>

            <c:choose>
                            <c:when test="${puedePublicar}">
                                <div class="card shadow-sm mb-4">
                                    <div class="card-body">
                                        <h2 class="h5 mb-3">Nueva publicación</h2>

                                        <%-- Errores de validación que manda el servlet --%>
                                        <c:if test="${not empty errores}">
                                            <div class="alert alert-danger" role="alert">
                                                <ul class="mb-0">
                                                    <c:forEach var="error" items="${errores}">
                                                        <li><c:out value="${error}" /></li>
                                                    </c:forEach>
                                                </ul>
                                            </div>
                                        </c:if>

                                        <form id="formImagen"
                                              action="${pageContext.request.contextPath}/curso?id=${curso.id}"
                                              method="post" novalidate
                                              data-cloud-name="<c:out value='${cloudName}' />"
                                              data-upload-preset="<c:out value='${uploadPreset}' />"
                                              data-configurado="${cloudinaryConfigurado}">
                                            <input type="hidden" id="imagenUrl" name="imagenUrl" value="<c:out value='${valorImagenUrl}' />">
                                            <input type="hidden" id="imagenPublicId" name="imagenPublicId" value="<c:out value='${valorImagenPublicId}' />">

                                            <div class="mb-3">
                                                <label for="titulo" class="form-label">Título</label>
                                                <input type="text"
                                                       id="titulo"
                                                       name="titulo"
                                                       class="form-control"
                                                       maxlength="150"
                                                       required
                                                       value="<c:out value='${valorTitulo}' />">
                                            </div>

                                            <div class="mb-3">
                                                <label for="mensaje" class="form-label">Mensaje</label>
                                                <textarea id="mensaje" name="mensaje" class="form-control"
                                                          rows="3" maxlength="2000"><c:out value="${valorMensaje}" /></textarea>
                                            </div>

                                            <div class="mb-3">
                                                <label for="fechaLimite" class="form-label">
                                                    Fecha límite <span class="text-muted">(opcional)</span>
                                                </label>

                                                <input type="date"
                                                       id="fechaLimite"
                                                       name="fechaLimite"
                                                       class="form-control"
                                                       value="<c:out value='${valorFechaLimite}' />"
                                                       min="<%= java.time.LocalDate.now() %>">

                                            </div>

                                            <div class="mb-3">
                                                <label for="archivoImagen" class="form-label">Imagen o PDF <span class="text-muted">(opcional)</span></label>
                                                <input type="file" id="archivoImagen" class="form-control"
                                                       accept="image/jpeg,image/png,application/pdf"
                                                       ${cloudinaryConfigurado ? '' : 'disabled'}>
                                                <small class="text-muted">JPG, PNG o PDF, hasta 10 MB. Se sube al publicar.</small>

                                                <c:if test="${not cloudinaryConfigurado}">
                                                    <p class="alert alert-info mt-2 mb-0">La subida de archivos aún no está configurada. Podés publicar sin adjunto.</p>
                                                </c:if>

                                                <div id="contenedorPreview" class="preview mt-2" hidden>
                                                    <img id="previewImagen" alt="Vista previa del archivo">
                                                    <button id="quitarImagen" type="button" class="btn btn-sm btn-outline-secondary ms-2">Quitar archivo</button>
                                                </div>
                                                <p id="estadoImagen" class="small mt-2 mb-0" role="status" aria-live="polite"></p>
                                            </div>

                                            <div class="row g-3 mb-3">
                                                <div class="col-md-6">
                                                    <label for="tipo" class="form-label">Tipo</label>
                                                    <select id="tipo" name="tipo" class="form-select">
                                                        <option value="">Seleccioná un tipo</option>
                                                        <option value="Duda" ${valorTipo == 'Duda' ? 'selected' : ''}>Duda</option>
                                                        <option value="Mensaje" ${valorTipo == 'Mensaje' ? 'selected' : ''}>Mensaje</option>
                                                        <c:if test="${esAdmin}">
                                                            <option value="Material" ${valorTipo == 'Material' ? 'selected' : ''}>Material</option>
                                                        </c:if>
                                                    </select>
                                                </div>
                                                <div class="col-md-6" id="grupoCategoria">
                                                    <label for="categoria" class="form-label">Categoría</label>
                                                    <select id="categoria" name="categoria" class="form-select">
                                                        <option value="">Seleccioná una categoría</option>
                                                        <option value="Ejercicio" ${valorCategoria == 'Ejercicio' ? 'selected' : ''}>Ejercicio</option>
                                                        <option value="Examen" ${valorCategoria == 'Examen' ? 'selected' : ''}>Examen</option>
                                                        <option value="Reunion" ${valorCategoria == 'Reunion' ? 'selected' : ''}>Reunión</option>
                                                    </select>
                                                </div>
                                            </div>

                                            <div id="grupoMaterial" class="row g-3 mb-3" style="display: none;">
                                                <div class="col-md-6">
                                                    <label for="tipoMaterial" class="form-label">Tipo de material</label>
                                                    <select id="tipoMaterial" name="tipoMaterial" class="form-select">
                                                        <option value="">Seleccioná un tipo de material</option>
                                                        <option value="Apuntes" ${valorTipoMaterial == 'Apuntes' ? 'selected' : ''}>Apuntes</option>
                                                        <option value="Ejercicio" ${valorTipoMaterial == 'Ejercicio' ? 'selected' : ''}>Ejercicio</option>
                                                        <option value="Libro" ${valorTipoMaterial == 'Libro' ? 'selected' : ''}>Libro</option>
                                                        <option value="Video" ${valorTipoMaterial == 'Video' ? 'selected' : ''}>Video</option>
                                                    </select>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="tipoArchivo" class="form-label">Tipo de archivo</label>
                                                    <select id="tipoArchivo" name="tipoArchivo" class="form-select">
                                                        <option value="">Seleccioná un tipo de archivo</option>
                                                        <option value="JPG" ${valorTipoArchivo == 'JPG' ? 'selected' : ''}>JPG</option>
                                                        <option value="PDF" ${valorTipoArchivo == 'PDF' ? 'selected' : ''}>PDF</option>
                                                        <option value="PNG" ${valorTipoArchivo == 'PNG' ? 'selected' : ''}>PNG</option>
                                                    </select>
                                                </div>
                                                <div class="col-12">
                                                    <label for="archivoUrl" class="form-label">URL del archivo</label>
                                                    <input type="text" id="archivoUrl" name="archivoUrl"
                                                           class="form-control"
                                                           placeholder="https://ejemplo.com/archivo.pdf"
                                                           value="<c:out value='${valorArchivoUrl}' />">
                                                </div>
                                                <div class="col-12">
                                                    <label for="tema" class="form-label">Tema</label>
                                                    <input type="text" id="tema" name="tema"
                                                           class="form-control" maxlength="100"
                                                           placeholder="Ej.: Integrales definidas"
                                                           value="<c:out value='${valorTema}' />">
                                                </div>
                                            </div>

                                            <button type="submit" id="botonGuardar" class="btn btn-primary"
                                                    data-texto="Publicar en el curso">Publicar en el curso</button>
                                        </form>
                                        <script src="${pageContext.request.contextPath}/js/subida-imagen.js" defer></script>
                                    </div>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <div class="alert alert-secondary">
                                    Este curso no corresponde al año actual: se muestra solo para consulta.
                                </div>
                            </c:otherwise>
                        </c:choose>

            <hr>

            <h2 class="h4 mb-3">Material de Estudio</h2>

            <form method="get"
                  action="${pageContext.request.contextPath}/curso"
                  class="card shadow-sm mb-3">
                <div class="card-body">
                    <input type="hidden" name="id" value="${curso.id}">

                    <div class="row g-3 align-items-start">
                        <div class="col-md-5">
                            <span class="form-label fw-semibold d-block">Tipo de material</span>
                            <div class="d-flex flex-wrap gap-3">
                                <c:forEach var="opcion" items="${tiposMaterialDisponibles}">
                                    <c:set var="marcado" value="false" />
                                    <c:forEach var="elegido" items="${filtroTipoMaterial}">
                                        <c:if test="${elegido == opcion}">
                                            <c:set var="marcado" value="true" />
                                        </c:if>
                                    </c:forEach>
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox"
                                               name="tipoMaterial" value="${opcion}"
                                               id="tipoMaterial_${opcion}" ${marcado ? 'checked' : ''}>
                                        <label class="form-check-label" for="tipoMaterial_${opcion}">
                                            <c:out value="${opcion}" />
                                        </label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <span class="form-label fw-semibold d-block">Tipo de archivo</span>
                            <div class="d-flex flex-wrap gap-3">
                                <c:forEach var="opcion" items="${tiposArchivoDisponibles}">
                                    <c:set var="marcado" value="false" />
                                    <c:forEach var="elegido" items="${filtroTipoArchivo}">
                                        <c:if test="${elegido == opcion}">
                                            <c:set var="marcado" value="true" />
                                        </c:if>
                                    </c:forEach>
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox"
                                               name="tipoArchivo" value="${opcion}"
                                               id="tipoArchivo_${opcion}" ${marcado ? 'checked' : ''}>
                                        <label class="form-check-label" for="tipoArchivo_${opcion}">
                                            <c:out value="${opcion}" />
                                        </label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <div class="col-md-3 d-flex gap-2">
                            <button type="submit" class="btn btn-primary">Filtrar material</button>
                            <a href="${pageContext.request.contextPath}/curso?id=${curso.id}"
                               class="btn btn-outline-secondary">Limpiar filtros</a>
                        </div>
                    </div>

                    <p class="text-muted small mb-0 mt-2">
                        Podés combinar etiquetas: se muestran los materiales que cumplen
                        con lo seleccionado en cada tipo.
                    </p>
                </div>
            </form>

            <c:choose>
                <c:when test="${empty materiales}">
                    <div class="alert alert-info">
                        <c:choose>
                            <c:when test="${not empty filtroTipoMaterial or not empty filtroTipoArchivo}">
                                No hay material con esas etiquetas en este curso.
                            </c:when>
                            <c:otherwise>
                                Todavía no hay material cargado en este curso.
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row g-3 mb-4">
                        <c:forEach var="material" items="${materiales}">
                            <div class="col-md-6 col-xl-4 d-flex">
                                <%-- ★ NUEVO: antes decía "card shadow-sm w-100" --%>
                                <div class="card pub-reddit w-100">
                                    <div class="card-body">
                                        <div class="d-flex flex-wrap gap-2 mb-2">
                                            <span class="badge text-bg-dark">
                                                <c:out value="${material.tipoMaterial}" />
                                            </span>
                                            <span class="badge text-bg-secondary">
                                                <c:out value="${material.tipoArchivo}" />
                                            </span>
                                            <span class="badge text-bg-info">
                                                <c:out value="${material.tema}" />
                                            </span>
                                        </div>

                                        <p class="mb-2">
                                            <c:out value="${material.mensaje}" />
                                        </p>

                                        <c:if test="${not empty material.imagenUrl}">
                                            <img src="<c:out value='${material.imagenUrl}' />"
                                                 alt="Imagen del material"
                                                 class="img-fluid rounded mb-2"
                                                 style="max-height: 180px;">
                                        </c:if>

                                        <p class="text-muted small mb-2">
                                            Fecha: <c:out value="${material.fechaPublicacion}" />
                                        </p>

                                        <a href="<c:out value='${material.archivoUrl}' />"
                                           target="_blank" rel="noopener"
                                           class="btn btn-sm btn-outline-primary">
                                            Abrir archivo
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

            <hr>

            <h2 class="h4 mb-4">Publicaciones</h2>

            <c:choose>
                <c:when test="${empty publicaciones}">
                    <div class="alert alert-info">
                        Todavía no hay publicaciones en este curso
                    </div>
                </c:when>

                <c:otherwise>
                    <%-- ★ NUEVO: reemplaza todo el bloque de cards de publicaciones (con sus modales de eliminar).
                         Dentro del curso se muestra el autor y se habilitan Reportar / Editar / Eliminar. --%>
                    <c:set var="pubOcultarCurso" value="true" scope="request"/>
                    <c:set var="pubGestion" value="true" scope="request"/>
                    <div>
                        <c:forEach var="publicacion" items="${publicaciones}">
                            <c:set var="pub" value="${publicacion}" scope="request"/>
                            <jsp:include page="/layout/publicacion.jsp"/>
                        </c:forEach>
                    </div>
                    <%-- ★ FIN NUEVO --%>
                </c:otherwise>
            </c:choose>

            <c:if test="${not esAdmin}">
                <hr>

                <h2 class="h4 mb-3">Compañeros cursando</h2>

                <c:choose>
                    <c:when test="${empty companieros}">
                        <div class="alert alert-info">
                            No hay otros estudiantes cursando esta materia.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="d-flex flex-wrap gap-2">
                            <c:forEach var="companiero" items="${companieros}">
                                <a href="${pageContext.request.contextPath}/perfil?id=${companiero.id}"
                                   class="btn btn-outline-primary">
                                    <c:out value="${companiero.nombre}" />
                                </a>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </div>
</div>

<c:if test="${puedeReportar}">
    <div class="modal fade" id="modalReportar" tabindex="-1"
         aria-labelledby="tituloReportar" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/curso?id=${curso.id}"
                      method="post" novalidate>
                    <input type="hidden" name="accion" value="reportar">
                    <input type="hidden" name="publicacionId" id="reportePublicacionId">

                    <div class="modal-header">
                        <h5 class="modal-title" id="tituloReportar">Reportar publicación</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                aria-label="Cerrar"></button>
                    </div>

                    <div class="modal-body">
                        <%-- Autor y mensaje de la publicación seleccionada (los completa el script de abajo) --%>
                        <p class="mb-1"><strong>Autor:</strong> <span id="reporteAutor"></span></p>
                        <p class="mb-3"><strong>Mensaje:</strong></p>
                        <blockquote class="border-start border-3 ps-3 text-muted"
                                    id="reporteMensaje" style="white-space: pre-wrap;"></blockquote>

                        <%-- Error de validación, dentro del modal --%>
                        <c:if test="${not empty errorReporte}">
                            <div class="alert alert-danger py-2" role="alert" id="errorReporte">
                                <c:out value="${errorReporte}" />
                            </div>
                        </c:if>

                        <label for="reporteMotivo" class="form-label">Motivo del reporte</label>
                        <textarea id="reporteMotivo" name="motivo" class="form-control"
                                  rows="3" maxlength="1000"><c:out value="${valorMotivo}" /></textarea>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary"
                                data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-danger">Enviar reporte</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script>
        (function () {
            var modal = document.getElementById("modalReportar");
            var motivo = document.getElementById("reporteMotivo");
            var error = document.getElementById("errorReporte");

            // Datos que manda el servidor si el reporte salió con error (para reabrir el modal)
            var hayErrorEnReporte = ${not empty errorReporte ? 'true' : 'false'};
            var idPublicacionConError = ${empty reportePublicacionId ? 0 : reportePublicacionId};
            var reabriendo = false;

            // Al abrirse: se completan autor, mensaje e id con los datos del botón "Reportar" apretado
            modal.addEventListener("show.bs.modal", function (evento) {
                var boton = evento.relatedTarget;
                if (!boton) return;
                document.getElementById("reporteAutor").textContent =
                        boton.getAttribute("data-autor") || "Usuario eliminado";
                document.getElementById("reporteMensaje").textContent =
                        boton.getAttribute("data-mensaje");
                document.getElementById("reportePublicacionId").value =
                        boton.getAttribute("data-publicacion-id");

                // Apertura normal: modal limpio. Si se reabre por un error, se conserva lo escrito y el error
                if (!reabriendo) {
                    motivo.value = "";
                    if (error) error.style.display = "none";
                }
            });

            // Si el servidor devolvió un error, se "aprieta" de nuevo el botón de esa publicación
            // (cuando ya cargó todo, incluido el JS de Bootstrap)
            if (hayErrorEnReporte) {
                window.addEventListener("load", function () {
                    var boton = document.querySelector('[data-publicacion-id="' + idPublicacionConError + '"]');
                    if (boton) {
                        reabriendo = true;
                        boton.click();
                    }
                });
            }
        })();
    </script>
</c:if>


<script>
    (function () {
        // El tipo Material (solo administrador) pide datos extra y no categoría.
        var tipo = document.getElementById("tipo");
        var grupoMaterial = document.getElementById("grupoMaterial");
        var grupoCategoria = document.getElementById("grupoCategoria");
        if (tipo && grupoMaterial && grupoCategoria) {
            var actualizarTipo = function () {
                var esMaterial = tipo.value === "Material";
                grupoMaterial.style.display = esMaterial ? "flex" : "none";
                grupoCategoria.style.display = esMaterial ? "none" : "block";
            };
            tipo.addEventListener("change", actualizarTipo);
            actualizarTipo();
        }
    })();
</script>
<jsp:include page="/layout/footer.jsp" />