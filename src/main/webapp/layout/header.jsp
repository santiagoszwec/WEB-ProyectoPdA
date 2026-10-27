<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>ANSALUNI</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos/general.css">
</head>
<body>
<nav class="navbar navbar-dark navbar-ansaluni">
    <div class="container-fluid px-3 px-md-4 d-flex align-items-center gap-2">

        <c:if test="${not empty sessionScope.usuarioLogueado}">
             <button id="botonSidebar"
                    hidden
                    class="btn btn-outline-light border-0 px-2"
                    type="button"
                    aria-controls="sidebarAnsaluni"
                    aria-expanded="true"
                    aria-label="Mostrar u ocultar el menú lateral">
                <i class="bi bi-list fs-3"></i>
             </button>
        </c:if>

        <a href="${pageContext.request.contextPath}/inicio"
           class="navbar-brand fw-bold fs-3 mb-0 p-0">
            ANSALUNI
        </a>

        <c:if test="${not empty sessionScope.usuarioLogueado}">
            <div class="ms-auto d-flex align-items-center gap-3">
                <span class="navbar-text text-white d-none d-sm-inline">
                    <i class="bi bi-person-circle me-1"></i>
                    <c:out value="${sessionScope.usuarioLogueado.nombre}" />
                </span>

                <button type="button"
                        class="btn btn-sm btn-outline-light"
                        data-bs-toggle="modal"
                        data-bs-target="#modalCerrarSesion"
                        aria-label="Cerrar sesión">
                    <i class="bi bi-box-arrow-right"></i>
                    <span class="d-none d-sm-inline ms-1">Cerrar sesión</span>
                </button>
            </div>
        </c:if>
    </div>
</nav>
<c:if test="${not empty sessionScope.usuarioLogueado}">
    <div class="modal fade" id="modalCerrarSesion" tabindex="-1"
         aria-labelledby="tituloCerrarSesion" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="tituloCerrarSesion">Cerrar sesión</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                            aria-label="Cerrar"></button>
                </div>
                <div class="modal-body">
                    ¿Confirmás que querés cerrar la sesión?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary"
                            data-bs-dismiss="modal">Cancelar</button>

                    <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                        <button type="submit" class="btn btn-danger">Cerrar sesión</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:if>


<main class="container-fluid p-0 pt-4">