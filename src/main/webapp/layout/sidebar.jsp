<aside class="col-md-3 col-lg-2 mb-4">
    <div class="card shadow-sm">
        <div class="card-body">
            <h5 class="card-title mb-3">Mi espacio</h5>

            <nav class="nav flex-column">
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/inicio">
                    Inicio
                </a>
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/misCursos">
                    Mis cursos
                </a>
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/misHoras">
                    Mis horas de estudio
                </a>
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/notificaciones">
                    Notificaciones
                </a>
                <a class="nav-link"
                   href="${pageContext.request.contextPath}/foro">
                    Foro
                </a>
                <c:if test="${sessionScope.usuarioLogueado.admin}">
                    <a class="nav-link"
                       href="${pageContext.request.contextPath}/gestionPublicaciones">
                        Gestión de publicaciones
                    </a>
                </c:if>
            </nav>
        </div>
    </div>
</aside>