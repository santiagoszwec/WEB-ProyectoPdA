<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

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

<c:set var="base" value="${pageContext.request.contextPath}" />

<c:if test="${not empty sessionScope.usuarioLogueado}">
    <c:set var="partesNombre" value="${fn:split(fn:trim(sessionScope.usuarioLogueado.nombre), ' ')}" />
    <c:set var="cantPartes" value="${fn:length(partesNombre)}" />
    <c:set var="iniciales" value="${fn:toUpperCase(fn:substring(partesNombre[0], 0, 1))}${cantPartes > 1 ? fn:toUpperCase(fn:substring(partesNombre[cantPartes - 1], 0, 1)) : ''}" />
    <c:set var="totalNotif" value="${empty cabNotifTotal ? 0 : cabNotifTotal}" />
</c:if>

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

        <a href="${base}/inicio"
           class="navbar-brand fw-bold fs-3 mb-0 p-0">
            ANSALUNI
        </a>

        <c:if test="${not empty sessionScope.usuarioLogueado}">
            <div class="ms-auto d-flex align-items-center gap-1">

                <%-- Notificaciones --%>
                <div class="dropdown">
                    <button type="button"
                            class="btn-cabecera"
                            data-bs-toggle="dropdown"
                            aria-expanded="false"
                            aria-label="Notificaciones (${totalNotif})">
                        <i class="bi bi-bell fs-5"></i>
                        <c:if test="${totalNotif > 0}">
                            <span class="contador-notif" aria-hidden="true">${totalNotif > 9 ? '9+' : totalNotif}</span>
                        </c:if>
                    </button>

                    <div class="dropdown-menu dropdown-menu-end menu-cabecera menu-notificaciones">
                        <div class="menu-cabecera-titulo">
                            <strong>Notificaciones</strong>
                            <c:if test="${totalNotif > 0}">
                                <span class="badge rounded-pill bg-danger-subtle text-danger-emphasis">
                                    ${totalNotif} ${totalNotif == 1 ? 'pendiente' : 'pendientes'}
                                </span>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${empty cabNotifRecientes}">
                                <div class="notif-vacia">
                                    <i class="bi bi-bell-slash fs-3"></i>
                                    <span>No tenés notificaciones pendientes.</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="notif-lista">
                                    <c:forEach var="n" items="${cabNotifRecientes}">
                                        <c:choose>
                                            <c:when test="${n.publicacionActiva and n.esDuda}">
                                                <c:set var="destino" value="${base}/duda?id=${n.publicacionId}" />
                                            </c:when>
                                            <c:when test="${n.publicacionActiva and not empty n.cursoId}">
                                                <c:set var="destino" value="${base}/curso?id=${n.cursoId}#publicacion-${n.publicacionId}" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="destino" value="${base}/notificaciones" />
                                            </c:otherwise>
                                        </c:choose>
                                        <a class="notif-item" href="${destino}">
                                            <span class="notif-texto"><c:out value="${n.mensaje}" /></span>
                                            <span class="notif-meta">
                                                <span class="badge text-bg-secondary"><c:out value="${n.tipo}" /></span>
                                                <c:out value="${n.fecha}" />
                                            </span>
                                        </a>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <a class="notif-pie" href="${base}/notificaciones">Ver todas y marcar como leídas</a>
                    </div>
                </div>

                <%-- Círculo de perfil --%>
                <div class="dropdown">
                    <button type="button"
                            class="btn-cabecera"
                            data-bs-toggle="dropdown"
                            aria-expanded="false"
                            aria-label="Menú de usuario">
                        <span class="avatar-cabecera"><c:out value="${iniciales}" /></span>
                    </button>

                    <div class="dropdown-menu dropdown-menu-end menu-cabecera menu-usuario">
                        <div class="usuario-datos">
                            <span class="avatar-cabecera avatar-grande"><c:out value="${iniciales}" /></span>
                            <div class="usuario-texto">
                                <div class="fw-bold"><c:out value="${sessionScope.usuarioLogueado.nombre}" /></div>
                                <div class="small text-secondary"><c:out value="${sessionScope.usuarioLogueado.correo}" /></div>
                                <span class="badge rounded-pill bg-success-subtle text-success-emphasis mt-1">
                                    ${sessionScope.usuarioLogueado.admin ? 'Administrador' : 'Estudiante'}
                                </span>
                            </div>
                        </div>

                        <div class="menu-cabecera-opciones">
                            <a class="dropdown-item d-flex align-items-center gap-2"
                               href="${base}/perfil?id=${sessionScope.usuarioLogueado.id}">
                                <i class="bi bi-person"></i>Mi perfil
                            </a>
                            <button type="button"
                                    class="dropdown-item d-flex align-items-center gap-2 text-danger fw-semibold"
                                    data-bs-toggle="modal"
                                    data-bs-target="#modalCerrarSesion">
                                <i class="bi bi-box-arrow-right"></i>Cerrar sesión
                            </button>
                        </div>
                    </div>
                </div>

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

                    <form action="${base}/logout" method="post" class="m-0">
                        <button type="submit" class="btn btn-danger">Cerrar sesión</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:if>


<main class="container-fluid p-0 pt-4">