
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ocultarMenu" value="false" scope="request"/>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <div class="p-4 mb-4 bg-light rounded-3">
                <h1>Inicio</h1>
                <p class="mb-0">
                    Últimas publicaciones de tus cursos
                </p>
            </div>

            <c:choose>
                <c:when test="${empty publicaciones}">
                    <div class="alert alert-info">
                        Todavía no hay publicaciones en tus cursos
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="d-flex flex-column gap-3">
                        <c:forEach var="publicacion" items="${publicaciones}">
                            <div class="card shadow-sm">
                                <div class="card-body">

                                    <div class="mb-2">
                                        <h5 class="mb-2">
                                            <c:out value="${publicacion.nombreCurso}" />
                                        </h5>

                                        <div class="d-flex flex-wrap gap-2">
                                <span class="badge text-bg-dark">
                                    <c:out value="${publicacion.tipo}" />
                                </span>

                                            <c:if test="${not empty publicacion.categoria}">
                                    <span class="badge text-bg-info">
                                        <c:out value="${publicacion.categoria eq 'Reunion' ? 'Reunión' : publicacion.categoria}" />
                                    </span>
                                            </c:if>

                                            <c:if test="${publicacion.tipo eq 'Duda'}">
                                    <span class="badge ${publicacion.estado eq 'Abierta' ? 'text-bg-warning' : 'text-bg-success'}">
                                        <c:out value="${publicacion.estado}" />
                                    </span>
                                            </c:if>
                                        </div>
                                    </div>

                                    <p class="mb-3">
                                        <c:out value="${publicacion.mensaje}" />
                                    </p>

                                    <p class="text-muted small mb-0">
                                        Por
                                        <c:out value="${empty publicacion.autor ? 'Usuario eliminado' : publicacion.autor}" />
                                        · Fecha:
                                        <c:out value="${publicacion.fechaPublicacion}" />
                                    </p>

                                    <c:if test="${not empty publicacion.imagenUrl}">
                                        <img src="<c:out value='${publicacion.imagenUrl}' />"
                                             alt="Imagen de la publicación"
                                             class="img-fluid rounded mt-3"
                                             style="max-height: 350px;">
                                    </c:if>

                                    <div class="mt-3">
                                        <a href="${pageContext.request.contextPath}/curso?id=${publicacion.cursoId}"
                                           class="btn btn-sm btn-outline-primary">
                                            Ver curso
                                        </a>

                                        <c:if test="${publicacion.tipo eq 'Duda'}">
                                            <a href="${pageContext.request.contextPath}/duda?id=${publicacion.id}"
                                               class="btn btn-sm btn-outline-secondary">
                                                    ${publicacion.estado eq 'Abierta' ? 'Ver duda y responder' : 'Ver duda'}
                                            </a>
                                        </c:if>
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