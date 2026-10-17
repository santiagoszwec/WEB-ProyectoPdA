<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

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
            </p>
            <hr>

            <h2 class="h4 mb-4">Publicaciones</h2>

            <c:choose>
                <c:when test="${empty publicaciones}">
                    <div class="alert alert-info">
                        Todavía no hay publicaciones en este curso
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="d-flex flex-column gap-3">
                        <c:forEach var="publicacion" items="${publicaciones}">
                            <div class="card shadow-sm">
                                <div class="card-body">
                                    <p class="mb-3">
                                        <c:out value="${publicacion.mensaje}" />
                                    </p>
                                    <p class="text-muted small mb-0">
                                        Fecha:
                                        <c:out value="${publicacion.fechaPublicacion}" />
                                    </p>
                                    <c:if test="${not empty publicacion.imagenUrl}">
                                        <img src="<c:out value='${publicacion.imagenUrl}' />"
                                             alt="Imagen de la publicación"
                                             class="img-fluid rounded mt-3"
                                             style="max-height: 350px;">
                                    </c:if>
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