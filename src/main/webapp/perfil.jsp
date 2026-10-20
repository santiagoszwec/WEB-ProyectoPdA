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
