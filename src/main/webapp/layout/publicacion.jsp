<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    Tarjeta de publicación (estilo Reddit, solo Bootstrap). Se usa en toda la web.
    Uso:  <c:set var="pub" value="${publicacion}" scope="request"/>
          <jsp:include page="/layout/publicacion.jsp"/>
    Opcional (dentro de un curso, para no repetir el nombre del curso):
          <c:set var="pubOcultarCurso" value="true" scope="request"/>
--%>
<c:set var="pubEsDuda" value="${pub.tipo eq 'Duda'}"/>
<c:set var="pubAutor" value="${empty pub.autor ? 'Usuario eliminado' : pub.autor}"/>
<c:set var="pubOcultar" value="${pubOcultarCurso eq 'true'}"/>

<%-- Texto que identifica la tarjeta: el curso (o el autor si estamos dentro del curso) --%>
<c:set var="pubNombre" value="${pubOcultar ? pubAutor : pub.nombreCurso}"/>
<c:set var="pubPartes" value="${fn:split(pubNombre, ' ')}"/>
<c:set var="pubIniciales"
       value="${fn:toUpperCase(fn:substring(pubPartes[0], 0, 1))}${fn:length(pubPartes) > 1 ? fn:toUpperCase(fn:substring(pubPartes[fn:length(pubPartes) - 1], 0, 1)) : ''}"/>
<c:set var="pubColores" value="${['text-bg-primary', 'text-bg-success', 'text-bg-danger', 'text-bg-dark', 'text-bg-secondary']}"/>
<c:set var="pubColor" value="${pubColores[(pubOcultar ? fn:length(pubAutor) : pub.cursoId) % 5]}"/>

<c:set var="pubDestino"
       value="${pageContext.request.contextPath}${pubEsDuda ? '/duda?id=' : '/curso?id='}${pubEsDuda ? pub.id : pub.cursoId}"/>

<article class="card pub-reddit position-relative mb-3" id="publicacion-${pub.id}">
    <a href="${pubDestino}" class="stretched-link"
       aria-label="Abrir la publicación <c:out value='${pub.titulo}'/>"></a>

    <div class="card-body d-flex flex-column gap-2">

        <%-- Cabecera: avatar, curso/autor, fecha y menú --%>
        <div class="d-flex align-items-center gap-2">
            <span class="rounded-circle d-flex align-items-center justify-content-center fw-bold small flex-shrink-0 ${pubColor}"
                  style="width:32px;height:32px;"><c:out value="${pubIniciales}"/></span>

            <div class="flex-grow-1" style="min-width:0;">
                <div class="small">
                    <span class="fw-bold"><c:out value="${pubNombre}"/></span>
                    <span class="text-body-secondary">• <c:out value="${pub.fechaPublicacion}"/></span>
                </div>
                <c:if test="${not pubOcultar}">
                    <div class="small text-body-secondary"><c:out value="${pubAutor}"/></div>
                </c:if>
            </div>

            <div class="dropdown position-relative z-1">
                <button type="button" class="btn btn-sm btn-link text-secondary rounded-circle p-1"
                        data-bs-toggle="dropdown" aria-expanded="false" aria-label="Más opciones">
                    <i class="bi bi-three-dots fs-5"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow">
                    <c:if test="${not pubOcultar}">
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/curso?id=${pub.cursoId}">
                                <i class="bi bi-box-arrow-up-right me-2"></i>Ver en el curso
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${pubEsDuda}">
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/duda?id=${pub.id}">
                                <i class="bi bi-chat-left-text me-2"></i>${pub.estado eq 'Abierta' ? 'Ver duda y responder' : 'Ver duda'}
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>

        <%-- Título --%>
        <c:if test="${not empty pub.titulo}">
            <h3 class="h6 fw-semibold mb-0"><c:out value="${pub.titulo}"/></h3>
        </c:if>

        <%-- Etiquetas --%>
        <div class="d-flex flex-wrap gap-1">
            <span class="badge rounded-pill ${pub.tipo eq 'Duda' ? 'bg-warning-subtle text-warning-emphasis' : (pub.tipo eq 'Material' ? 'bg-success-subtle text-success-emphasis' : 'bg-primary-subtle text-primary-emphasis')}">
                <c:out value="${pub.tipo}"/>
            </span>
            <c:if test="${not empty pub.categoria}">
                <span class="badge rounded-pill bg-secondary-subtle text-secondary-emphasis">
                    <c:out value="${pub.categoria eq 'Reunion' ? 'Reunión' : pub.categoria}"/>
                </span>
            </c:if>
            <c:if test="${pubEsDuda}">
                <span class="badge rounded-pill ${pub.estado eq 'Abierta' ? 'bg-warning-subtle text-warning-emphasis' : 'bg-success-subtle text-success-emphasis'}">
                    <c:out value="${pub.estado}"/>
                </span>
            </c:if>
            <c:if test="${not empty pub.fechaLimite}">
                <span class="badge rounded-pill bg-danger-subtle text-danger-emphasis">
                    <i class="bi bi-calendar-event"></i> Límite: <c:out value="${pub.fechaLimite}"/>
                </span>
            </c:if>
        </div>

        <%-- Mensaje --%>
        <p class="small mb-0" style="line-height:1.6; white-space:pre-line;"><c:out value="${pub.mensaje}"/></p>

        <%-- Imagen o PDF --%>
        <c:choose>
            <c:when test="${fn:endsWith(fn:toLowerCase(pub.imagenUrl), '.pdf')}">
                <div>
                    <a href="<c:out value='${pub.imagenUrl}'/>" target="_blank" rel="noopener"
                       class="btn btn-sm bg-body-secondary rounded-pill border-0 fw-semibold position-relative z-1">
                        <i class="bi bi-file-earmark-pdf me-1"></i>Ver PDF
                    </a>
                </div>
            </c:when>
            <c:when test="${not empty pub.imagenUrl}">
                <img src="<c:out value='${pub.imagenUrl}'/>" alt="Imagen de la publicación"
                     class="img-fluid rounded-3 border align-self-start" style="max-height:350px;">
            </c:when>
        </c:choose>

        <%-- Acciones --%>
        <div class="d-flex flex-wrap gap-2">
            <c:if test="${pubEsDuda}">
                <a href="${pageContext.request.contextPath}/duda?id=${pub.id}"
                   class="btn btn-sm bg-body-secondary rounded-pill border-0 fw-semibold position-relative z-1">
                    <i class="bi bi-chat-left-text me-1"></i>${pub.estado eq 'Abierta' ? 'Ver duda y responder' : 'Ver duda'}
                </a>
            </c:if>
            <c:if test="${not pubOcultar}">
                <a href="${pageContext.request.contextPath}/curso?id=${pub.cursoId}"
                   class="btn btn-sm bg-body-secondary rounded-pill border-0 fw-semibold position-relative z-1">
                    <i class="bi bi-box-arrow-up-right me-1"></i>Ver en el curso
                </a>
            </c:if>
        </div>
    </div>
</article>