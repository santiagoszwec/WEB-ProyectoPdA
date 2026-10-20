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

            <c:if test="${not empty mensajeExito}">
                            <div class="alert alert-success" role="alert">
                                <c:out value="${mensajeExito}" />
                            </div>
            </c:if>

            <c:choose>
                            <c:when test="${puedePublicar}">
                                <div class="card shadow-sm mb-4">
                                    <div class="card-body">
                                        <h2 class="h5 mb-3">Nueva publicación</h2>

                                        <%-- Errores de validación que manda el servlet --%>
                                        <c:if test="${not empty errores}">
                                            <div class="alert alert-danger" role="alert">
                                                <ul class="mb-0">
                                                    <c:forEach var="error" items="${errores}">
                                                        <li><c:out value="${error}" /></li>
                                                    </c:forEach>
                                                </ul>
                                            </div>
                                        </c:if>

                                        <form action="${pageContext.request.contextPath}/curso?id=${curso.id}"
                                              method="post" novalidate>


                                            <div class="mb-3">
                                                <label for="mensaje" class="form-label">Mensaje</label>
                                                <textarea id="mensaje" name="mensaje" class="form-control"
                                                          rows="3" maxlength="2000"><c:out value="${valorMensaje}" /></textarea>
                                            </div>


                                            <div class="mb-3">
                                                <div class="form-check">
                                                    <input class="form-check-input" type="checkbox"
                                                           id="adjuntarImagen" name="adjuntarImagen"
                                                           ${valorAdjuntar ? 'checked' : ''}>
                                                    <label class="form-check-label" for="adjuntarImagen">
                                                        Adjuntar una imagen
                                                    </label>
                                                </div>
                                                <div id="grupoImagenUrl" class="mt-2">
                                                    <label for="imagenUrl" class="form-label">URL de la imagen</label>
                                                    <input type="text" id="imagenUrl" name="imagenUrl"
                                                           class="form-control"
                                                           placeholder="https://ejemplo.com/imagen.png"
                                                           value="<c:out value='${valorImagenUrl}' />">
                                                </div>
                                            </div>

                                            <div class="row g-3 mb-3">
                                                <div class="col-md-6">
                                                    <label for="tipo" class="form-label">Tipo</label>
                                                    <select id="tipo" name="tipo" class="form-select">
                                                        <option value="">Seleccioná un tipo</option>
                                                        <option value="Duda" ${valorTipo == 'Duda' ? 'selected' : ''}>Duda</option>
                                                        <option value="Mensaje" ${valorTipo == 'Mensaje' ? 'selected' : ''}>Mensaje</option>
                                                    </select>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="categoria" class="form-label">Categoría</label>
                                                    <select id="categoria" name="categoria" class="form-select">
                                                        <option value="">Seleccioná una categoría</option>
                                                        <option value="Ejercicio" ${valorCategoria == 'Ejercicio' ? 'selected' : ''}>Ejercicio</option>
                                                        <option value="Examen" ${valorCategoria == 'Examen' ? 'selected' : ''}>Examen</option>
                                                        <option value="Reunion" ${valorCategoria == 'Reunion' ? 'selected' : ''}>Reunión</option>
                                                    </select>
                                                </div>
                                            </div>

                                            <button type="submit" class="btn btn-primary">Publicar</button>
                                        </form>
                                    </div>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <div class="alert alert-secondary">
                                    Este curso no corresponde al año actual: se muestra solo para consulta.
                                </div>
                            </c:otherwise>
                        </c:choose>

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

                                    <div class="d-flex flex-wrap gap-2 mb-2">
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

                                    <p class="mb-3">
                                        <c:out value="${publicacion.mensaje}" />
                                    </p>

                                     <p class="text-muted small mb-0">
                                        Por <c:out value="${empty publicacion.autor ? 'Usuario eliminado' : publicacion.autor}" />
                                        · Fecha: <c:out value="${publicacion.fechaPublicacion}" />
                                     </p>


                                    <c:if test="${not empty publicacion.imagenUrl}">
                                        <img src="<c:out value='${publicacion.imagenUrl}' />"
                                             alt="Imagen de la publicación"
                                             class="img-fluid rounded mt-3"
                                             style="max-height: 350px;">
                                    </c:if>
                                    <c:if test="${publicacion.tipo eq 'Duda'}">
                                        <div class="mt-3">
                                            <a href="${pageContext.request.contextPath}/duda?id=${publicacion.id}"
                                               class="btn btn-sm btn-outline-primary">
                                                ${publicacion.estado eq 'Abierta' ? 'Ver duda y responder' : 'Ver duda'}
                                            </a>
                                        </div>
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

<script>
    (function () {
        var casilla = document.getElementById("adjuntarImagen");
        var grupo = document.getElementById("grupoImagenUrl");
        if (!casilla || !grupo) return;
        function actualizar() { grupo.style.display = casilla.checked ? "block" : "none"; }
        casilla.addEventListener("change", actualizar);
        actualizar();
    })();
</script>

<jsp:include page="/layout/footer.jsp" />