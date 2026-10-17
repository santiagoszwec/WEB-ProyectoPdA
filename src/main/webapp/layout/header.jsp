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

                    <a class="nav-link text-white"
                       href="${pageContext.request.contextPath}/logout">
                        Cerrar sesión
                    </a>
                </c:if>
            </div>
        </div>
    </div>
</nav>
<main class="container-fluid p-0 pt-4">