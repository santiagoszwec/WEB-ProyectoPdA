<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>ANSALUNI</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/estilos/general.css">
</head>
<body>
<nav class="navbar navbar-expand-lg"
     style="background-color: #0B1F3A;">

    <div class="container-fluid px-4">

        <button class="navbar-toggler border-light"
                type="button"
                data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent"
                aria-expanded="false"
                aria-label="Abrir menú">

            <span class="navbar-toggler-icon"
                  style="filter: invert(1);">
            </span>
        </button>

        <div class="collapse navbar-collapse"
             id="navbarSupportedContent">
            <a href="${pageContext.request.contextPath}/inicio"
               class="navbar-brand fw-bold fs-3 mb-0 p-0"
               style="color: white !important; background: transparent !important; border: none !important; text-decoration: none;">
                ANSALUNI
            </a>

            <div class="ms-auto d-flex align-items-center gap-3">
                <c:if test="${not empty sessionScope.usuarioLogueado}">
                    <span class="navbar-text text-white">
                        Usuario:
                        <c:out value="${sessionScope.usuarioLogueado.nombre}" />
                    </span>

                    <button type="button"
                            class="btn btn-link nav-link text-white p-0"
                            data-bs-toggle="modal"
                            data-bs-target="#modalCerrarSesion">
                        Cerrar sesión
                    </button>

                </c:if>
            </div>
        </div>
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
                        <button type="submit" class="btn btn-danger">Confirmar</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:if>


<main class="container-fluid p-0 pt-4">