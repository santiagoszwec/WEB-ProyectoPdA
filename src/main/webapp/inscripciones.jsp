<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inscripciones</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/estilos/general.css">
</head>

<body>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">

        <jsp:include page="/layout/sidebar.jsp" />

        <main class="col-md-9 col-lg-10 p-4">

            <h2 class="mb-2">Inscripciones</h2>

            <c:if test="${param.resultado == 'exito'}">
                <div class="alert alert-success">
                    ¡Te inscribiste correctamente al curso!
                </div>
            </c:if>

            <c:if test="${param.resultado == 'error'}">
                <div class="alert alert-danger">
                    No se pudo realizar la inscripción
                </div>
            </c:if>

            <c:forEach var="semestre" begin="1" end="6">

                <h4 class="mt-4 mb-3">Semestre ${semestre}</h4>

                <div class="row g-3">
                    <c:set var="hayCursos" value="${false}" />

                    <c:forEach var="curso" items="${cursos}">
                        <c:if test="${curso.semestre == semestre}">
                            <c:set var="hayCursos" value="${true}" />

                            <div class="col-12 col-lg-6 col-xl-4">
                                <div class="card h-100 shadow-sm">
                                    <div class="card-body">
                                        <h5 class="card-title">
                                            <c:out value="${curso.nombre}" />
                                        </h5>

                                        <p class="card-text">
                                            <c:out value="${curso.descripcion}" />
                                        </p>

                                        <p class="text-muted mb-3">
                                            Año: ${curso.anio}<br>
                                            Créditos: ${curso.creditos}
                                        </p>

                                        <form method="post"
                                              action="${pageContext.request.contextPath}/inscripciones">
                                            <input type="hidden"
                                                   name="cursoId"
                                                   value="${curso.id}">

                                            <button type="submit"
                                                    class="btn btn-primary">
                                                Inscribirse
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>

                    <c:if test="${not hayCursos}">
                        <div class="col-12">
                            <p class="text-muted">
                                No hay cursos donde inscribirse en este semestre
                            </p>
                        </div>
                    </c:if>
                </div>
            </c:forEach>

        </main>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>