
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="ocultarMenu" value="false" scope="request"/>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <c:set var="saludoNombre" value="${fn:split(sessionScope.usuarioLogueado.nombre, ' ')[0]}"/>
            <div class="p-4 mb-4 bg-light rounded-3">
                <h1>Hola, <c:out value="${saludoNombre}"/>!</h1>
                <p class="mb-0">
                    Estas son las últimas publicaciones de tus cursos
                </p>
            </div>

            <%-- ★ NUEVO: dos columnas. En pantalla chica las tarjetas pasan arriba, plegadas --%>
            <div class="row g-4">

                <%-- ===== Columna principal: publicaciones ===== --%>
                <div class="col-lg-8 col-xl-9">

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${error}" />
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${empty publicaciones and empty error}">
                            <div class="alert alert-info">
                                Todavía no hay publicaciones en tus cursos
                            </div>
                        </c:when>

                        <c:otherwise>
                            <div>
                                <c:forEach var="publicacion" items="${publicaciones}">
                                    <c:set var="pub" value="${publicacion}" scope="request"/>
                                    <jsp:include page="/layout/publicacion.jsp"/>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <%-- ★ NUEVO ===== Columna lateral: Ir a mis cursos + Atajos ===== --%>
                <div class="col-lg-4 col-xl-3 order-first order-lg-last">
                    <div class="sticky-lg-top" style="top: 1rem;">

                        <%-- Tarjeta: Ir a mis cursos --%>
                        <div class="card rounded-4 mb-3">
                            <div class="card-header bg-transparent border-0 pb-0">
                                <%-- Pantalla chica: botón que pliega/despliega. Pantalla grande: título fijo --%>
                                <button type="button"
                                        class="btn w-100 d-flex justify-content-between align-items-center fw-semibold p-0 d-lg-none"
                                        data-bs-toggle="collapse" data-bs-target="#panelCursos"
                                        aria-expanded="false" aria-controls="panelCursos">
                                    Ir a mis cursos <i class="bi bi-chevron-down"></i>
                                </button>
                                <h2 class="h6 fw-semibold mb-0 d-none d-lg-block">Ir a mis cursos</h2>
                            </div>

                            <div class="collapse d-lg-block" id="panelCursos">
                                <div class="card-body pt-2">
                                    <c:choose>
                                        <c:when test="${empty cursosCursando}">
                                            <p class="small text-body-secondary mb-0">
                                                Todavía no estás cursando ningún curso.
                                            </p>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="list-group list-group-flush">
                                                <c:forEach var="curso" items="${cursosCursando}">
                                                    <c:set var="cNombre" value="${curso.nombre}"/>
                                                    <c:set var="cPartes" value="${fn:split(cNombre, ' ')}"/>
                                                    <c:set var="cIniciales"
                                                           value="${fn:toUpperCase(fn:substring(cPartes[0], 0, 1))}${fn:length(cPartes) > 1 ? fn:toUpperCase(fn:substring(cPartes[fn:length(cPartes) - 1], 0, 1)) : ''}"/>
                                                    <c:set var="cColores" value="${['text-bg-primary', 'text-bg-success', 'text-bg-danger', 'text-bg-dark', 'text-bg-secondary']}"/>

                                                    <a href="${pageContext.request.contextPath}/curso?id=${curso.id}"
                                                       class="list-group-item list-group-item-action d-flex align-items-center gap-2 px-0 rounded-3">
                                                        <span class="rounded-circle d-flex align-items-center justify-content-center fw-bold small flex-shrink-0 ${cColores[fn:length(cNombre) % 5]}"
                                                              style="width:32px;height:32px;"><c:out value="${cIniciales}"/></span>
                                                        <span class="min-w-0" style="min-width:0;">
                                                            <span class="d-block fw-semibold small"><c:out value="${curso.nombre}"/></span>
                                                            <span class="d-block small text-body-secondary">
                                                                Semestre <c:out value="${curso.semestre}"/> · <c:out value="${curso.estadoInscripcion}"/>
                                                            </span>
                                                        </span>
                                                    </a>
                                                </c:forEach>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>

                                    <a href="${pageContext.request.contextPath}/misCursos"
                                       class="btn btn-link btn-sm px-0 mt-2 text-decoration-none fw-semibold">
                                        Ver todos mis cursos
                                        <c:if test="${totalCursando gt fn:length(cursosCursando)}">
                                            (${totalCursando})
                                        </c:if>
                                    </a>
                                </div>
                            </div>
                        </div>

                        <%-- Tarjeta: Atajos --%>
                        <div class="card rounded-4">
                            <div class="card-header bg-transparent border-0 pb-0">
                                <button type="button"
                                        class="btn w-100 d-flex justify-content-between align-items-center fw-semibold p-0 d-lg-none"
                                        data-bs-toggle="collapse" data-bs-target="#panelAtajos"
                                        aria-expanded="false" aria-controls="panelAtajos">
                                    Atajos <i class="bi bi-chevron-down"></i>
                                </button>
                                <h2 class="h6 fw-semibold mb-0 d-none d-lg-block">Atajos</h2>
                            </div>

                            <div class="collapse d-lg-block" id="panelAtajos">
                                <div class="card-body pt-2 d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/misHoras"
                                       class="btn btn-outline-primary rounded-pill text-start">
                                        <i class="bi bi-clock-history me-2"></i>Registrar horas de estudio
                                    </a>
                                    <a href="${pageContext.request.contextPath}/inscripciones"
                                       class="btn btn-outline-primary rounded-pill text-start">
                                        <i class="bi bi-plus-circle me-2"></i>Inscribirme a un curso
                                    </a>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
                <%-- ★ FIN NUEVO --%>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />