<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/layout/header.jsp" />

<%-- Iniciales y color del avatar, calculados a partir del nombre --%>
<c:set var="partesNombre" value="${fn:split(estudiante.nombre, ' ')}" />
<c:set var="iniciales"
       value="${fn:toUpperCase(fn:substring(partesNombre[0], 0, 1))}${fn:length(partesNombre) gt 1 ? fn:toUpperCase(fn:substring(partesNombre[1], 0, 1)) : ''}" />
<c:set var="colorAvatar"
       value="${['text-bg-primary','text-bg-success','text-bg-danger','text-bg-dark','text-bg-secondary'][fn:length(estudiante.nombre) % 5]}" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">
            <div class="mx-auto d-flex flex-column gap-4" style="max-width: 960px;">

                <%-- ★ NUEVO: "Volver" como botón con recuadro, igual que en los demás --%>
                <div>
                    <a href="${pageContext.request.contextPath}/misCursos"
                       class="btn btn-outline-secondary">
                        ← Volver a mis cursos
                    </a>
                </div>

                <c:if test="${not empty mensajeExito}">
                    <div class="alert alert-success alert-dismissible fade show mb-0" role="alert">
                        <i class="bi bi-check-circle me-1"></i>
                        <c:out value="${mensajeExito}" />
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar aviso"></button>
                    </div>
                </c:if>

                <%-- Tarjeta de cabecera --%>
                <section class="card border-0 shadow-sm rounded-4">
                    <div class="card-body">
                        <div class="d-flex flex-wrap align-items-center gap-3">
                            <span class="rounded-circle d-flex align-items-center justify-content-center fw-bold flex-shrink-0 ${colorAvatar}"
                                  style="width: 72px; height: 72px; font-size: 26px;"
                                  aria-hidden="true">
                                <c:out value="${iniciales}" />
                            </span>

                            <div class="flex-grow-1" style="min-width: 220px;">
                                <h1 class="h3 fw-bold mb-0">
                                    <c:out value="${estudiante.nombre}" />
                                </h1>
                            </div>

                            <c:if test="${esPropio}">
                                <button type="button"
                                        class="btn btn-outline-dark rounded-pill d-inline-flex align-items-center gap-2 fw-semibold"
                                        data-bs-toggle="collapse"
                                        data-bs-target="#formPerfil"
                                        aria-expanded="${abrirFormulario ? 'true' : 'false'}"
                                        aria-controls="formPerfil">
                                    <i class="bi bi-pencil"></i> Editar perfil
                                </button>
                            </c:if>
                        </div>

                        <c:if test="${esPropio}">
                            <dl class="row g-3 mt-2 mb-0">
                                <div class="col-sm-6">
                                    <dt class="small text-body-secondary fw-semibold">Correo</dt>
                                    <dd class="mb-0 text-break"><c:out value="${estudiante.correo}" /></dd>
                                </div>
                                <div class="col-sm-6">
                                    <dt class="small text-body-secondary fw-semibold">Año de generación</dt>
                                    <dd class="mb-0"><c:out value="${estudiante.anioDeGeneracion}" /></dd>
                                </div>
                            </dl>
                        </c:if>

                        <c:if test="${not esPropio}">
                            <p class="small text-body-secondary mt-3 mb-0">
                                Estás viendo el perfil de otro estudiante: solo se muestran las materias
                                que tienen en común y su progreso de estudio.
                            </p>
                        </c:if>
                    </div>
                </section>

                <%-- Formulario de edición (collapse con el mismo id de siempre) --%>
                <c:if test="${esPropio}">
                    <section class="collapse ${abrirFormulario ? 'show' : ''}" id="formPerfil">
                        <div class="card border-0 shadow-sm rounded-4">
                            <div class="card-body">
                                <h2 class="h5 fw-bold mb-3">Editar perfil</h2>

                                <c:if test="${not empty errores}">
                                    <div class="alert alert-danger" role="alert">
                                        <ul class="mb-0">
                                            <c:forEach var="error" items="${errores}">
                                                <li><c:out value="${error}" /></li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>

                                <form action="${pageContext.request.contextPath}/perfil" method="post" class="row g-3">

                                    <div class="col-md-6">
                                        <label for="nombre" class="form-label fw-semibold">Nombre</label>
                                        <input type="text"
                                               id="nombre"
                                               name="nombre"
                                               class="form-control"
                                               maxlength="100"
                                               required
                                               value="<c:out value='${empty valorNombre ? estudiante.nombre : valorNombre}' />">
                                    </div>

                                    <div class="col-md-6">
                                        <label for="correo" class="form-label fw-semibold">Correo</label>
                                        <input type="email"
                                               id="correo"
                                               name="correo"
                                               class="form-control"
                                               maxlength="100"
                                               required
                                               value="<c:out value='${empty valorCorreo ? estudiante.correo : valorCorreo}' />">
                                    </div>

                                    <div class="col-md-6">
                                        <label for="anioDeGeneracion" class="form-label fw-semibold">Año de generación</label>
                                        <input type="number"
                                               id="anioDeGeneracion"
                                               name="anioDeGeneracion"
                                               class="form-control"
                                               min="2008"
                                               max="2027"
                                               required
                                               value="<c:out value='${empty valorAnio ? estudiante.anioDeGeneracion : valorAnio}' />">
                                        <div class="form-text">Entre 2008 y 2027.</div>
                                    </div>

                                    <div class="col-12"><hr class="my-1"></div>

                                    <div class="col-md-6">
                                        <label for="password" class="form-label fw-semibold">
                                            Contraseña nueva <span class="text-muted fw-normal">(opcional)</span>
                                        </label>
                                        <input type="password"
                                               id="password"
                                               name="password"
                                               class="form-control"
                                               minlength="6"
                                               maxlength="255"
                                               autocomplete="new-password"
                                               placeholder="Dejala vacía para no cambiarla">
                                        <div class="form-text">Mínimo 6 caracteres.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="password2" class="form-label fw-semibold">Repetir contraseña</label>
                                        <input type="password"
                                               id="password2"
                                               name="password2"
                                               class="form-control"
                                               minlength="6"
                                               autocomplete="new-password">
                                    </div>

                                    <div class="col-12 d-flex flex-wrap gap-2">
                                        <button type="submit" class="btn btn-dark rounded-pill px-4 fw-semibold">
                                            Guardar cambios
                                        </button>
                                        <button type="button"
                                                class="btn btn-outline-secondary rounded-pill px-4 fw-semibold"
                                                data-bs-toggle="collapse"
                                                data-bs-target="#formPerfil">
                                            Cancelar
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </section>
                </c:if>

                <div class="row g-4">

                    <%-- ===== Materias en común ===== --%>
                    <section class="col-lg-7">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body">
                                <h2 class="h5 fw-bold mb-3 d-flex align-items-center gap-2">
                                    <i class="bi bi-book"></i> Materias en común
                                </h2>

                                <c:choose>
                                    <c:when test="${empty materias}">
                                        <div class="alert alert-info mb-0">
                                            No hay materias en común para mostrar.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="table-responsive">
                                            <table class="table align-middle mb-0">
                                                <thead>
                                                    <tr class="small text-body-secondary">
                                                        <th scope="col" class="fw-semibold">Materia</th>
                                                        <th scope="col" class="fw-semibold">Año</th>
                                                        <th scope="col" class="fw-semibold">Sem.</th>
                                                        <th scope="col" class="fw-semibold">Créd.</th>
                                                        <th scope="col" class="fw-semibold">Estado</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="curso" items="${materias}">
                                                        <tr>
                                                            <th scope="row" class="fw-semibold"><c:out value="${curso.nombre}" /></th>
                                                            <td><c:out value="${curso.anio}" /></td>
                                                            <td><c:out value="${curso.semestre}" /></td>
                                                            <td><c:out value="${curso.creditos}" /></td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${curso.estadoInscripcion == 'Cursando'}">
                                                                        <span class="badge rounded-pill text-bg-primary">Cursando</span>
                                                                    </c:when>
                                                                    <c:when test="${curso.estadoInscripcion == 'Cursada'}">
                                                                        <span class="badge rounded-pill text-bg-secondary">Cursada</span>
                                                                    </c:when>
                                                                    <c:when test="${curso.estadoInscripcion == 'Aprobada'}">
                                                                        <span class="badge rounded-pill text-bg-success">Aprobada</span>
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
                    </section>

                    <%-- ===== Progreso de estudio ===== --%>
                    <section class="col-lg-5">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body">
                                <h2 class="h5 fw-bold mb-3 d-flex align-items-center gap-2">
                                    <i class="bi bi-clock"></i> Progreso de estudio
                                </h2>

                                <c:choose>
                                    <c:when test="${empty progreso}">
                                        <div class="alert alert-info mb-0">
                                            Todavía no registró horas de estudio.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <%-- La materia con más horas = barra completa; las demás, proporcionales --%>
                                        <c:set var="maxHoras" value="0" />
                                        <c:forEach var="materia" items="${progreso}">
                                            <c:if test="${materia.totalHoras gt maxHoras}">
                                                <c:set var="maxHoras" value="${materia.totalHoras}" />
                                            </c:if>
                                        </c:forEach>

                                        <ul class="list-unstyled d-flex flex-column gap-3 mb-3">
                                            <c:forEach var="materia" items="${progreso}">
                                                <c:set var="ancho"
                                                       value="${maxHoras gt 0 ? materia.totalHoras * 100 / maxHoras : 0}" />
                                                <li>
                                                    <div class="d-flex justify-content-between small mb-1">
                                                        <span class="fw-semibold"><c:out value="${materia.curso}" /></span>
                                                        <span class="text-body-secondary">
                                                            <c:out value="${materia.totalHoras}" /> h
                                                        </span>
                                                    </div>
                                                    <div class="progress" style="height: 8px;">
                                                        <div class="progress-bar"
                                                             role="progressbar"
                                                             style="width: ${ancho}%; background-color: #2f6db5;"
                                                             aria-valuenow="${ancho}"
                                                             aria-valuemin="0"
                                                             aria-valuemax="100"></div>
                                                    </div>
                                                </li>
                                            </c:forEach>
                                        </ul>

                                        <div class="d-flex justify-content-between align-items-center border-top pt-3">
                                            <span class="fw-semibold">Total de horas estudiadas</span>
                                            <span class="badge text-bg-dark rounded-pill fs-6">
                                                <c:out value="${totalHoras}" /> h
                                            </span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </section>
                </div>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />