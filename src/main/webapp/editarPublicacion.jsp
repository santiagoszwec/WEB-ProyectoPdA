<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <h1 class="mb-4">Editar publicación</h1>

            <c:choose>

                <c:when test="${not empty error and empty publicacion}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${error}" />
                    </div>
                    <a href="${volverUrl}" class="btn btn-outline-secondary">Volver al curso</a>
                </c:when>

                <c:otherwise>

                    <c:if test="${not empty errores}">
                        <div class="alert alert-danger" role="alert">
                            <ul class="mb-0">
                                <c:forEach var="error" items="${errores}">
                                    <li><c:out value="${error}" /></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>

                    <div class="card shadow-sm mb-4">
                        <div class="card-body">

                            <p class="text-muted small mb-3">
                                ID: <c:out value="${publicacion.id}" />
                                · Fecha de publicación:
                                <c:out value="${publicacion.fechaPublicacion}" />
                                (no se modifica al editar)
                                <c:if test="${esMaterial}">
                                    · Tipo: Material
                                </c:if>
                            </p>

                            <form action="${pageContext.request.contextPath}/publicacion/editar"
                                  method="post" novalidate>

                                <input type="hidden" name="id" value="${publicacion.id}">

                                <div class="mb-3">
                                    <label for="mensaje" class="form-label">Mensaje</label>
                                    <textarea id="mensaje" name="mensaje" class="form-control"
                                              rows="3" maxlength="2000" required><c:out value="${publicacion.mensaje}" /></textarea>
                                </div>

                                <div class="mb-3">
                                    <label for="imagenUrl" class="form-label">
                                        Imagen (opcional)
                                    </label>
                                    <input type="text" id="imagenUrl" name="imagenUrl"
                                           class="form-control" maxlength="255"
                                           placeholder="https://ejemplo.com/imagen.png"
                                           value="<c:out value='${publicacion.imagenUrl}' />">
                                    <div class="form-text">
                                        Dejala vacía para quitar la imagen. Debe comenzar con
                                        http:// o https://.
                                    </div>
                                </div>

                                <c:choose>
                                    <c:when test="${esMaterial}">

                                        <div class="row g-3 mb-3">
                                            <div class="col-md-6">
                                                <label for="tipoMaterial" class="form-label">Tipo de material</label>
                                                <select id="tipoMaterial" name="tipoMaterial"
                                                        class="form-select" required>
                                                    <c:forEach var="opcion" items="${tiposMaterialDisponibles}">
                                                        <option value="${opcion}"
                                                                ${tipoMaterialTexto == opcion.name() ? 'selected' : ''}>
                                                            <c:out value="${opcion}" />
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>

                                            <div class="col-md-6">
                                                <label for="tipoArchivo" class="form-label">Tipo de archivo</label>
                                                <select id="tipoArchivo" name="tipoArchivo"
                                                        class="form-select" required>
                                                    <c:forEach var="opcion" items="${tiposArchivoDisponibles}">
                                                        <option value="${opcion}"
                                                                ${tipoArchivoTexto == opcion.name() ? 'selected' : ''}>
                                                            <c:out value="${opcion}" />
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="archivoUrl" class="form-label">URL del archivo</label>
                                            <input type="text" id="archivoUrl" name="archivoUrl"
                                                   class="form-control" maxlength="255" required
                                                   placeholder="https://ejemplo.com/archivo.pdf"
                                                   value="<c:out value='${archivoUrlTexto}' />">
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label">Tema</label>
                                            <input type="text" class="form-control" readonly
                                                   value="<c:out value='${material.tema}' />">
                                            <div class="form-text">
                                                El tema se define al crear el material.
                                            </div>
                                        </div>

                                    </c:when>

                                    <c:otherwise>

                                        <div class="mb-3">
                                            <label for="categoria" class="form-label">Categoría</label>
                                            <select id="categoria" name="categoria"
                                                    class="form-select" required>
                                                <c:forEach var="opcion" items="${categoriasDisponibles}">
                                                    <option value="${opcion}"
                                                            ${categoriaActualTexto == opcion.name() ? 'selected' : ''}>
                                                        <c:out value="${opcion}" />
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                    </c:otherwise>
                                </c:choose>

                                <div class="d-flex gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        Guardar cambios
                                    </button>
                                    <a href="${volverUrl}" class="btn btn-outline-secondary">
                                        Cancelar
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>

                    <c:if test="${not esMaterial and esAdmin}">
                        <p class="text-muted small">
                            Esta publicación no es material: solo se editan mensaje,
                            imagen y categoría.
                        </p>
                    </c:if>

                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />
