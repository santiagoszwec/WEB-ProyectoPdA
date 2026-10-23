<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/layout/header.jsp" />

<style>
    .texto-error { color: #dc3545; }
    .preview img { max-width: 240px; max-height: 240px; border-radius: .5rem; }
</style>

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

                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <div class="d-flex flex-wrap gap-2 align-items-center mb-2">
                            <strong>
                                <c:out value="${empty duda.autorNombre ? 'Usuario eliminado' : duda.autorNombre}" />
                            </strong>
                            <span class="text-muted small"><c:out value="${duda.fechaPublicacion}" /></span>
                            <span class="badge text-bg-info">
                                <c:out value="${duda.categoria eq 'Reunion' ? 'Reunión' : duda.categoria}" />
                            </span>
                            <span class="badge ${duda.estado == 'Abierta' ? 'text-bg-warning' : 'text-bg-success'}">
                                <c:out value="${duda.estado}" />
                            </span>
                        </div>

                        <p class="mb-2"><c:out value="${duda.mensaje}" /></p>

                        <c:if test="${not empty duda.imagenUrl}">
                            <img src="<c:out value='${duda.imagenUrl}' />" alt="Imagen de la duda"
                                 class="img-fluid rounded" style="max-height: 350px;" loading="lazy">
                        </c:if>
                    </div>
                </div>

                <h2 class="h5 mb-3">Respuestas (${respuestas.size()})</h2>

                <c:if test="${empty respuestas}">
                    <div class="alert alert-info">Esta duda todavía no tiene respuestas.</div>
                </c:if>

                <c:forEach var="respuesta" items="${respuestas}">
                    <div id="respuesta-${respuesta.id}"
                         class="card mb-3 ${respuesta.destacado ? 'border-success' : ''} ${respuestaAComentar.id == respuesta.id ? 'border-primary border-2' : ''}">
                        <div class="card-body">
                            <div class="d-flex flex-wrap gap-2 align-items-center mb-2">
                                <strong><c:out value="${respuesta.autorNombre}" /></strong>
                                <span class="text-muted small"><c:out value="${respuesta.fechaPublicacion}" /></span>
                                <c:if test="${respuesta.destacado}">
                                    <span class="badge text-bg-success">Más útil</span>
                                </c:if>
                            </div>

                            <p class="mb-2"><c:out value="${respuesta.mensaje}" /></p>

                                                       <c:choose>
                                                           <c:when test="${fn:endsWith(fn:toLowerCase(respuesta.imagenUrl), '.pdf')}">
                                                               <a href="<c:out value='${respuesta.imagenUrl}' />" target="_blank" rel="noopener"
                                                                  class="btn btn-sm btn-outline-secondary">📄 Ver PDF</a>
                                                           </c:when>
                                                           <c:when test="${not empty respuesta.imagenUrl}">
                                                               <img src="<c:out value='${respuesta.imagenUrl}' />" alt="Imagen adjunta"
                                                                    class="img-fluid rounded" style="max-height: 300px;" loading="lazy">
                                                           </c:when>
                                                           <c:otherwise>
                                                               <span class="text-muted small">Sin archivo adjunto</span>
                                                           </c:otherwise>
                                                       </c:choose>

                                                        <div class="mt-2 d-flex flex-wrap gap-2">
                                                            <a href="${pageContext.request.contextPath}/duda?id=${duda.id}&comentar=${respuesta.id}#formImagen"
                                                               class="btn btn-sm btn-outline-secondary">Comentar</a>

                                                            <c:if test="${puedeMarcarUtil}">
                                                                <form method="post" action="${pageContext.request.contextPath}/duda" class="d-inline"
                                                                      onsubmit="return confirm('¿Marcar esta respuesta como la más útil? La duda quedará resuelta.');">
                                                                    <input type="hidden" name="accion" value="marcarUtil">
                                                                    <input type="hidden" name="id" value="${duda.id}">
                                                                    <input type="hidden" name="respuesta" value="${respuesta.id}">
                                                                    <button type="submit" class="btn btn-sm btn-outline-success">Marcar como más útil</button>
                                                                </form>
                                                            </c:if>
                                                        </div>

                            <c:if test="${not empty respuesta.comentarios}">
                                <div class="mt-3 ms-4 ps-3 border-start">
                                    <c:forEach var="comentario" items="${respuesta.comentarios}">
                                        <div class="mb-3">
                                            <div class="small">
                                                <strong><c:out value="${comentario.autorNombre}" /></strong>
                                                <span class="text-muted"> · <c:out value="${comentario.fechaPublicacion}" /></span>
                                            </div>
                                            <p class="mb-1"><c:out value="${comentario.mensaje}" /></p>
                                                                                       <c:choose>
                                                                                           <c:when test="${fn:endsWith(fn:toLowerCase(comentario.imagenUrl), '.pdf')}">
                                                                                               <a href="<c:out value='${comentario.imagenUrl}' />" target="_blank" rel="noopener"
                                                                                                  class="btn btn-sm btn-outline-danger">📄 Ver PDF</a>
                                                                                           </c:when>
                                                                                           <c:when test="${not empty comentario.imagenUrl}">
                                                                                               <img src="<c:out value='${comentario.imagenUrl}' />" alt="Imagen adjunta"
                                                                                                    class="img-fluid rounded" style="max-height: 200px;" loading="lazy">
                                                                                           </c:when>
                                                                                       </c:choose>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
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

                <c:if test="${not empty errorRespuesta}">
                    <div class="alert alert-danger" role="alert"><c:out value="${errorRespuesta}" /></div>
                </c:if>

                <c:choose>
                    <c:when test="${puedeResponder or not empty respuestaAComentar}">

                        <form id="formImagen" action="${pageContext.request.contextPath}/duda" method="post"
                              class="card card-body shadow-sm"
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
                                <textarea id="texto" name="texto" class="form-control" rows="4"
                                          maxlength="2000" required><c:out value="${texto}" /></textarea>
                            </div>

                            <div class="mb-3">
                                            <label for="archivoImagen" class="form-label">Imagen o PDF <span class="text-muted">(opcional)</span></label>
                                            <input type="file" id="archivoImagen" class="form-control" accept="image/jpeg,image/png,application/pdf"
                                                   ${cloudinaryConfigurado ? '' : 'disabled'}>
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
                                                                          <button type="submit" id="botonGuardar" class="btn btn-primary" data-texto="Publicar comentario">Publicar comentario</button>
                                        <a href="${pageContext.request.contextPath}/duda?id=${duda.id}#respuesta-${respuestaAComentar.id}"
                                           class="btn btn-outline-secondary">Cancelar</a>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" id="botonGuardar" class="btn btn-primary" data-texto="Publicar respuesta">Publicar respuesta</button>                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </form>

                        <script src="${pageContext.request.contextPath}/js/subida-imagen.js" defer></script>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-secondary">Esta duda está resuelta: no admite nuevas respuestas.</div>
                    </c:otherwise>
                </c:choose>

            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />