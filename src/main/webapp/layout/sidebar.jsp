<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="rutaOriginal" value="${requestScope['jakarta.servlet.forward.servlet_path']}" />
<c:set var="ruta" value="${empty rutaOriginal ? pageContext.request.servletPath : rutaOriginal}" />

<aside class="col-md-3 col-lg-2 mb-4">
    <div class="card shadow-sm">
        <div class="card-body">
            <h5 class="card-title mb-3">Mi espacio</h5>

            <nav class="nav nav-pills flex-column gap-1" aria-label="Menú principal">
                <a class="nav-link ${fn:startsWith(ruta, '/inicio') ? 'active' : ''}"
                   ${fn:startsWith(ruta, '/inicio') ? 'aria-current="page"' : ''}
                   href="${pageContext.request.contextPath}/inicio">
                    Inicio
                </a>
                <a class="nav-link ${fn:startsWith(ruta, '/perfil') and param.id == sessionScope.usuarioLogueado.id ? 'active' : ''}"
                   ${fn:startsWith(ruta, '/perfil') and param.id == sessionScope.usuarioLogueado.id ? 'aria-current="page"' : ''}
                   href="${pageContext.request.contextPath}/perfil?id=${sessionScope.usuarioLogueado.id}">
                    Mi perfil
                </a>
                <a class="nav-link ${fn:startsWith(ruta, '/misCursos') ? 'active' : ''}"
                   ${fn:startsWith(ruta, '/misCursos') ? 'aria-current="page"' : ''}
                   href="${pageContext.request.contextPath}/misCursos">
                    Mis cursos
                </a>
                <a class="nav-link ${fn:startsWith(ruta, '/misHoras') ? 'active' : ''}"
                   ${fn:startsWith(ruta, '/misHoras') ? 'aria-current="page"' : ''}
                   href="${pageContext.request.contextPath}/misHoras">
                    Mis horas de estudio
                </a>
                <a class="nav-link ${fn:startsWith(ruta, '/notificaciones') ? 'active' : ''}"
                   ${fn:startsWith(ruta, '/notificaciones') ? 'aria-current="page"' : ''}
                   href="${pageContext.request.contextPath}/notificaciones">
                    Notificaciones
                </a>
                <a class="nav-link ${fn:startsWith(ruta, '/foro') ? 'active' : ''}"
                   ${fn:startsWith(ruta, '/foro') ? 'aria-current="page"' : ''}
                   href="${pageContext.request.contextPath}/foro">
                    Foro
                </a>
                <a class="nav-link ${fn:startsWith(ruta, '/inscripciones') ? 'active' : ''}"
                   ${fn:startsWith(ruta, '/inscripciones') ? 'aria-current="page"' : ''}
                   href="${pageContext.request.contextPath}/inscripciones">
                    Inscribirme a curso
                </a>
                <c:if test="${sessionScope.usuarioLogueado.admin}">
                    <a class="nav-link ${fn:startsWith(ruta, '/gestionPublicaciones') ? 'active' : ''}"
                       ${fn:startsWith(ruta, '/gestionPublicaciones') ? 'aria-current="page"' : ''}
                       href="${pageContext.request.contextPath}/gestionPublicaciones">
                        Gestión de publicaciones
                    </a>
                </c:if>
            </nav>
        </div>
    </div>
</aside>