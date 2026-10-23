<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">
            <h1 class="mb-4">Notificaciones</h1>
            <form method="get"
                  action="${pageContext.request.contextPath}/notificaciones"
                  class="row g-3 align-items-end mb-3">

                                <div class="col-md-4">
                    <label for="tipo" class="form-label">Tipo</label>
                    <select id="tipo" name="tipo" class="form-select">
                        <option value="">Todos</option>
                        <c:forEach var="t" items="${tipos}">
                            <option value="${t}"
                                ${filtroTipo == t.name() ? 'selected' : ''}>
                                <c:out value="${t}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                                <div class="col-md-4">
                    <label for="orden" class="form-label">Orden</label>
                    <select id="orden" name="orden" class="form-select">
                        <option value="desc"
                        ${filtroOrden != 'asc' ? 'selected' : ''}>
                            Más recientes primero
                        </option>
                        <option value="asc"
                        ${filtroOrden == 'asc' ? 'selected' : ''}>
                            Más antiguas primero
                        </option>
                    </select>
                </div>

                <div class="col-md-2 d-flex gap-2">
                    <button type="submit" class="btn btn-primary">
                        Aplicar filtros
                    </button>
                    <a href="${pageContext.request.contextPath}/notificaciones"
                       class="btn btn-outline-secondary">
                        Limpiar filtros
                    </a>
                </div>
            </form>
            <c:if test="${not empty notificacionMensaje}">
                <div class="alert alert-success" role="alert">
                    <c:out value="${notificacionMensaje}" />
                </div>
            </c:if>

            <c:if test="${not empty notificacionError}">
                <div class="alert alert-danger" role="alert">
                    <c:out value="${notificacionError}" />
                </div>
            </c:if>

            <c:if test="${not empty aviso}">
                <div class="alert alert-warning" role="alert">
                    <c:out value="${aviso}" />
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <c:out value="${error}" />
                </div>
            </c:if>

            <c:if test="${empty error and empty listaNotificaciones}">
                <div class="alert alert-info">
                    <c:choose>
                        <c:when test="${not empty filtroTipo}">
                            No tenés notificaciones de ese tipo.
                        </c:when>
                        <c:otherwise>
                            No tenés notificaciones.
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
            <c:if test="${not empty listaNotificaciones}">

                <p class="text-muted small">
                        ${listaNotificaciones.size()} notificaciones ·
                    Orden:
                        ${filtroOrden == 'asc'
                                ? 'más antiguas primero'
                                : 'más recientes primero'}
                </p>

                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead>
                        <tr>
                            <th scope="col">Fecha</th>
                            <th scope="col">Tipo</th>
                            <th scope="col">Mensaje</th>
                            <th scope="col" class="text-end">Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="notificacion"
                                   items="${listaNotificaciones}">
                            <tr>
                                <td>
                                    <c:out value="${notificacion.fecha}" />
                                </td>
                                <td>
                                        <span class="badge text-bg-secondary">
                                            <c:out value="${notificacion.tipo}" />
                                        </span>
                                </td>
                                                               <td>
                                                                   <c:choose>
                                                                       <c:when test="${notificacion.publicacionActiva and notificacion.esDuda}">
                                                                           <a href="${pageContext.request.contextPath}/duda?id=${notificacion.publicacionId}">
                                                                               <c:out value="${notificacion.mensaje}" />
                                                                           </a>
                                                                       </c:when>
                                                                       <c:when test="${notificacion.publicacionActiva and not empty notificacion.cursoId}">
                                                                           <a href="${pageContext.request.contextPath}/curso?id=${notificacion.cursoId}#publicacion-${notificacion.publicacionId}">
                                                                               <c:out value="${notificacion.mensaje}" />
                                                                           </a>
                                                                       </c:when>
                                                                       <c:otherwise>
                                                                           <c:out value="${notificacion.mensaje}" />
                                                                       </c:otherwise>
                                                                   </c:choose>
                                                               </td>
                                     <td class="text-end">
                                        <form method="post"
                                             action="${pageContext.request.contextPath}/notificaciones"
                                                 class="d-inline">
                                                  <input type="hidden" name="id" value="${notificacion.id}">
                                                  <button type="submit" class="btn btn-sm btn-outline-success">
                                                    Marcar como leída
                                                     </button>
                                           </form>
                                        </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />