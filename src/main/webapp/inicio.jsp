<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="ocultarMenu" value="false" scope="request"/>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">
            <div class="mx-auto d-flex flex-column gap-4" style="max-width: 1100px;">

                <%-- Saludo --%>
                <c:set var="saludoNombre" value="${fn:split(sessionScope.usuarioLogueado.nombre, ' ')[0]}"/>
                <div class="d-flex flex-wrap justify-content-between align-items-start gap-2">
                    <div>
                        <h1 class="h3 fw-bold mb-1">¡Hola, <c:out value="${saludoNombre}"/>! 👋</h1>
                        <p class="text-body-secondary mb-0">Este es tu resumen académico</p>
                    </div>
                    <span class="badge rounded-pill bg-success-subtle text-success-emphasis fs-6 fw-normal px-3 py-2">
                        ${empty totalCursando ? 0 : totalCursando} cursos activos
                    </span>
                </div>

                <%-- Resultados del buscador de materias (barra azul) --%>
                <c:if test="${not empty materiaBuscada}">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body">
                            <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
                                <h2 class="h5 fw-semibold mb-0">
                                    Materias que coinciden con “<c:out value="${materiaBuscada}"/>”
                                </h2>
                                <a href="${pageContext.request.contextPath}/inicio" class="btn btn-outline-secondary btn-sm">
                                    Limpiar búsqueda
                                </a>
                            </div>
                            <c:choose>
                                <c:when test="${empty cursosEncontrados}">
                                    <p class="text-body-secondary mb-0">
                                        No estás inscripto en ninguna materia con ese nombre.
                                        <a href="${pageContext.request.contextPath}/inscripciones">Ver cursos para inscribirme</a>
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <div class="row g-3">
                                        <c:forEach var="curso" items="${cursosEncontrados}">
                                            <div class="col-md-6 col-xl-4">
                                                <a href="${pageContext.request.contextPath}/curso?id=${curso.id}"
                                                   class="card h-100 text-decoration-none text-body border rounded-4">
                                                    <div class="card-body">
                                                        <div class="fw-semibold"><c:out value="${curso.nombre}"/></div>
                                                        <div class="small text-body-secondary">
                                                            Año <c:out value="${curso.anio}"/> · Semestre <c:out value="${curso.semestre}"/>
                                                        </div>
                                                        <span class="badge rounded-pill bg-success-subtle text-success-emphasis mt-2">
                                                            <c:out value="${curso.estadoInscripcion}"/>
                                                        </span>
                                                    </div>
                                                </a>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>

                <%-- Indicadores --%>
                <div class="row g-3">
                    <div class="col-md-4">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body">
                                <i class="bi bi-clock fs-5 text-primary" aria-hidden="true"></i>
                                <div class="fs-3 fw-bold mt-2">
                                    <fmt:formatNumber value="${empty estudio ? 0 : estudio.totalHoras}" maxFractionDigits="1"/> h
                                </div>
                                <div class="small text-body-secondary">Horas estudiadas</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body">
                                <i class="bi bi-book fs-5 text-success" aria-hidden="true"></i>
                                <div class="fs-3 fw-bold mt-2">${empty totalCursando ? 0 : totalCursando}</div>
                                <div class="small text-body-secondary">Cursos activos</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body">
                                <i class="bi bi-trophy fs-5 text-warning-emphasis" aria-hidden="true"></i>
                                <c:choose>
                                    <c:when test="${not empty estudio.masEstudiada}">
                                        <div class="fs-3 fw-bold mt-2">
                                            <fmt:formatNumber value="${estudio.masEstudiada.horas}" maxFractionDigits="1"/> h
                                        </div>
                                        <div class="small text-body-secondary text-truncate">
                                            Más estudiada: <c:out value="${estudio.masEstudiada.nombre}"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="fs-3 fw-bold mt-2">—</div>
                                        <div class="small text-body-secondary">Materia más estudiada</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row g-3">
                    <%-- Mi estudio: horas por materia --%>
                    <div class="col-lg-6">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body d-flex flex-column">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h2 class="h5 fw-semibold mb-0">Mi estudio</h2>
                                    <i class="bi bi-bar-chart-fill text-body-secondary" aria-hidden="true"></i>
                                </div>

                                <c:choose>
                                    <c:when test="${empty estudio.materias}">
                                        <p class="text-body-secondary mb-3">Todavía no registraste horas de estudio.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="materia" items="${estudio.materias}">
                                            <div class="mb-3">
                                                <div class="d-flex justify-content-between small mb-1">
                                                    <span class="text-truncate me-2"><c:out value="${materia.nombre}"/></span>
                                                    <span class="fw-semibold">
                                                        <fmt:formatNumber value="${materia.horas}" maxFractionDigits="1"/> h
                                                    </span>
                                                </div>
                                                <div class="progress" role="progressbar" style="height: 6px;"
                                                     aria-label="Horas en ${fn:escapeXml(materia.nombre)}"
                                                     aria-valuenow="${materia.porcentaje}" aria-valuemin="0" aria-valuemax="100">
                                                    <div class="progress-bar" style="width: ${materia.porcentaje}%"></div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>

                                <a href="${pageContext.request.contextPath}/misHoras"
                                   class="btn btn-outline-secondary rounded-pill w-100 mt-auto">
                                    <i class="bi bi-plus-lg me-1"></i>Registrar horas
                                </a>
                            </div>
                        </div>
                    </div>

                    <%-- Mis cursos --%>
                    <div class="col-lg-6">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body d-flex flex-column">
                                <h2 class="h5 fw-semibold mb-3">Mis cursos</h2>

                                <c:choose>
                                    <c:when test="${empty cursosCursando}">
                                        <p class="text-body-secondary mb-3">Todavía no estás cursando ningún curso.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="list-group list-group-flush mb-2">
                                            <c:forEach var="curso" items="${cursosCursando}">
                                                <c:set var="cPartes" value="${fn:split(curso.nombre, ' ')}"/>
                                                <c:set var="cIniciales"
                                                       value="${fn:toUpperCase(fn:substring(cPartes[0], 0, 1))}${fn:length(cPartes) > 1 ? fn:toUpperCase(fn:substring(cPartes[fn:length(cPartes) - 1], 0, 1)) : ''}"/>
                                                <a href="${pageContext.request.contextPath}/curso?id=${curso.id}"
                                                   class="list-group-item list-group-item-action d-flex align-items-center gap-3 px-0 border-0">
                                                    <span class="rounded-circle bg-success-subtle text-success-emphasis d-flex align-items-center justify-content-center fw-bold small flex-shrink-0"
                                                          style="width: 40px; height: 40px;"><c:out value="${cIniciales}"/></span>
                                                    <span style="min-width: 0;">
                                                        <span class="d-block fw-semibold text-truncate"><c:out value="${curso.nombre}"/></span>
                                                        <span class="d-block small text-body-secondary"><c:out value="${curso.estadoInscripcion}"/></span>
                                                    </span>
                                                </a>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="d-flex flex-wrap gap-3 mt-auto">
                                    <a href="${pageContext.request.contextPath}/misCursos" class="text-decoration-none fw-semibold">
                                        Ver todos mis cursos
                                    </a>
                                    <a href="${pageContext.request.contextPath}/inscripciones" class="text-decoration-none fw-semibold">
                                        Inscribirme a un curso
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Últimas publicaciones (o las de las materias buscadas) --%>
                <div class="card border-0 shadow-sm rounded-4">
                    <div class="card-body">
                        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 pb-3 mb-3 border-bottom">
                            <h2 class="h5 fw-semibold mb-0">
                                <c:choose>
                                    <c:when test="${not empty materiaBuscada}">
                                        Publicaciones de “<c:out value="${materiaBuscada}"/>”
                                    </c:when>
                                    <c:otherwise>Últimas publicaciones</c:otherwise>
                                </c:choose>
                            </h2>
                            <c:if test="${not empty materiaBuscada}">
                                <a href="${pageContext.request.contextPath}/inicio" class="text-decoration-none">Ver todas</a>
                            </c:if>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                <c:out value="${error}" />
                            </div>
                        </c:if>

                        <c:choose>
                            <c:when test="${empty publicaciones and empty error and not empty materiaBuscada}">
                                <div class="alert alert-info mb-0">
                                    No hay publicaciones en las materias que coinciden con la búsqueda.
                                </div>
                            </c:when>
                            <c:when test="${empty publicaciones and empty error}">
                                <div class="alert alert-info mb-0">
                                    Todavía no hay publicaciones en tus cursos.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="publicacion" items="${publicaciones}">
                                    <c:set var="pub" value="${publicacion}" scope="request"/>
                                    <jsp:include page="/layout/publicacion.jsp"/>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />