<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="ocultarMenu" value="false" scope="request"/>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">
            <div class="mx-auto d-flex flex-column gap-4" style="max-width: 1100px;">

                <div>
                    <a href="${pageContext.request.contextPath}/inicio"
                       class="btn btn-outline-secondary">
                        ← Volver al inicio
                    </a>
                </div>

                <h1 class="h3 fw-bold mb-0">Gestión de Reportes</h1>

                <c:if test="${not empty mensaje}">
                    <div class="alert alert-info alert-dismissible fade show mb-0"
                         role="alert">
                        <i class="bi bi-info-circle me-1"></i>
                        <c:out value="${mensaje}"/>
                        <button type="button" class="btn-close"
                                data-bs-dismiss="alert" aria-label="Cerrar"></button>
                    </div>
                </c:if>

                <section class="card border-0 shadow-sm rounded-4">
                    <div class="card-body">

                        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
                            <div>
                                <h5 class="fw-semibold mb-1">Denuncias por revisar</h5>
                                <small class="text-body-secondary">
                                    Revisión y administración de las publicaciones y comentarios denunciados.
                                </small>
                            </div>

                            <span class="badge rounded-pill text-bg-light border text-dark">
                                ${totalReportes} pendientes
                            </span>
                        </div>

                        <c:choose>

                            <c:when test="${empty reportes}">
                                <div class="alert alert-info mb-0">
                                    <i class="bi bi-check-circle me-1"></i>
                                    No hay reportes pendientes. Todas las denuncias se encuentran revisadas.
                                </div>
                            </c:when>

                            <c:otherwise>

                                <div class="d-flex flex-column gap-3">

                                    <c:forEach var="reporte" items="${reportes}">

                                        <div class="card border rounded-4 shadow-sm">

                                            <div class="card-body">

                                                <div class="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-3">

                                                    <div>
                                                        <span class="badge rounded-pill
                                                            ${not empty reporte.comentarioId ? 'text-bg-warning' : 'text-bg-primary'}">

                                                            <c:choose>
                                                                <c:when test="${not empty reporte.comentarioId}">
                                                                    <i class="bi bi-chat-left-text me-1"></i>
                                                                    Comentario
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <i class="bi bi-file-text me-1"></i>
                                                                    Publicación
                                                                </c:otherwise>
                                                            </c:choose>

                                                        </span>

                                                        <h3 class="h5 fw-semibold mt-2 mb-1">
                                                            Reporte #${reporte.id}
                                                        </h3>

                                                        <small class="text-body-secondary">
                                                            <i class="bi bi-calendar3 me-1"></i>
                                                            <c:out value="${reporte.fechaReporte}"/>
                                                        </small>
                                                    </div>

                                                    <div class="d-flex flex-column gap-1">
                                                        <span class="small">
                                                            <i class="bi bi-person-check me-1 text-primary"></i>
                                                            <strong>Reportado por:</strong>
                                                            <c:out value="${reporte.nombreReportante}"/>
                                                        </span>

                                                        <span class="small">
                                                            <i class="bi bi-person me-1 text-secondary"></i>
                                                            <strong>Autor del contenido:</strong>
                                                            <c:out value="${reporte.nombreAutor}"/>
                                                        </span>
                                                    </div>

                                                </div>

                                                <hr class="text-body-secondary">

                                                <div class="bg-light rounded-3 p-3 mb-3">

                                                    <h4 class="h6 fw-semibold mb-3">
                                                        <i class="bi bi-file-earmark-text me-2 text-primary"></i>
                                                        Publicación denunciada
                                                    </h4>

                                                    <p class="fw-semibold mb-1">
                                                        <c:choose>
                                                            <c:when test="${not empty reporte.tituloPublicacion}">
                                                                <c:out value="${reporte.tituloPublicacion}"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                Sin título
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </p>

                                                    <p class="mb-0 text-body-secondary text-start">
                                                        <c:out value="${fn:trim(reporte.mensajePublicacion)}"/>
                                                    </p>

                                                </div>

                                                <c:if test="${not empty reporte.comentarioId}">
                                                    <div class="border rounded-3 p-3 mb-3">

                                                        <h4 class="h6 fw-semibold mb-2">
                                                            <i class="bi bi-chat-left-text me-2 text-warning"></i>
                                                            Comentario denunciado
                                                        </h4>

                                                        <p class="mb-0 text-start">
                                                            <c:out value="${fn:trim(reporte.mensajeComentario)}"/>
                                                        </p>

                                                    </div>
                                                </c:if>

                                                <div class="mb-3">

                                                    <h4 class="h6 fw-semibold mb-2">
                                                        <i class="bi bi-exclamation-circle me-2 text-danger"></i>
                                                        Motivo de la denuncia
                                                    </h4>

                                                    <p class="mb-0 text-body-secondary text-start">
                                                        <c:out value="${fn:trim(reporte.motivo)}"/>
                                                    </p>

                                                </div>

                                                <hr class="text-body-secondary">

                                                <div class="row g-3">

                                                    <div class="col-lg-7">
                                                        <form action="${pageContext.request.contextPath}/reportes"
                                                              method="post">

                                                            <input type="hidden" name="reporteId"
                                                                   value="${reporte.id}">

                                                            <input type="hidden" name="accion"
                                                                   value="aprobar">

                                                            <input type="hidden" name="pagina"
                                                                   value="${paginaActual}">

                                                            <label class="form-label fw-semibold">
                                                                Motivo de la baja
                                                            </label>

                                                            <textarea name="motivo"
                                                                      class="form-control mb-2"
                                                                      rows="2"
                                                                      placeholder="Explicá por qué se da de baja el contenido..."
                                                                      required></textarea>

                                                            <button type="submit"
                                                                    class="btn btn-danger btn-sm">
                                                                <i class="bi bi-check-circle me-1"></i>
                                                                Aprobar denuncia
                                                            </button>

                                                        </form>
                                                    </div>

                                                    <div class="col-lg-5">
                                                        <form action="${pageContext.request.contextPath}/reportes"
                                                              method="post">

                                                            <input type="hidden" name="reporteId"
                                                                   value="${reporte.id}">

                                                            <input type="hidden" name="accion"
                                                                   value="rechazar">

                                                            <input type="hidden" name="pagina"
                                                                   value="${paginaActual}">

                                                            <label class="form-label fw-semibold">
                                                                Resolución
                                                            </label>

                                                            <input type="text"
                                                                   name="motivo"
                                                                   class="form-control mb-2"
                                                                   placeholder="Motivo del rechazo (opcional)">

                                                            <button type="submit"
                                                                    class="btn btn-outline-secondary btn-sm">
                                                                <i class="bi bi-x-circle me-1"></i>
                                                                Rechazar denuncia
                                                            </button>

                                                        </form>
                                                    </div>

                                                </div>

                                            </div>
                                        </div>

                                    </c:forEach>

                                </div>

                                <c:if test="${totalReportes > 0}">
                                    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mt-3 pt-3 border-top">

                                        <small class="text-body-secondary">
                                            Mostrando ${desde}–${hasta} de ${totalReportes} reportes
                                        </small>

                                        <nav aria-label="Paginación de reportes">
                                            <ul class="pagination pagination-sm mb-0">

                                                <c:url var="urlAnterior" value="/reportes">
                                                    <c:param name="pagina" value="${paginaActual - 1}"/>
                                                </c:url>

                                                <li class="page-item ${paginaActual == 1 ? 'disabled' : ''}">
                                                    <a class="page-link" href="${urlAnterior}">
                                                        <i class="bi bi-chevron-left"></i>
                                                    </a>
                                                </li>

                                                <c:forEach begin="1" end="${totalPaginas}" var="numero">

                                                    <c:url var="urlPagina" value="/reportes">
                                                        <c:param name="pagina" value="${numero}"/>
                                                    </c:url>

                                                    <li class="page-item ${paginaActual == numero ? 'active' : ''}">
                                                        <a class="page-link" href="${urlPagina}">
                                                                ${numero}
                                                        </a>
                                                    </li>

                                                </c:forEach>

                                                <c:url var="urlSiguiente" value="/reportes">
                                                    <c:param name="pagina" value="${paginaActual + 1}"/>
                                                </c:url>

                                                <li class="page-item ${paginaActual == totalPaginas ? 'disabled' : ''}">
                                                    <a class="page-link" href="${urlSiguiente}">
                                                        <i class="bi bi-chevron-right"></i>
                                                    </a>
                                                </li>

                                            </ul>
                                        </nav>

                                    </div>
                                </c:if>

                            </c:otherwise>
                        </c:choose>

                    </div>
                </section>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />