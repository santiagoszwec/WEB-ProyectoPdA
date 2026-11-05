<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">
            <div class="mx-auto d-flex flex-column gap-4" style="max-width: 1100px;">

                <!-- Volver al inicio -->
                <div>
                    <a href="${pageContext.request.contextPath}/inicio"
                       class="btn btn-outline-secondary">
                        ← Volver al inicio
                    </a>
                </div>

                <!-- Título -->
                <h1 class="h3 fw-bold mb-0">Gestión de Publicaciones</h1>

                <!-- Mensajes -->
                <c:if test="${not empty mensajeExito}">
                    <div class="alert alert-success alert-dismissible fade show mb-0" role="alert">
                        <i class="bi bi-check-circle me-1"></i>
                        <c:out value="${mensajeExito}" />
                        <button type="button" class="btn-close"
                                data-bs-dismiss="alert" aria-label="Cerrar"></button>
                    </div>
                </c:if>

                <!-- Filtros -->
                <section class="card border-0 shadow-sm rounded-4">
                    <div class="card-body">
                        <div class="mb-3">
                            <h5 class="fw-semibold mb-1">Filtros de búsqueda</h5>
                            <small class="text-body-secondary">
                                Buscá publicaciones por mensaje, autor, curso, estado o tipo.
                            </small>
                        </div>

                        <form action="${pageContext.request.contextPath}/gestionPublicaciones"
                              method="get">

                            <div class="row g-3 align-items-end">

                                <div class="col-md-5">
                                    <label for="busqueda" class="form-label fw-semibold">
                                        Buscar publicaciones
                                    </label>
                                    <input type="text"
                                           class="form-control"
                                           id="busqueda"
                                           name="busqueda"
                                           placeholder="Mensaje, autor o curso..."
                                           value="<c:out value='${busqueda}'/>">
                                </div>

                                <div class="col-md-3">
                                    <label for="estado" class="form-label fw-semibold">
                                        Estado
                                    </label>
                                    <select class="form-select" id="estado" name="estado">
                                        <option value=""
                                        ${estadoSeleccionado == '' ? 'selected' : ''}>
                                            Todos los estados
                                        </option>
                                        <option value="activas"
                                        ${estadoSeleccionado == 'activas' ? 'selected' : ''}>
                                            Activas
                                        </option>
                                        <option value="inactivas"
                                        ${estadoSeleccionado == 'inactivas' ? 'selected' : ''}>
                                            Inactivas
                                        </option>
                                    </select>
                                </div>

                                <div class="col-md-2">
                                    <label for="tipo" class="form-label fw-semibold">
                                        Tipo
                                    </label>
                                    <select class="form-select" id="tipo" name="tipo">
                                        <option value=""
                                        ${tipoSeleccionado == '' ? 'selected' : ''}>
                                            Todos
                                        </option>
                                        <option value="Mensaje"
                                        ${tipoSeleccionado == 'Mensaje' ? 'selected' : ''}>
                                            Mensaje
                                        </option>
                                        <option value="Duda"
                                        ${tipoSeleccionado == 'Duda' ? 'selected' : ''}>
                                            Duda
                                        </option>
                                        <option value="Material"
                                        ${tipoSeleccionado == 'Material' ? 'selected' : ''}>
                                            Material
                                        </option>
                                    </select>
                                </div>

                                <div class="col-md-2 d-flex gap-2">
                                    <button type="submit"
                                            class="btn btn-primary flex-grow-1">
                                        <i class="bi bi-search"></i>
                                        Filtrar
                                    </button>

                                    <a href="${pageContext.request.contextPath}/gestionPublicaciones"
                                       class="btn btn-outline-secondary"
                                       title="Limpiar filtros">
                                        <i class="bi bi-x-lg"></i>
                                    </a>
                                </div>

                            </div>
                        </form>
                    </div>
                </section>

                <!-- Listado de publicaciones -->
                <section class="card border-0 shadow-sm rounded-4">
                    <div class="card-body">

                        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
                            <div>
                                <h5 class="fw-semibold mb-1">Publicaciones registradas</h5>
                                <small class="text-body-secondary">
                                    Administración de las publicaciones del sistema.
                                </small>
                            </div>

                            <span class="badge rounded-pill text-bg-light border text-dark">
                                ${totalPublicaciones} publicaciones
                            </span>
                        </div>

                        <c:if test="${empty publicaciones}">
                            <div class="alert alert-info mb-0">
                                No se encontraron publicaciones con los criterios seleccionados.
                            </div>
                        </c:if>

                        <c:if test="${not empty publicaciones}">
                            <div class="table-responsive">
                                <table class="table align-middle mb-0">

                                    <thead class="table-light">
                                    <tr class="small text-body-secondary">
                                        <th scope="col" class="fw-semibold">ID</th>
                                        <th scope="col" class="fw-semibold">Tipo</th>
                                        <th scope="col" class="fw-semibold">Curso</th>
                                        <th scope="col" class="fw-semibold">Autor</th>
                                        <th scope="col" class="fw-semibold">Mensaje</th>
                                        <th scope="col" class="fw-semibold">Fecha</th>
                                        <th scope="col" class="fw-semibold text-center">Estado</th>
                                        <th scope="col" class="fw-semibold text-center">Acción</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    <c:forEach var="publicacion" items="${publicaciones}">
                                        <tr>
                                            <td>
                                                <c:out value="${publicacion.id}" />
                                            </td>

                                            <td>
                                                <span class="badge text-bg-dark">
                                                    <c:out value="${publicacion.tipo}" />
                                                </span>
                                            </td>

                                            <td>
                                                <c:out value="${publicacion.curso}" />
                                            </td>

                                            <td>
                                                <c:out value="${empty publicacion.autor ? 'Usuario eliminado' : publicacion.autor}" />
                                            </td>

                                            <td style="max-width: 320px; overflow-wrap: anywhere;">
                                                <c:out value="${publicacion.mensaje}" />
                                            </td>

                                            <td>
                                                <c:out value="${publicacion.fechaPublicacion}" />
                                            </td>

                                            <td class="text-center small">
                                                <c:choose>
                                                    <c:when test="${publicacion.activa}">
                                                        <span class="badge text-bg-success">Activa</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge text-bg-danger">Dada de baja</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>

                                            <td class="text-center">
                                                <form action="${pageContext.request.contextPath}/gestionPublicaciones"
                                                      method="post"
                                                      onsubmit="return confirm('¿Estás seguro de que querés eliminar esta publicación?');">

                                                    <input type="hidden" name="publicacionId"
                                                           value="${publicacion.id}">

                                                    <input type="hidden" name="busqueda"
                                                           value="<c:out value='${busqueda}'/>">

                                                    <input type="hidden" name="estado"
                                                           value="${estadoSeleccionado}">

                                                    <input type="hidden" name="tipo"
                                                           value="${tipoSeleccionado}">

                                                    <button type="submit"
                                                            class="btn btn-outline-danger btn-sm"
                                                        ${not publicacion.activa ? 'disabled' : ''}>
                                                        Eliminar
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>

                                </table>
                            </div>

                            <!-- Paginación -->
                            <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mt-3 pt-3 border-top">

                                <small class="text-body-secondary">
                                    Mostrando ${desde}–${hasta} de ${totalPublicaciones} publicaciones
                                </small>

                                <nav aria-label="Paginación de publicaciones">
                                    <ul class="pagination pagination-sm mb-0">

                                        <c:url var="urlAnterior" value="/gestionPublicaciones">
                                            <c:param name="pagina" value="${paginaActual - 1}" />
                                            <c:param name="busqueda" value="${busqueda}" />
                                            <c:param name="estado" value="${estadoSeleccionado}" />
                                            <c:param name="tipo" value="${tipoSeleccionado}" />
                                        </c:url>

                                        <li class="page-item ${paginaActual == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="${urlAnterior}">
                                                <i class="bi bi-chevron-left"></i>
                                            </a>
                                        </li>

                                        <c:forEach begin="1" end="${totalPaginas}" var="numero">
                                            <c:url var="urlPagina" value="/gestionPublicaciones">
                                                <c:param name="pagina" value="${numero}" />
                                                <c:param name="busqueda" value="${busqueda}" />
                                                <c:param name="estado" value="${estadoSeleccionado}" />
                                                <c:param name="tipo" value="${tipoSeleccionado}" />
                                            </c:url>

                                            <li class="page-item ${paginaActual == numero ? 'active' : ''}">
                                                <a class="page-link" href="${urlPagina}">
                                                        ${numero}
                                                </a>
                                            </li>
                                        </c:forEach>

                                        <c:url var="urlSiguiente" value="/gestionPublicaciones">
                                            <c:param name="pagina" value="${paginaActual + 1}" />
                                            <c:param name="busqueda" value="${busqueda}" />
                                            <c:param name="estado" value="${estadoSeleccionado}" />
                                            <c:param name="tipo" value="${tipoSeleccionado}" />
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

                    </div>
                </section>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />