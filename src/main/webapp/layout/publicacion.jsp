<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    Tarjeta de publicación (solo Bootstrap + estilos .pub-card de general.css). Se usa en toda la web.
    Uso:  <c:set var="pub" value="${publicacion}" scope="request"/>
          <jsp:include page="/layout/publicacion.jsp"/>
    Opcionales (request): pubOcultarCurso="true"  -> dentro de un curso (muestra al autor en vez de la materia)
                          pubGestion="true"       -> habilita Reportar / Editar / Eliminar (solo curso.jsp)
--%>
<c:set var="pubEsDuda" value="${pub.tipo eq 'Duda'}"/>
<c:set var="pubAutor" value="${empty pub.autor ? 'Usuario eliminado' : pub.autor}"/>
<c:set var="pubOcultar" value="${pubOcultarCurso eq 'true'}"/>
<c:set var="pubCurso" value="${empty pub.nombreCurso ? pub.curso : pub.nombreCurso}"/>
<c:set var="pubNombre" value="${pubOcultar ? pubAutor : (empty pubCurso ? 'Publicación' : pubCurso)}"/>
<c:set var="pubTieneCurso" value="${pub.cursoId gt 0}"/>
<c:set var="pubEsMia" value="${pubGestion eq 'true' and pub.usuarioId == sessionScope.usuarioLogueado.id}"/>
<c:set var="pubReportar" value="${pubGestion eq 'true' and puedeReportar}"/>
<c:set var="pubVerCurso" value="${pubTieneCurso and not pubOcultar}"/>

<%-- Avatar (iniciales y color, como antes) --%>
<c:set var="pubPartes" value="${fn:split(pubNombre, ' ')}"/>
<c:set var="pubIniciales"
       value="${fn:toUpperCase(fn:substring(pubPartes[0], 0, 1))}${fn:length(pubPartes) > 1 ? fn:toUpperCase(fn:substring(pubPartes[fn:length(pubPartes) - 1], 0, 1)) : ''}"/>
<c:set var="pubColores" value="${['text-bg-primary', 'text-bg-success', 'text-bg-danger', 'text-bg-dark', 'text-bg-secondary']}"/>
<c:set var="pubColor" value="${pubColores[fn:length(pubNombre) % 5]}"/>

<%-- ★ NUEVO: fechas en texto ("19 de octubre de 2026" y "20/10/2026") --%>
<c:set var="pubMeses" value="${['enero','febrero','marzo','abril','mayo','junio','julio','agosto','septiembre','octubre','noviembre','diciembre']}"/>
<c:if test="${not empty pub.fechaPublicacion}">
    <c:set var="pubF" value="${fn:split(pub.fechaPublicacion, '-')}"/>
    <c:set var="pubFechaTexto" value="${pubF[2] + 0} de ${pubMeses[pubF[1] - 1]} de ${pubF[0]}"/>
</c:if>
<c:if test="${not empty pub.fechaLimite}">
    <c:set var="pubL" value="${fn:split(pub.fechaLimite, '-')}"/>
    <c:set var="pubLimiteTexto" value="${pubL[2]}/${pubL[1]}/${pubL[0]}"/>
</c:if>

<%-- ★ NUEVO: adjunto (PDF con nombre de archivo, o imagen) --%>
<c:set var="pubTieneAdjunto" value="${not empty pub.imagenUrl}"/>
<c:set var="pubEsPdf" value="${pubTieneAdjunto and fn:endsWith(fn:toLowerCase(fn:substringBefore(pub.imagenUrl, '?')), '.pdf')}"/>
<c:if test="${pubEsPdf}">
    <c:set var="pubRutaPartes" value="${fn:split(fn:substringBefore(pub.imagenUrl, '?'), '/')}"/>
    <c:set var="pubArchivo" value="${pubRutaPartes[fn:length(pubRutaPartes) - 1]}"/>
</c:if>

<c:set var="pubDestino"
       value="${pageContext.request.contextPath}${pubEsDuda ? '/duda?id=' : '/curso?id='}${pubEsDuda ? pub.id : pub.cursoId}"/>
<c:set var="pubHayMenu" value="${pubEsDuda or pubVerCurso}"/>
<c:set var="pubHayPie" value="${pubEsDuda or not empty pub.fechaLimite}"/>
<c:set var="pubHayBotones" value="${pubEsDuda or pubVerCurso or pubEsPdf or pubReportar or pubEsMia}"/>

<article class="card pub-card position-relative mb-3" id="publicacion-${pub.id}">
    <%-- Toda la tarjeta es clickeable (si hay a dónde ir) --%>
    <c:if test="${pubEsDuda or pubVerCurso}">
        <a href="${pubDestino}" class="stretched-link"
           aria-label="Abrir la publicación <c:out value='${pub.titulo}'/>"></a>
    </c:if>

    <div class="card-body d-flex flex-column gap-2">

        <%-- Cabecera: avatar, nombre, fecha, estado y menú --%>
        <div class="d-flex align-items-center gap-2">
            <span class="rounded-circle d-flex align-items-center justify-content-center fw-bold small flex-shrink-0 ${pubColor}"
                  style="width:32px;height:32px;"><c:out value="${pubIniciales}"/></span>

            <div class="flex-grow-1" style="min-width:0;">
                <div class="fw-bold text-truncate" style="font-size:.95rem; line-height:1.25;"><c:out value="${pubNombre}"/></div>
                <div class="text-body-secondary" style="font-size:.8rem; line-height:1.3;">
                    <c:if test="${not pubOcultar}"><c:out value="${pubAutor}"/> · </c:if>Publicado el <c:out value="${pubFechaTexto}"/>
                </div>
            </div>

            <c:if test="${pubEsDuda and not empty pub.estado}">
                <span class="badge rounded-pill flex-shrink-0 fw-semibold ${pub.estado eq 'Abierta' ? 'bg-success-subtle text-success-emphasis' : 'bg-secondary-subtle text-secondary-emphasis'}">
                    <c:out value="${pub.estado}"/>
                </span>
            </c:if>

            <%-- Menú "···": accesos de navegación --%>
            <c:if test="${pubHayMenu}">
                <div class="dropdown position-relative z-1">
                    <button type="button" class="btn btn-sm btn-link text-secondary rounded-circle p-1"
                            data-bs-toggle="dropdown" aria-expanded="false" aria-label="Más opciones">
                        <i class="bi bi-three-dots fs-5"></i>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow">
                        <c:if test="${pubVerCurso}">
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/curso?id=${pub.cursoId}">
                                    <i class="bi bi-box-arrow-up-right me-2"></i>Ver en el curso
                                </a>
                            </li>
                        </c:if>
                        <c:if test="${pubEsDuda}">
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/duda?id=${pub.id}">
                                    <i class="bi bi-chat-left-text me-2"></i>${pub.estado eq 'Abierta' ? 'Ver y responder' : 'Ver duda'}
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </c:if>
        </div>

        <hr class="pub-sep">

        <%-- Título y mensaje --%>
        <div>
            <c:if test="${not empty pub.titulo}">
                <h3 class="fw-bold mb-1" style="font-size:1.05rem; line-height:1.3;"><c:out value="${pub.titulo}"/></h3>
            </c:if>
            <p class="mb-0 text-body-secondary" style="font-size:.875rem; line-height:1.55; white-space:pre-line;"><c:out value="${pub.mensaje}"/></p>
        </div>

        <%-- Etiquetas: tipo y categoría --%>
        <div class="d-flex flex-wrap gap-1">
            <span class="badge rounded-pill fw-semibold pub-chip ${pub.tipo eq 'Duda' ? 'bg-primary-subtle text-primary' : (pub.tipo eq 'Material' ? 'bg-success-subtle text-success-emphasis' : 'bg-info-subtle text-info-emphasis')}">
                <c:out value="${pub.tipo}"/>
            </span>
            <c:if test="${not empty pub.categoria}">
                <span class="badge rounded-pill fw-medium pub-chip pub-chip-cat">
                    <c:out value="${pub.categoria eq 'Reunion' ? 'Reunión' : pub.categoria}"/>
                </span>
            </c:if>
        </div>

        <%-- Imagen --%>
        <c:if test="${pubTieneAdjunto and not pubEsPdf}">
            <img src="<c:out value='${pub.imagenUrl}'/>" alt="Imagen de la publicación"
                 class="img-fluid rounded-3 border align-self-start" style="max-height:350px;">
        </c:if>

        <%-- PDF --%>
        <c:if test="${pubEsPdf}">
            <div class="pub-adjunto p-2 d-flex align-items-center gap-2">
                <i class="bi bi-file-earmark-pdf-fill text-danger flex-shrink-0 text-center" style="width:32px; font-size:1.6rem;"></i>
                <div class="flex-grow-1" style="min-width:0;">
                    <div class="fw-semibold text-truncate" style="font-size:.9rem;"><c:out value="${pubArchivo}"/></div>
                    <div class="text-body-secondary" style="font-size:.78rem;">Documento adjunto · PDF</div>
                </div>
                <a href="<c:out value='${pub.imagenUrl}'/>" target="_blank" rel="noopener" download
                   class="text-secondary fs-5 p-1 position-relative z-1" title="Descargar" aria-label="Descargar adjunto">
                    <i class="bi bi-download"></i>
                </a>
            </div>
        </c:if>

        <%-- Pie: respuestas (dudas) y fecha límite --%>
        <c:if test="${pubHayPie}">
            <div class="d-flex justify-content-between align-items-center text-body-secondary" style="font-size:.8rem;">
                <span>
                    <c:if test="${pubEsDuda}">
                        <i class="bi bi-chat-left me-1"></i>${pub.respuestas} ${pub.respuestas == 1 ? 'respuesta' : 'respuestas'}
                    </c:if>
                </span>
                <c:if test="${not empty pubLimiteTexto}">
                    <span><i class="bi bi-calendar-event me-1"></i>Límite: <c:out value="${pubLimiteTexto}"/></span>
                </c:if>
            </div>
        </c:if>

        <%-- Botones --%>
        <c:if test="${pubHayBotones}">
            <hr class="pub-sep">
            <div class="d-flex flex-wrap align-items-center gap-2">

                <c:if test="${pubEsDuda}">
                    <a href="${pageContext.request.contextPath}/duda?id=${pub.id}"
                       class="btn btn-primary btn-sm rounded-pill fw-semibold pub-btn position-relative z-1 ${pubOcultar ? '' : 'flex-grow-1'}">
                        <i class="bi bi-chat-dots me-1"></i>${pub.estado eq 'Abierta' ? 'Ver y responder' : 'Ver duda'}
                    </a>
                </c:if>

                                <c:if test="${pubVerCurso}">
                                    <a href="${pageContext.request.contextPath}/curso?id=${pub.cursoId}"
                                       class="btn btn-sm bg-body-secondary rounded-pill border-0 fw-semibold position-relative z-1">
                                        <i class="bi bi-box-arrow-up-right me-1"></i>Ver en el curso
                                    </a>
                                </c:if>

                <c:if test="${pubEsPdf}">
                    <a href="<c:out value='${pub.imagenUrl}'/>" target="_blank" rel="noopener"
                       class="btn btn-sm rounded-pill fw-semibold pub-btn pub-btn-claro position-relative z-1">
                        <i class="bi bi-file-earmark-pdf me-1"></i>Ver PDF
                    </a>
                </c:if>

                <%-- Reportar (solo inscriptos, igual que antes) --%>
                <c:if test="${pubReportar}">
                    <button type="button"
                            class="btn btn-sm rounded-pill fw-semibold pub-btn pub-btn-reportar position-relative z-1"
                            data-bs-toggle="modal"
                            data-bs-target="#modalReportar"
                            data-publicacion-id="${pub.id}"
                            data-autor="<c:out value='${pub.autor}' />"
                            data-mensaje="<c:out value='${pub.mensaje}' />">
                        <i class="bi bi-flag me-1"></i>Reportar
                    </button>
                </c:if>

                <%-- Editar / Eliminar (solo el autor, igual que antes) --%>
                <c:if test="${pubEsMia}">
                    <div class="ms-auto d-flex align-items-center gap-3 position-relative z-1">
                        <a href="${pageContext.request.contextPath}/publicacion/editar?id=${pub.id}"
                           class="text-secondary fs-5"
                           title="Editar publicación"
                           aria-label="Editar publicación">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <button type="button"
                                class="btn p-0 border-0 bg-transparent text-danger fs-5"
                                data-bs-toggle="modal"
                                data-bs-target="#modalDesactivar${pub.id}"
                                title="Eliminar publicación"
                                aria-label="Eliminar publicación">
                            <i class="bi bi-trash3"></i>
                        </button>
                    </div>
                </c:if>
            </div>
        </c:if>
    </div>
</article>

<%-- Modal de eliminar (el mismo que tenías en curso.jsp) --%>
<c:if test="${pubEsMia}">
    <div class="modal fade" id="modalDesactivar${pub.id}" tabindex="-1"
         aria-labelledby="tituloDesactivar${pub.id}" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow">
                <div class="modal-header border-0 pb-0">
                    <div class="rounded-circle bg-danger-subtle text-danger d-flex align-items-center justify-content-center"
                         style="width: 48px; height: 48px;">
                        <i class="bi bi-exclamation-triangle fs-4"></i>
                    </div>
                    <button type="button" class="btn-close ms-auto" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body pt-3">
                    <h5 class="modal-title fw-semibold mb-2" id="tituloDesactivar${pub.id}">
                        ¿Eliminar esta publicación?
                    </h5>
                    <p class="text-muted mb-2">
                        Vas a eliminar la publicación
                        <strong><c:out value="${pub.titulo}" /></strong>.
                    </p>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Cancelar</button>
                    <form action="${pageContext.request.contextPath}/publicacion/desactivar" method="post" class="m-0">
                        <input type="hidden" name="publicacionId" value="${pub.id}">
                        <input type="hidden" name="cursoId" value="${curso.id}">
                        <button type="submit" class="btn btn-danger">
                            <i class="bi bi-trash3 me-1"></i> Sí, eliminar
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:if>