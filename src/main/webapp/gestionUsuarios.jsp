<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">
            <div class="mx-auto d-flex flex-column gap-4" style="max-width: 1100px;">

                <div>
                    <a href="${pageContext.request.contextPath}/inicio"
                       class="btn btn-outline-secondary">
                        ← Volver al inicio
                    </a>
                </div>

                <h1 class="h3 fw-bold mb-3">Gestión de Usuarios</h1>

                <c:if test="${not empty mensajeExito}">
                    <div class="alert alert-success alert-dismissible fade show mb-0" role="alert">
                        <i class="bi bi-check-circle me-1"></i>
                        <c:out value="${mensajeExito}" />
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
                    </div>
                </c:if>

                <c:if test="${not empty mensajeError}">
                    <div class="alert alert-danger alert-dismissible fade show mb-0" role="alert">
                        <i class="bi bi-exclamation-triangle me-1"></i>
                        <c:out value="${mensajeError}" />
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
                    </div>
                </c:if>

                <section class="card border-0 shadow-sm rounded-4">
                    <div class="card-body">
                        <c:if test="${empty usuarios}">
                            <div class="alert alert-info mb-0">
                                No hay usuarios registrados en el sistema.
                            </div>
                        </c:if>

                        <c:if test="${not empty usuarios}">
                            <div class="table-responsive">
                                <table class="table align-middle mb-0">
                                    <thead class="table-light">
                                        <tr class="small text-body-secondary">
                                            <th scope="col" class="fw-semibold">Usuario</th>
                                            <th scope="col" class="fw-semibold">Correo</th>
                                            <th scope="col" class="fw-semibold">Rol</th>
                                            <th scope="col" class="fw-semibold text-center">Estado</th>
                                            <th scope="col" class="fw-semibold text-center">Hasta</th>
                                            <th scope="col" class="fw-semibold text-center">Acción</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="u" items="${usuarios}">
                                            <c:set var="s" value="${suspensiones[u.id]}" />
                                            <tr>
                                                <td><c:out value="${u.nombre}" /></td>
                                                <td><c:out value="${u.correo}" /></td>
                                                <td><c:out value="${u.rol}" /></td>
                                                <td class="text-center small">
                                                    <c:choose>
                                                        <c:when test="${not empty s}">
                                                            <span class="badge text-bg-danger">Suspendido</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge text-bg-success">Activo</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-center small">
                                                    <c:choose>
                                                        <c:when test="${not empty s}">
                                                            <fmt:formatDate value="${s.fechaFin}" type="date" datePattern="dd/MM/yyyy" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-body-secondary">—</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-center">
                                                    <c:choose>
                                                        <c:when test="${empty s}">
                                                            <!-- Usuario activo: botón Suspender -->
                                                            <button type="button"
                                                                    class="btn btn-outline-warning btn-sm"
                                                                    data-bs-toggle="modal"
                                                                    data-bs-target="#modalSuspender${u.id}">
                                                                Suspender cuenta
                                                            </button>

                                                            <div class="modal fade" id="modalSuspender${u.id}"
                                                                 tabindex="-1" aria-labelledby="modalSuspender${u.id}Label"
                                                                 aria-hidden="true">
                                                                <div class="modal-dialog modal-dialog-centered">
                                                                    <div class="modal-content">
                                                                        <div class="modal-header">
                                                                            <h5 class="modal-title fs-6" id="modalSuspender${u.id}Label">
                                                                                Suspender cuenta
                                                                            </h5>
                                                                            <button type="button" class="btn-close"
                                                                                    data-bs-dismiss="modal"
                                                                                    aria-label="Cerrar"></button>
                                                                        </div>
                                                                        <div class="modal-body">
                                                                            <form action="${pageContext.request.contextPath}/gestionUsuarios"
                                                                                    method="post">
                                                                                <input type="hidden" name="accion" value="suspender" />
                                                                                <input type="hidden" name="usuarioId" value="${u.id}" />

                                                                                <div class="mb-3">
                                                                                    <label class="form-label fw-semibold">Usuario</label>
                                                                                    <input type="text" class="form-control"
                                                                                           value="<c:out value="${u.nombre}" />" readonly />
                                                                                </div>

                                                                                <div class="mb-3">
                                                                                    <label class="form-label fw-semibold">Motivo <span class="text-danger">*</span></label>
                                                                                    <textarea name="motivo" class="form-control" rows="3"
                                                                                              required
                                                                                              placeholder="Ej. Infracciones repetidas..."></textarea>
                                                                                </div>

                                                                                <div class="mb-3">
                                                                                    <label class="form-label fw-semibold">Duración (días) <span class="text-danger">*</span></label>
                                                                                    <input type="number" name="dias" class="form-control"
                                                                                           min="1" required value="7" />
                                                                                </div>

                                                                            </div>
                                                                        <div class="modal-footer">
                                                                            <button type="button" class="btn btn-secondary btn-sm"
                                                                                    data-bs-dismiss="modal">
                                                                                Cancelar
                                                                            </button>
                                                                            <button type="submit" class="btn btn-warning btn-sm">
                                                                                Sí, suspender
                                                                            </button>
                                                                            </form>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <!-- Usuario suspendido: botón Reactivar -->
                                                            <button type="button"
                                                                    class="btn btn-outline-success btn-sm"
                                                                    data-bs-toggle="modal"
                                                                    data-bs-target="#modalReactivar${u.id}">
                                                                Reactivar cuenta
                                                            </button>

                                                            <div class="modal fade" id="modalReactivar${u.id}"
                                                                 tabindex="-1" aria-labelledby="modalReactivar${u.id}Label"
                                                                 aria-hidden="true">
                                                                <div class="modal-dialog modal-dialog-centered">
                                                                    <div class="modal-content">
                                                                        <div class="modal-header">
                                                                            <h5 class="modal-title fs-6" id="modalReactivar${u.id}Label">
                                                                                Reactivar cuenta
                                                                            </h5>
                                                                            <button type="button" class="btn-close"
                                                                                    data-bs-dismiss="modal"
                                                                                    aria-label="Cerrar"></button>
                                                                        </div>
                                                                        <div class="modal-body">
                                                                            <form action="${pageContext.request.contextPath}/gestionUsuarios"
                                                                                    method="post">
                                                                                <input type="hidden" name="accion" value="reactivar" />
                                                                                <input type="hidden" name="usuarioId" value="${u.id}" />

                                                                                ¿Estás seguro de que querés reactivar la cuenta de
                                                                                <strong><c:out value="${u.nombre}" /></strong>?
                                                                                <br><br>
                                                                                El usuario podrá volver a iniciar sesión
                                                                                y recibirá una notificación.
                                                                            </div>
                                                                        <div class="modal-footer">
                                                                            <button type="button" class="btn btn-secondary btn-sm"
                                                                                    data-bs-dismiss="modal">
                                                                                Cancelar
                                                                            </button>
                                                                            <button type="submit" class="btn btn-success btn-sm">
                                                                                Sí, reactivar
                                                                            </button>
                                                                            </form>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:if>
                    </div>
                </section>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />
