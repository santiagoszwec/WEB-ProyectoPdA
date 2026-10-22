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

            <h1 class="mb-1">
                <c:out value="${estudiante.nombre}" />
            </h1>
            <p class="text-muted mb-4">
                Perfil público
                <c:if test="${esPropio}">· (este sos vos)</c:if>
            </p>

            <c:if test="${not empty mensajeExito}">
                <div class="alert alert-success" role="alert">
                    <c:out value="${mensajeExito}" />
                </div>
            </c:if>

            <c:if test="${esPropio}">
                <div class="card shadow-sm mb-4">
                    <div class="card-body">

                        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
                            <h2 class="h5 mb-0">Mis datos</h2>
                            <button type="button"
                                    class="btn btn-outline-primary"
                                    data-bs-toggle="collapse"
                                    data-bs-target="#formPerfil"
                                    aria-expanded="${abrirFormulario ? 'true' : 'false'}"
                                    aria-controls="formPerfil">
                                Editar perfil
                            </button>
                        </div>

                        <dl class="row mb-0">
                            <dt class="col-sm-3">Correo</dt>
                            <dd class="col-sm-9"><c:out value="${estudiante.correo}" /></dd>

                            <dt class="col-sm-3">Año de generación</dt>
                            <dd class="col-sm-9 mb-0"><c:out value="${estudiante.anioDeGeneracion}" /></dd>
                        </dl>

                        <div class="collapse ${abrirFormulario ? 'show' : ''}" id="formPerfil">
                            <hr>

                            <c:if test="${not empty errores}">
                                <div class="alert alert-danger" role="alert">
                                    <ul class="mb-0">
                                        <c:forEach var="error" items="${errores}">
                                            <li><c:out value="${error}" /></li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/perfil" method="post">

                                <div class="mb-3">
                                    <label for="nombre" class="form-label">Nombre</label>
                                    <input type="text"
                                           id="nombre"
                                           name="nombre"
                                           class="form-control"
                                           maxlength="100"
                                           required
                                           value="<c:out value='${empty valorNombre ? estudiante.nombre : valorNombre}' />">
                                </div>

                                <div class="mb-3">
                                    <label for="correo" class="form-label">Correo</label>
                                    <input type="email"
                                           id="correo"
                                           name="correo"
                                           class="form-control"
                                           maxlength="100"
                                           required
                                           value="<c:out value='${empty valorCorreo ? estudiante.correo : valorCorreo}' />">
                                </div>

                                <div class="mb-3">
                                    <label for="anioDeGeneracion" class="form-label">Año de generación</label>
                                    <input type="number"
                                           id="anioDeGeneracion"
                                           name="anioDeGeneracion"
                                           class="form-control"
                                           min="2008"
                                           max="2027"
                                           required
                                           value="<c:out value='${empty valorAnio ? estudiante.anioDeGeneracion : valorAnio}' />">
                                </div>

                                <div class="row g-3 mb-3">
                                    <div class="col-md-6">
                                        <label for="password" class="form-label">
                                            Contraseña nueva <span class="text-muted">(opcional)</span>
                                        </label>
                                        <input type="password"
                                               id="password"
                                               name="password"
                                               class="form-control"
                                               minlength="6"
                                               maxlength="255"
                                               autocomplete="new-password"
                                               placeholder="Dejala vacía para no cambiarla">
                                    </div>

                                    <div class="col-md-6">
                                        <label for="password2" class="form-label">Repetir contraseña</label>
                                        <input type="password"
                                               id="password2"
                                               name="password2"
                                               class="form-control"
                                               minlength="6"
                                               autocomplete="new-password">
                                    </div>
                                </div>

                                <div class="d-flex gap-2">
                                    <button type="submit" class="btn btn-primary">Guardar cambios</button>

                                    <button type="button"
                                            class="btn btn-outline-secondary"
                                            data-bs-toggle="collapse"
                                            data-bs-target="#formPerfil">
                                        Cancelar
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="row g-4 mb-4">
                <div class="col-lg-6">
                    <div class="card shadow-sm h-100">
                        <div class="card-body">
                            <h2 class="h5 mb-3">Materias en común</h2>

                            <c:choose>
                                <c:when test="${empty materias}">
                                    <div class="alert alert-info mb-0">
                                        No hay materias en común para mostrar.
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-sm align-middle mb-0">
                                            <thead>
                                                <tr>
                                                    <th scope="col">Materia</th>
                                                    <th scope="col">Año</th>
                                                    <th scope="col">Semestre</th>
                                                    <th scope="col">Créditos</th>
                                                    <th scope="col">Estado</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="curso" items="${materias}">
                                                    <tr>
                                                        <td><c:out value="${curso.nombre}" /></td>
                                                        <td><c:out value="${curso.anio}" /></td>
                                                        <td><c:out value="${curso.semestre}" /></td>
                                                        <td><c:out value="${curso.creditos}" /></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${curso.estadoInscripcion == 'Cursando'}">
                                                                    <span class="badge text-bg-primary">Cursando</span>
                                                                </c:when>
                                                                <c:when test="${curso.estadoInscripcion == 'Cursada'}">
                                                                    <span class="badge text-bg-secondary">Cursada</span>
                                                                </c:when>
                                                                <c:when test="${curso.estadoInscripcion == 'Aprobada'}">
                                                                    <span class="badge text-bg-success">Aprobada</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:out value="${curso.estadoInscripcion}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <div class="col-lg-6">
                    <div class="card shadow-sm h-100">
                        <div class="card-body">
                            <h2 class="h5 mb-3">Progreso de estudio</h2>

                            <c:choose>
                                <c:when test="${empty progreso}">
                                    <div class="alert alert-info mb-0">
                                        Todavía no registró horas de estudio.
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-sm align-middle mb-0">
                                            <thead>
                                                <tr>
                                                    <th scope="col">Materia</th>
                                                    <th scope="col" class="text-end">Horas</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="materia" items="${progreso}">
                                                    <tr>
                                                        <td><c:out value="${materia.curso}" /></td>
                                                        <td class="text-end">
                                                            <c:out value="${materia.totalHoras}" />
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                            <tfoot>
                                                <tr class="table-light fw-semibold">
                                                    <td>Total de horas estudiadas</td>
                                                    <td class="text-end">
                                                        <c:out value="${totalHoras}" />
                                                    </td>
                                                </tr>
                                            </tfoot>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />
