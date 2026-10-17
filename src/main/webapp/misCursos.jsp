<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />
        <div class="col-md-9 col-lg-10 p-4">
            <div class="mb-4">
                <h1>Mis cursos</h1>
            </div>

            <c:choose>
                <c:when test="${empty cursos}">
                    <div class="alert alert-info">
                        No estás inscripto en ningún curso
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
                                                    Entrar al curso
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