<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">
            <div class="mx-auto d-flex flex-column gap-4" style="max-width: 960px;">

                <div>
                    <%-- ★ NUEVO: "Volver al curso" ahora es el botón con recuadro, igual que en los demás --%>
                    <c:if test="${not empty curso}">
                        <a href="${pageContext.request.contextPath}/curso?id=${curso.id}"
                           class="btn btn-outline-secondary mb-3">
                            ← Volver al curso
                        </a>
                    </c:if>

                    <h1 class="mb-1">Mi progreso</h1>
                    <c:if test="${not empty curso}">
                        <p class="text-muted mb-0"><c:out value="${curso.nombre}" /></p>
                    </c:if>
                </div>

                <c:if test="${not empty progresoMensaje}">
                    <div class="alert alert-success mb-0" role="alert"><c:out value="${progresoMensaje}" /></div>
                </c:if>
                <c:if test="${not empty progresoError}">
                    <div class="alert alert-danger mb-0" role="alert"><c:out value="${progresoError}" /></div>
                </c:if>

                <c:choose>
                    <c:when test="${not empty error}">
                        <div class="alert alert-danger mb-0" role="alert"><c:out value="${error}" /></div>
                        <div>
                            <a href="${pageContext.request.contextPath}/misCursos" class="btn btn-outline-secondary">Ir a Mis cursos</a>
                        </div>
                    </c:when>
                    <c:otherwise>

                        <%-- ★ NUEVO: tarjeta "Avance de la materia" con el estilo del perfil --%>
                        <section class="card border-0 shadow-sm rounded-4">
                            <div class="card-body p-4">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h2 class="h5 fw-bold mb-0 d-flex align-items-center gap-2">
                                        <i class="bi bi-graph-up"></i> Avance de la materia
                                    </h2>
                                    <span class="badge text-bg-dark rounded-pill fs-6">${progreso.porcentaje}%</span>
                                </div>
                                <div class="progress" role="progressbar" aria-label="Progreso en la materia"
                                     aria-valuenow="${progreso.porcentaje}" aria-valuemin="0" aria-valuemax="100"
                                     style="height: 12px;">
                                    <div class="progress-bar ${progreso.porcentaje == 100 ? 'bg-success' : ''}"
                                         style="width: ${progreso.porcentaje}%; ${progreso.porcentaje == 100 ? '' : 'background-color: #2f6db5;'}"></div>
                                </div>
                                <p class="text-body-secondary small mt-2 mb-0">
                                    ${progreso.completadas} de ${progreso.total} tareas completadas
                                    <c:if test="${progreso.pendientes > 0}">· te faltan ${progreso.pendientes}</c:if>
                                </p>
                            </div>
                        </section>

                        <%-- ★ NUEVO: formulario "Agregar tarea" con el estilo del perfil (mismos campos y validaciones) --%>
                        <section class="card border-0 shadow-sm rounded-4">
                            <div class="card-body p-4">
                                <h2 class="h5 fw-bold mb-3 d-flex align-items-center gap-2">
                                    <i class="bi bi-plus-circle"></i> Agregar tarea
                                </h2>
                                <form method="post" action="${pageContext.request.contextPath}/progreso" class="row g-3 align-items-start">
                                    <input type="hidden" name="accion" value="agregar">
                                    <input type="hidden" name="curso" value="${curso.id}">

                                    <div class="col-md-6">
                                        <label for="titulo" class="form-label fw-semibold">Título <span class="text-danger">*</span></label>
                                        <input type="text" id="titulo" name="titulo" maxlength="150" required
                                               placeholder="Ej.: Entregar el laboratorio 2"
                                               class="form-control ${not empty errorTitulo ? 'is-invalid' : ''}"
                                               ${not empty errorTitulo ? 'aria-describedby="errorTitulo"' : ''}
                                               value="<c:out value='${valorTitulo}' />">
                                        <c:if test="${not empty errorTitulo}">
                                            <div id="errorTitulo" class="invalid-feedback"><c:out value="${errorTitulo}" /></div>
                                        </c:if>
                                    </div>

                                    <div class="col-md-3">
                                        <label for="fechaLimite" class="form-label fw-semibold">Fecha límite <span class="text-muted fw-normal">(opcional)</span></label>
                                        <input type="date" id="fechaLimite" name="fechaLimite" min="${hoy}"
                                               class="form-control ${not empty errorFecha ? 'is-invalid' : ''}"
                                               ${not empty errorFecha ? 'aria-describedby="errorFecha"' : ''}
                                               value="<c:out value='${valorFecha}' />">
                                        <c:if test="${not empty errorFecha}">
                                            <div id="errorFecha" class="invalid-feedback"><c:out value="${errorFecha}" /></div>
                                        </c:if>
                                    </div>

                                    <div class="col-md-3 d-grid">
                                        <label class="form-label invisible" aria-hidden="true">Agregar</label>
                                        <button type="submit" class="btn btn-dark rounded-pill fw-semibold">Agregar tarea</button>
                                    </div>
                                </form>
                            </div>
                        </section>

                        <%-- ★ NUEVO: tarjeta "Tareas" con el estilo del perfil --%>
                        <section class="card border-0 shadow-sm rounded-4">
                            <div class="card-body p-4">
                                <h2 class="h5 fw-bold mb-3 d-flex align-items-center gap-2">
                                    <i class="bi bi-check2-square"></i> Tareas
                                </h2>

                                <c:choose>
                                    <c:when test="${progreso.total == 0}">
                                        <div class="alert alert-info mb-0">
                                            Todavía no tenés tareas en esta materia. Agregá la primera con el formulario de arriba.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <ul class="list-group list-group-flush">
                                            <c:forEach var="tarea" items="${tareas}">
                                                <li class="list-group-item px-0 d-flex justify-content-between align-items-center gap-3 flex-wrap">
                                                    <div>
                                                        <c:choose>
                                                            <c:when test="${tarea.completada}">
                                                                <span class="text-success" aria-hidden="true">✓</span>
                                                                <span class="text-decoration-line-through text-body-secondary"><c:out value="${tarea.titulo}" /></span>
                                                                <div class="small text-body-secondary">Completada el <c:out value="${tarea.fechaCompletada}" /></div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="fw-semibold"><c:out value="${tarea.titulo}" /></span>
                                                                <c:if test="${tarea.vencida}">
                                                                    <span class="badge rounded-pill text-bg-danger ms-1">Vencida</span>
                                                                </c:if>
                                                                <div class="small text-body-secondary">
                                                                    <c:choose>
                                                                        <c:when test="${not empty tarea.fechaLimite}">Fecha límite: <c:out value="${tarea.fechaLimite}" /></c:when>
                                                                        <c:otherwise>Sin fecha límite</c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>

                                                    <c:if test="${not tarea.completada}">
                                                        <form method="post" action="${pageContext.request.contextPath}/progreso" class="mb-0">
                                                            <input type="hidden" name="accion" value="completar">
                                                            <input type="hidden" name="curso" value="${curso.id}">
                                                            <input type="hidden" name="tarea" value="${tarea.id}">
                                                            <button type="submit" class="btn btn-sm btn-outline-success rounded-pill px-3 fw-semibold">Marcar como completada</button>
                                                        </form>
                                                    </c:if>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </section>

                    </c:otherwise>
                </c:choose>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />