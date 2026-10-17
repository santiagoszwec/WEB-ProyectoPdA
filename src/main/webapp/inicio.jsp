
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
                    <c:forEach var="publicacion" items="${publicaciones}">
                        <div class="card mb-3">
                            <div class="card-body">
                                <p class="text-muted mb-2">
                                        ${publicacion.fechaPublicacion}
                                </p>

                                <p class="card-text">
                                    <c:out value="${publicacion.mensaje}" />
                                </p>

                                <c:if test="${not empty publicacion.imagenUrl}">
                                    <img src="<c:out value='${publicacion.imagenUrl}'/>"
                                            class="img-fluid rounded"
                                            alt="Imagen de la publicación">
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />