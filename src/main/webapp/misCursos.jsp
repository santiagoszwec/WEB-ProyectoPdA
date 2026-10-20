<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />
        <div class="col-md-9 col-lg-10 p-4">
            <div class="mb-4">
                <h1>Mis cursos</h1>


                <p class="text-muted mb-0">
                    <c:choose>
                        <c:when test="${totalCursos == 1}">Estás inscripto en 1 curso.</c:when>
                        <c:otherwise>Estás inscripto en <c:out value="${totalCursos}" /> cursos.</c:otherwise>
                    </c:choose>
                    <c:if test="${hayFiltros}">
                        Mostrando <c:out value="${cursos.size()}" /> de <c:out value="${totalCursos}" />.
                    </c:if>
                </p>
            </div>


            <c:if test="${totalCursos > 0}">
                <form action="${pageContext.request.contextPath}/misCursos" method="get"
                      class="row g-2 align-items-end mb-4">
                    <div class="col-12 col-md-5">
                        <label for="buscar" class="form-label">Buscar por nombre</label>
                        <input type="text" id="buscar" name="buscar" class="form-control"
                               placeholder="Nombre del curso"
                               value="<c:out value='${buscar}' />">
                    </div>
                    <div class="col-12 col-md-3">
                        <label for="estado" class="form-label">Estado</label>
                        <select id="estado" name="estado" class="form-select">
                            <option value="">Todos</option>
                            <option value="Cursando" ${estadoElegido == 'Cursando' ? 'selected' : ''}>Cursando</option>
                            <option value="Cursada" ${estadoElegido == 'Cursada' ? 'selected' : ''}>Cursada</option>
                            <option value="Aprobada" ${estadoElegido == 'Aprobada' ? 'selected' : ''}>Aprobada</option>
                        </select>
                    </div>
                    <div class="col-12 col-md-4 d-flex gap-2">
                        <button type="submit" class="btn btn-primary">Buscar</button>
                        <a href="${pageContext.request.contextPath}/misCursos"
                           class="btn btn-outline-secondary">Limpiar</a>
                    </div>
                </form>
            </c:if>

            <c:choose>
                <c:when test="${empty cursos}">

                    <div class="alert alert-info">
                        <c:choose>
                            <c:when test="${hayFiltros}">
                                No se encontraron cursos con esa búsqueda o filtro
                            </c:when>
                            <c:otherwise>
                                No estás inscripto en ningún curso
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="row g-4">
                        <c:forEach var="curso" items="${cursos}">
                            <div class="col-12 col-md-6 col-xl-4 d-flex">

                                <a href="${pageContext.request.contextPath}/curso?id=${curso.id}"
                                   class="text-decoration-none text-dark w-100">

                                    <div class="card h-100 shadow-sm">
                                        <div class="card-body p-4 d-flex flex-column">

                                            <div class="d-flex justify-content-between align-items-start gap-2 mb-3">
                                                <h5 class="card-title mb-0">
                                                    <c:out value="${curso.nombre}" />
                                                </h5>
                                                <c:choose>
                                                    <c:when test="${curso.estadoInscripcion == 'Cursando'}">
                                                        <span class="badge text-bg-primary flex-shrink-0">
                                                            Cursando
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${curso.estadoInscripcion == 'Cursada'}">
                                                        <span class="badge text-bg-secondary flex-shrink-0">
                                                            Cursada
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${curso.estadoInscripcion == 'Aprobada'}">
                                                        <span class="badge text-bg-success flex-shrink-0">
                                                            Aprobada
                                                        </span>
                                                    </c:when>
                                                </c:choose>
                                            </div>
                                            <p class="text-muted mb-2">
                                                Año: <c:out value="${curso.anio}" />
                                                · Semestre: <c:out value="${curso.semestre}" />
                                            </p>

                                            <p class="mb-4">
                                                Créditos: <c:out value="${curso.creditos}" />
                                            </p>
                                            <div class="mt-auto">
                                                <span class="btn btn-outline-primary w-100">
                                                    ${curso.estadoInscripcion == 'Aprobada' ? 'Ver curso' : 'Entrar al curso'}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<jsp:include page="/layout/footer.jsp" />