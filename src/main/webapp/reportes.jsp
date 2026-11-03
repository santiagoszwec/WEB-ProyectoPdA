
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ocultarMenu" value="false" scope="request"/>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">

        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <div class="p-4 mb-4 bg-light rounded-4">
                <div class="d-flex align-items-center gap-3">
                    <div class="rounded-circle bg-white text-primary d-flex align-items-center justify-content-center"
                         style="width: 52px; height: 52px;">
                        <i class="bi bi-flag fs-3"></i>
                    </div>

                    <div>
                        <h1 class="h3 mb-1">Reportes pendientes</h1>
                    </div>
                </div>
            </div>

            <c:if test="${not empty mensaje}">
                <div class="alert alert-info alert-dismissible fade show rounded-3"
                     role="alert">
                    <i class="bi bi-info-circle me-2"></i>
                    <c:out value="${mensaje}"/>
                    <button type="button" class="btn-close"
                            data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:choose>

                <c:when test="${empty reportes}">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body text-center py-5">
                            <i class="bi bi-check-circle text-success"
                               style="font-size: 3rem;"></i>

                            <h2 class="h5 mt-3">No hay reportes pendientes</h2>

                            <p class="text-body-secondary mb-0">
                                Todas las denuncias se encuentran revisadas.
                            </p>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>

                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="h5 fw-semibold mb-0">Denuncias por revisar</h2>

                        <span class="badge rounded-pill text-bg-primary">
                            ${reportes.size()} pendientes
                        </span>
                    </div>

                    <div class="d-flex flex-column gap-4">

                        <c:forEach var="reporte" items="${reportes}">

                            <div class="card border-0 shadow-sm rounded-4">

                                <div class="card-header bg-transparent border-0 p-4 pb-2">

                                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">

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

                                            <span class="small text-body-secondary">
                                                <i class="bi bi-calendar3 me-1"></i>
                                                <c:out value="${reporte.fechaReporte}"/>
                                            </span>
                                        </div>

                                        <div class="mt-2 d-flex flex-column gap-1">

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

                                </div>

                                <div class="card-body px-4 pt-2">

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

                                        <p class="mb-0 text-body-secondary"
                                           style="white-space: pre-wrap; overflow-wrap: anywhere;"><c:out value="${reporte.mensajePublicacion}"/></p>

                                    </div>

                                    <c:if test="${not empty reporte.comentarioId}">

                                        <div class="border rounded-3 p-3 mb-3">

                                            <h4 class="h6 fw-semibold mb-2">
                                                <i class="bi bi-chat-left-text me-2 text-warning"></i>
                                                Comentario denunciado
                                            </h4>

                                            <p class="mb-0"
                                               style="white-space: pre-wrap; overflow-wrap: anywhere;"><c:out value="${reporte.mensajeComentario}"/></p>

                                        </div>

                                    </c:if>

                                    <div class="mb-4">

                                        <h4 class="h6 fw-semibold mb-2">
                                            <i class="bi bi-exclamation-circle me-2 text-danger"></i>
                                            Motivo de la denuncia
                                        </h4>

                                        <p class="mb-0 text-body-secondary"
                                           style="white-space: pre-wrap; overflow-wrap: anywhere;"><c:out value="${reporte.motivo}"/></p>

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

                                                <label class="form-label fw-semibold">
                                                    Motivo de la baja
                                                </label>

                                                <textarea name="motivo"
                                                          class="form-control mb-2"
                                                          rows="2"
                                                          placeholder="Explicá por qué se da de baja el contenido..."
                                                          required></textarea>

                                                <button type="submit"
                                                        class="btn btn-danger rounded-pill px-4">
                                                    <i class="bi bi-check-circle me-1"></i>
                                                    Aprobar denuncia
                                                </button>
                                            </form>
                                        </div>

                                        <div class="col-lg-5">

                                            <form action="${pageContext.request.contextPath}/reportes"
                                                  method="post">

                                                <input type="hidden"
                                                       name="reporteId"
                                                       value="${reporte.id}">

                                                <input type="hidden"
                                                       name="accion"
                                                       value="rechazar">

                                                <label class="form-label fw-semibold">
                                                    Resolución
                                                </label>

                                                <input type="text"
                                                       name="motivo"
                                                       class="form-control mb-2"
                                                       placeholder="Motivo del rechazo (opcional)">

                                                <button type="submit"
                                                        class="btn btn-outline-secondary rounded-pill px-4">
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
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />
