<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

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
                            <span class="badge text-bg-info"><c:out value="${duda.categoria}" /></span>
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
                    <div class="card mb-3 ${respuesta.destacado ? 'border-success' : ''}">
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
                                <c:when test="${not empty respuesta.imagenUrl}">
                                    <img src="<c:out value='${respuesta.imagenUrl}' />" alt="Imagen adjunta"
                                         class="img-fluid rounded" style="max-height: 300px;" loading="lazy">
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">Sin imagen</span>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${not empty respuesta.comentarios}">
                                <div class="mt-3 ms-4 ps-3 border-start">
                                    <c:forEach var="comentario" items="${respuesta.comentarios}">
                                        <div class="mb-3">
                                            <div class="small">
                                                <strong><c:out value="${comentario.autorNombre}" /></strong>
                                                <span class="text-muted"> · <c:out value="${comentario.fechaPublicacion}" /></span>
                                            </div>
                                            <p class="mb-1"><c:out value="${comentario.mensaje}" /></p>
                                            <c:if test="${not empty comentario.imagenUrl}">
                                                <img src="<c:out value='${comentario.imagenUrl}' />" alt="Imagen adjunta"
                                                     class="img-fluid rounded" style="max-height: 200px;" loading="lazy">
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>

            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />