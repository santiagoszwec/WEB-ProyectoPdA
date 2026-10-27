<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="rutaOriginal" value="${requestScope['jakarta.servlet.forward.servlet_path']}" />
<c:set var="ruta" value="${empty rutaOriginal ? pageContext.request.servletPath : rutaOriginal}" />
<c:set var="base" value="${pageContext.request.contextPath}" />

<c:set var="enInicio" value="${fn:startsWith(ruta, '/inicio')}" />
<c:set var="enMisCursos" value="${fn:startsWith(ruta, '/misCursos') or fn:startsWith(ruta, '/curso') or fn:startsWith(ruta, '/duda')}" />
<c:set var="enForo" value="${fn:startsWith(ruta, '/foro')}" />
<c:set var="enPerfilPropio" value="${fn:startsWith(ruta, '/perfil') and (empty param.id or param.id == sessionScope.usuarioLogueado.id)}" />
<c:set var="enHoras" value="${fn:startsWith(ruta, '/misHoras')}" />
<c:set var="enNotificaciones" value="${fn:startsWith(ruta, '/notificaciones')}" />
<c:set var="enInscripciones" value="${fn:startsWith(ruta, '/inscripciones')}" />
<c:set var="enGestion" value="${fn:startsWith(ruta, '/gestionPublicaciones')}" />
<%-- "Mi perfil" se abre solo cuando estás en alguna de sus páginas --%>
<c:set var="enMiPerfil" value="${enPerfilPropio or enHoras or enNotificaciones or enInscripciones}" />

<%-- ★ CAMBIO: id nuevo y sin clases col-* (el ancho lo da general.css) --%>
<aside class="sidebar-ansaluni" id="sidebarAnsaluni">

    <div class="sidebar-contenido">

        <p class="sidebar-titulo">
            <i class="bi bi-compass me-2"></i>Mi espacio
        </p>

        <nav class="nav flex-column gap-1 nav-lateral" aria-label="Menú principal">

            <a class="nav-link ${enInicio ? 'active' : ''}"
               ${enInicio ? 'aria-current="page"' : ''}
               href="${base}/inicio">
                <i class="bi bi-house-door"></i><span>Inicio</span>
            </a>

            <a class="nav-link ${enMisCursos ? 'active' : ''}"
               ${enMisCursos ? 'aria-current="page"' : ''}
               href="${base}/misCursos">
                <i class="bi bi-journal-bookmark"></i><span>Mis cursos</span>
            </a>

            <a class="nav-link ${enForo ? 'active' : ''}"
               ${enForo ? 'aria-current="page"' : ''}
               href="${base}/foro">
                <i class="bi bi-chat-square-text"></i><span>Foro</span>
            </a>

            <button type="button"
                    class="nav-link nav-link-grupo ${enMiPerfil ? 'grupo-activo' : ''}"
                    data-bs-toggle="collapse"
                    data-bs-target="#menuMiPerfil"
                    aria-expanded="${enMiPerfil}"
                    aria-controls="menuMiPerfil">
                <i class="bi bi-person-circle"></i><span>Mi perfil</span>
                <i class="bi bi-chevron-down flecha-menu"></i>
            </button>

            <div class="collapse ${enMiPerfil ? 'show' : ''}" id="menuMiPerfil">
                <div class="submenu-lateral">

                    <a class="nav-link ${enPerfilPropio ? 'active' : ''}"
                       ${enPerfilPropio ? 'aria-current="page"' : ''}
                       href="${base}/perfil?id=${sessionScope.usuarioLogueado.id}">
                        <i class="bi bi-person"></i><span>Ver mi perfil</span>
                    </a>

                    <a class="nav-link ${enHoras ? 'active' : ''}"
                       ${enHoras ? 'aria-current="page"' : ''}
                       href="${base}/misHoras">
                        <i class="bi bi-clock-history"></i><span>Mis horas de estudio</span>
                    </a>

                    <a class="nav-link ${enNotificaciones ? 'active' : ''}"
                       ${enNotificaciones ? 'aria-current="page"' : ''}
                       href="${base}/notificaciones">
                        <i class="bi bi-bell"></i><span>Notificaciones</span>
                    </a>

                    <a class="nav-link ${enInscripciones ? 'active' : ''}"
                       ${enInscripciones ? 'aria-current="page"' : ''}
                       href="${base}/inscripciones">
                        <i class="bi bi-plus-circle"></i><span>Inscribirme a curso</span>
                    </a>
                </div>
            </div>

            <c:if test="${sessionScope.usuarioLogueado.admin}">
                <hr class="separador-lateral">
                <a class="nav-link ${enGestion ? 'active' : ''}"
                   ${enGestion ? 'aria-current="page"' : ''}
                   href="${base}/gestionPublicaciones">
                    <i class="bi bi-shield-check"></i><span>Gestión de publicaciones</span>
                </a>
            </c:if>
        </nav>
    </div>
</aside>

<%-- ★ NUEVO: pantalla completa = desplegado ↔ angosto (se recuerda). Pantalla chica = oculto ↔ desplegado --%>
<script>
    (function () {
        var aside = document.getElementById("sidebarAnsaluni");
        var boton = document.getElementById("botonSidebar");
        if (!aside) return;

        var CLAVE = "ansaluniSidebarMini";
        var movil = window.matchMedia("(max-width: 767.98px)");
        var mini = false;      // solo vale en pantalla completa (arranca desplegado)
        var abierto = false;   // solo vale en pantalla chica (arranca oculto)

        try { mini = localStorage.getItem(CLAVE) === "1"; } catch (e) {}

        function guardar() {
            try { localStorage.setItem(CLAVE, mini ? "1" : "0"); } catch (e) {}
        }

        function pintar() {
            aside.classList.toggle("sidebar-mini", !movil.matches && mini);
            aside.classList.toggle("movil-abierto", movil.matches && abierto);
            if (boton) boton.setAttribute("aria-expanded", String(movil.matches ? abierto : !mini));
        }

        pintar();
        // Si se cambia el tamaño de la ventana, pasa solo de un comportamiento al otro
        if (movil.addEventListener) movil.addEventListener("change", pintar);

        if (boton) {
            boton.hidden = false;
            boton.addEventListener("click", function () {
                if (movil.matches) {
                    abierto = !abierto;
                } else {
                    mini = !mini;
                    guardar();
                }
                pintar();
            });
        }

        // Pantalla completa + sidebar angosto: tocar "Mi perfil" lo expande y abre su submenú
        var grupo = aside.querySelector(".nav-link-grupo");
        if (grupo) {
            grupo.addEventListener("click", function (e) {
                if (movil.matches || !mini) return;   // en los demás casos Bootstrap lo maneja normal
                e.stopPropagation();
                mini = false;
                guardar();
                pintar();
                var panel = document.getElementById("menuMiPerfil");
                if (panel && window.bootstrap) {
                    bootstrap.Collapse.getOrCreateInstance(panel, { toggle: false }).show();
                }
            });
        }
    })();
</script>