<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/layout/header.jsp" />

<style>
    .texto-error { color: #dc3545; }
    .preview img { max-width: 240px; max-height: 240px; border-radius: .5rem; }
</style>

<%-- ★ NUEVO: meses y colores para fechas en texto y avatares --%>
<c:set var="meses" value="${['enero','febrero','marzo','abril','mayo','junio','julio','agosto','septiembre','octubre','noviembre','diciembre']}"/>
<c:set var="coloresAvatar" value="${['text-bg-primary', 'text-bg-success', 'text-bg-danger', 'text-bg-dark', 'text-bg-secondary']}"/>

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <c:if test="${not empty dudaMensaje}">
                <div class="alert alert-success" role="alert"><c:out value="${dudaMensaje}" /></div>
            </c:if>
            <c:if test="${not empty dudaError}">
                <div class="alert alert-danger" role="alert"><c:out value="${dudaError}" /></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert"><c:out value="${error}" /></div>
            </c:if>

            <c:if test="${not empty duda}">

                <a href="${pageContext.request.contextPath}/curso?id=${duda.cursoId}"
                   class="btn btn-link px-0 mb-3">← Volver al curso</a>

                <%-- ★ NUEVO: tarjeta de la duda --%>
                <c:set var="dudaAutor" value="${empty duda.autorNombre ? 'Usuario eliminado' : duda.autorNombre}"/>
                <c:set var="dudaPartes" value="${fn:split(dudaAutor, ' ')}"/>
                <c:set var="dudaIniciales"
                       value="${fn:toUpperCase(fn:substring(dudaPartes[0], 0, 1))}${fn:length(dudaPartes) > 1 ? fn:toUpperCase(fn:substring(dudaPartes[fn:length(dudaPartes) - 1], 0, 1)) : ''}"/>
                <c:set var="dudaF" value="${fn:split(duda.fechaPublicacion, '-')}"/>

                <article class="card pub-card mb-4">
                    <div class="card-body d-flex flex-column gap-2">

                        <div class="d-flex align-items-center gap-2">
                            <span class="rounded-circle d-flex align-items-center justify-content-center fw-bold small flex-shrink-0 ${coloresAvatar[fn:length(dudaAutor) % 5]}"
                                  style="width:32px;height:32px;"><c:out value="${dudaIniciales}"/></span>
                            <div class="flex-grow-1" style="min-width:0;">
                                <div class="fw-bold text-truncate" style="font-size:.95rem; line-height:1.25;"><c:out value="${dudaAutor}" /></div>
                                <div class="text-body-secondary" style="font-size:.8rem; line-height:1.3;">
                                    Publicado el ${dudaF[2] + 0} de ${meses[dudaF[1] - 1]} de ${dudaF[0]}
                                </div>
                            </div>
                            <span class="badge rounded-pill flex-shrink-0 fw-semibold ${duda.estado == 'Abierta' ? 'bg-success-subtle text-success-emphasis' : 'bg-secondary-subtle text-secondary-emphasis'}">
                                <c:out value="${duda.estado}" />
                            </span>
                        </div>

                        <hr class="pub-sep">

                        <p class="mb-0 text-body-secondary" style="font-size:.875rem; line-height:1.55; white-space:pre-line;"><c:out value="${duda.mensaje}" /></p>

                        <div class="d-flex flex-wrap gap-1">
                            <span class="badge rounded-pill fw-semibold pub-chip bg-primary-subtle text-primary">Duda</span>
                            <span class="badge rounded-pill fw-medium pub-chip pub-chip-cat">
                                <c:out value="${duda.categoria eq 'Reunion' ? 'Reunión' : duda.categoria}" />
                            </span>
                        </div>

                        <c:if test="${not empty duda.imagenUrl}">
                            <img src="<c:out value='${duda.imagenUrl}' />" alt="Imagen de la duda"
                                 class="img-fluid rounded-3 border align-self-start" style="max-height: 350px;" loading="lazy">
                        </c:if>
                    </div>
                </article>

                <h2 class="h5 mb-3">Respuestas (${respuestas.size()})</h2>

                <c:if test="${empty respuestas}">
                    <div class="alert alert-info">Esta duda todavía no tiene respuestas.</div>
                </c:if>

                <c:forEach var="respuesta" items="${respuestas}">

                    <%-- ★ NUEVO: datos de la respuesta (avatar, fecha, adjunto, botones) --%>
                    <c:set var="rAutor" value="${empty respuesta.autorNombre ? 'Usuario eliminado' : respuesta.autorNombre}"/>
                    <c:set var="rPartes" value="${fn:split(rAutor, ' ')}"/>
                    <c:set var="rIniciales"
                           value="${fn:toUpperCase(fn:substring(rPartes[0], 0, 1))}${fn:length(rPartes) > 1 ? fn:toUpperCase(fn:substring(rPartes[fn:length(rPartes) - 1], 0, 1)) : ''}"/>
                    <c:set var="rF" value="${fn:split(respuesta.fechaPublicacion, '-')}"/>
                    <c:set var="rEsMia" value="${respuesta.usuarioId == sessionScope.usuarioLogueado.id}"/>
                    <c:set var="rEsPdf" value="${not empty respuesta.imagenUrl and fn:endsWith(fn:toLowerCase(fn:substringBefore(respuesta.imagenUrl, '?')), '.pdf')}"/>
                    <c:if test="${rEsPdf}">
                        <c:set var="rRuta" value="${fn:split(fn:substringBefore(respuesta.imagenUrl, '?'), '/')}"/>
                        <c:set var="rArchivo" value="${rRuta[fn:length(rRuta) - 1]}"/>
                    </c:if>
                    <c:set var="rHayBotones" value="${cursoDelAnioActual or puedeMarcarUtil or rEsMia}"/>

                    <article id="respuesta-${respuesta.id}"
                             class="card pub-card mb-3 ${respuesta.destacado ? 'border-success' : ''} ${respuestaAComentar.id == respuesta.id ? 'border-primary border-2' : ''}">
                        <div class="card-body d-flex flex-column gap-2">

                            <div class="d-flex align-items-center gap-2">
                                <span class="rounded-circle d-flex align-items-center justify-content-center fw-bold small flex-shrink-0 ${coloresAvatar[fn:length(rAutor) % 5]}"
                                      style="width:32px;height:32px;"><c:out value="${rIniciales}"/></span>
                                <div class="flex-grow-1" style="min-width:0;">
                                    <div class="fw-bold text-truncate" style="font-size:.95rem; line-height:1.25;"><c:out value="${rAutor}" /></div>
                                    <div class="text-body-secondary" style="font-size:.8rem; line-height:1.3;">
                                        Publicado el ${rF[2] + 0} de ${meses[rF[1] - 1]} de ${rF[0]}
                                    </div>
                                </div>
                                <c:if test="${respuesta.destacado}">
                                    <span class="badge rounded-pill flex-shrink-0 fw-semibold bg-success-subtle text-success-emphasis">
                                        <i class="bi bi-check-circle-fill me-1"></i>Más útil
                                    </span>
                                </c:if>
                            </div>

                            <hr class="pub-sep">

                            <p class="mb-0 text-body-secondary" style="font-size:.875rem; line-height:1.55; white-space:pre-line;"><c:out value="${respuesta.mensaje}" /></p>

                            <c:choose>
                                <c:when test="${rEsPdf}">
                                    <div class="pub-adjunto p-2 d-flex align-items-center gap-2">
                                        <i class="bi bi-file-earmark-pdf-fill text-danger flex-shrink-0 text-center" style="width:32px; font-size:1.6rem;"></i>
                                        <div class="flex-grow-1" style="min-width:0;">
                                            <div class="fw-semibold text-truncate" style="font-size:.9rem;"><c:out value="${rArchivo}" /></div>
                                            <div class="text-body-secondary" style="font-size:.78rem;">Documento adjunto · PDF</div>
                                        </div>
                                        <a href="<c:out value='${respuesta.imagenUrl}' />" target="_blank" rel="noopener" download
                                           class="text-secondary fs-5 p-1" title="Descargar" aria-label="Descargar adjunto">
                                            <i class="bi bi-download"></i>
                                        </a>
                                    </div>
                                </c:when>
                                <c:when test="${not empty respuesta.imagenUrl}">
                                    <img src="<c:out value='${respuesta.imagenUrl}' />" alt="Imagen adjunta"
                                         class="img-fluid rounded-3 border align-self-start" style="max-height: 300px;" loading="lazy">
                                </c:when>
                                <c:otherwise>
                                    <span class="text-body-secondary small">Sin archivo adjunto</span>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${rHayBotones}">
                                <hr class="pub-sep">
                                <div class="d-flex flex-wrap align-items-center gap-2">
                                    <%-- solo se puede comentar en cursos del año actual --%>
                                    <c:if test="${cursoDelAnioActual}">
                                        <a href="${pageContext.request.contextPath}/duda?id=${duda.id}&comentar=${respuesta.id}#formImagen"
                                           class="btn btn-sm rounded-pill fw-semibold pub-btn pub-btn-claro">
                                            <i class="bi bi-chat-left-text me-1"></i>Comentar
                                        </a>
                                    </c:if>

                                    <c:if test="${puedeMarcarUtil}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/duda"
                                              class="d-inline"
                                              onsubmit="return confirm('¿Marcar esta respuesta como la más útil? La duda quedará resuelta.');">
                                            <input type="hidden" name="accion" value="marcarUtil">
                                            <input type="hidden" name="id" value="${duda.id}">
                                            <input type="hidden" name="respuesta" value="${respuesta.id}">

                                            <button type="submit" class="btn btn-sm rounded-pill fw-semibold pub-btn pub-btn-util">
                                                <i class="bi bi-check2-circle me-1"></i>Marcar como más útil
                                            </button>
                                        </form>
                                    </c:if>

                                    <c:if test="${rEsMia}">
                                        <button type="button"
                                                class="btn p-0 border-0 bg-transparent text-danger fs-5 ms-auto"
                                                data-bs-toggle="modal"
                                                data-bs-target="#modalEliminarRespuesta${respuesta.id}"
                                                title="Eliminar respuesta"
                                                aria-label="Eliminar respuesta">
                                            <i class="bi bi-trash3"></i>
                                        </button>
                                    </c:if>
                                </div>
                            </c:if>

                            <c:if test="${not empty respuesta.comentarios}">
                                <div class="mt-1 ms-4 ps-3 border-start">
                                    <c:forEach var="comentario" items="${respuesta.comentarios}">
                                        <div class="mb-3">
                                            <div class="small">
                                                <strong><c:out value="${comentario.autorNombre}" /></strong>
                                                <span class="text-body-secondary"> · <c:out value="${comentario.fechaPublicacion}" /></span>
                                            </div>
                                            <div class="d-flex justify-content-between align-items-start gap-2">
                                                <p class="mb-1 text-body-secondary" style="font-size:.875rem; line-height:1.55;"><c:out value="${comentario.mensaje}" /></p>

                                                <c:if test="${comentario.usuarioId == sessionScope.usuarioLogueado.id}">
                                                    <button type="button"
                                                            class="btn p-0 border-0 bg-transparent text-danger fs-5"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#modalEliminarComentario${comentario.id}"
                                                            title="Eliminar comentario"
                                                            aria-label="Eliminar comentario">
                                                        <i class="bi bi-trash3"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                            <c:choose>
                                                <c:when test="${fn:endsWith(fn:toLowerCase(comentario.imagenUrl), '.pdf')}">
                                                    <a href="<c:out value='${comentario.imagenUrl}' />" target="_blank" rel="noopener"
                                                       class="btn btn-sm rounded-pill fw-semibold pub-btn-claro">
                                                        <i class="bi bi-file-earmark-pdf me-1"></i>Ver PDF
                                                    </a>
                                                </c:when>
                                                <c:when test="${not empty comentario.imagenUrl}">
                                                    <img src="<c:out value='${comentario.imagenUrl}' />" alt="Imagen adjunta"
                                                         class="img-fluid rounded-3 border" style="max-height: 200px;" loading="lazy">
                                                </c:when>
                                            </c:choose>
                                        </div>
                                        <c:if test="${comentario.usuarioId == sessionScope.usuarioLogueado.id}">
                                            <div class="modal fade"
                                                 id="modalEliminarComentario${comentario.id}"
                                                 tabindex="-1"
                                                 aria-labelledby="tituloEliminarComentario${comentario.id}"
                                                 aria-hidden="true">

                                                <div class="modal-dialog modal-dialog-centered">
                                                    <div class="modal-content border-0 shadow">
                                                        <div class="modal-header border-0">
                                                            <h5 class="modal-title"
                                                                id="tituloEliminarComentario${comentario.id}">
                                                                ¿Eliminar comentario?
                                                            </h5>
                                                            <button type="button" class="btn-close"
                                                                    data-bs-dismiss="modal"
                                                                    aria-label="Cerrar"></button>
                                                        </div>

                                                        <div class="modal-body">
                                                            ¿Seguro que querés eliminar este comentario?
                                                        </div>

                                                        <div class="modal-footer border-0">
                                                            <button type="button" class="btn btn-light border"
                                                                    data-bs-dismiss="modal">
                                                                Cancelar
                                                            </button>

                                                            <form action="${pageContext.request.contextPath}/comentario/desactivar"
                                                                  method="post" class="m-0">
                                                                <input type="hidden" name="comentarioId"
                                                                       value="${comentario.id}">
                                                                <input type="hidden" name="dudaId"
                                                                       value="${duda.id}">

                                                                <button type="submit" class="btn btn-danger">
                                                                    <i class="bi bi-trash3 me-1"></i>
                                                                    Sí, eliminar
                                                                </button>
                                                            </form>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </article>
                    <c:if test="${rEsMia}">
                        <div class="modal fade"
                             id="modalEliminarRespuesta${respuesta.id}"
                             tabindex="-1"
                             aria-labelledby="tituloEliminarRespuesta${respuesta.id}"
                             aria-hidden="true">

                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content border-0 shadow">
                                    <div class="modal-header border-0">
                                        <h5 class="modal-title" id="tituloEliminarRespuesta${respuesta.id}">
                                            ¿Eliminar respuesta?
                                        </h5>
                                        <button type="button" class="btn-close"
                                                data-bs-dismiss="modal" aria-label="Cerrar"></button>
                                    </div>

                                    <div class="modal-body">
                                        ¿Seguro que querés eliminar esta respuesta?
                                    </div>

                                    <div class="modal-footer border-0">
                                        <button type="button" class="btn btn-light border"
                                                data-bs-dismiss="modal">Cancelar</button>

                                        <form action="${pageContext.request.contextPath}/comentario/desactivar"
                                              method="post" class="m-0">
                                            <input type="hidden" name="comentarioId" value="${respuesta.id}">
                                            <input type="hidden" name="dudaId" value="${duda.id}">
                                            <button type="submit" class="btn btn-danger">
                                                <i class="bi bi-trash3 me-1"></i> Sí, eliminar
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>

                <c:choose>
                    <c:when test="${not empty respuestaAComentar}">
                        <h2 class="h5 mt-4 mb-2">
                            Comentar la respuesta de <c:out value="${respuestaAComentar.autorNombre}" />
                        </h2>
                        <blockquote class="border-start ps-3 text-muted small mb-3">
                            <c:out value="${respuestaAComentar.mensaje}" />
                        </blockquote>
                    </c:when>
                    <c:otherwise>
                        <h2 class="h5 mt-4 mb-3">Responder</h2>
                    </c:otherwise>
                </c:choose>

                <c:if test="${not empty errorRespuesta and empty errorTexto and empty errorImagen}">
                    <div class="alert alert-danger" role="alert"><c:out value="${errorRespuesta}" /></div>
                </c:if>

                <c:choose>
                    <c:when test="${puedeResponder or not empty respuestaAComentar}">

                        <form id="formImagen" action="${pageContext.request.contextPath}/duda" method="post"
                              class="card pub-card card-body"
                              data-cloud-name="<c:out value='${cloudName}' />"
                              data-upload-preset="<c:out value='${uploadPreset}' />"
                              data-configurado="${cloudinaryConfigurado}">

                            <input type="hidden" name="id" value="${duda.id}">
                            <c:if test="${not empty respuestaAComentar}">
                                <input type="hidden" name="comentar" value="${respuestaAComentar.id}">
                            </c:if>
                            <input type="hidden" id="imagenUrl" name="imagenUrl" value="<c:out value='${imagenUrl}' />">
                            <input type="hidden" id="imagenPublicId" name="imagenPublicId" value="<c:out value='${imagenPublicId}' />">

                            <div class="mb-3">
                                <label for="texto" class="form-label">
                                    ${not empty respuestaAComentar ? 'Tu comentario' : 'Tu respuesta'}
                                    <span class="text-danger">*</span>
                                </label>
                                <textarea id="texto" name="texto" rows="4" maxlength="2000" required
                                          class="form-control ${not empty errorTexto ? 'is-invalid' : ''}"
                                          ${not empty errorTexto ? 'aria-describedby="errorTexto"' : ''}><c:out value="${texto}" /></textarea>
                                <c:if test="${not empty errorTexto}">
                                    <div id="errorTexto" class="invalid-feedback"><c:out value="${errorTexto}" /></div>
                                </c:if>
                            </div>

                            <div class="mb-3">
                                <label for="archivoImagen" class="form-label">Imagen o PDF <span class="text-muted">(opcional)</span></label>
                                <input type="file" id="archivoImagen" accept="image/jpeg,image/png,application/pdf"
                                       class="form-control ${not empty errorImagen ? 'is-invalid' : ''}"
                                       ${not empty errorImagen ? 'aria-describedby="errorImagen"' : ''}
                                       ${cloudinaryConfigurado ? '' : 'disabled'}>
                                <c:if test="${not empty errorImagen}">
                                    <div id="errorImagen" class="invalid-feedback"><c:out value="${errorImagen}" /></div>
                                </c:if>
                                <small class="text-muted">JPG, PNG o PDF, hasta 10 MB. Se sube al publicar.</small>

                                <c:if test="${not cloudinaryConfigurado}">
                                    <p class="alert alert-info mt-2 mb-0">La subida de imágenes aún no está configurada. Podés publicar sin imagen.</p>
                                </c:if>

                                <div id="contenedorPreview" class="preview mt-2" hidden>
                                    <img id="previewImagen" alt="Vista previa de la imagen">
                                    <button id="quitarImagen" type="button" class="btn btn-sm btn-outline-secondary ms-2">Quitar imagen</button>
                                </div>
                                <p id="estadoImagen" class="small mt-2 mb-0" role="status" aria-live="polite"></p>
                            </div>

                            <div class="d-flex gap-2">
                                <c:choose>
                                    <c:when test="${not empty respuestaAComentar}">
                                        <button type="submit" id="botonGuardar" class="btn btn-primary rounded-pill fw-semibold pub-btn" data-texto="Publicar comentario">Publicar comentario</button>
                                        <a href="${pageContext.request.contextPath}/duda?id=${duda.id}#respuesta-${respuestaAComentar.id}"
                                           class="btn btn-outline-secondary rounded-pill fw-semibold pub-btn">Cancelar</a>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" id="botonGuardar" class="btn btn-primary rounded-pill fw-semibold pub-btn" data-texto="Publicar respuesta">Publicar respuesta</button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </form>

                        <script src="${pageContext.request.contextPath}/js/subida-imagen.js" defer></script>
                    </c:when>
                    <c:otherwise>
                        <%-- el motivo depende de si el curso es de un año anterior o la duda ya está resuelta --%>
                        <c:choose>
                            <c:when test="${not cursoDelAnioActual}">
                                <div class="alert alert-secondary">Este curso es de un año anterior: no se pueden agregar respuestas ni comentarios.</div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-secondary">Esta duda está resuelta: no admite nuevas respuestas.</div>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>

            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />