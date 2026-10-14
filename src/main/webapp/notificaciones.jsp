<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<h1 class="mb-4">Notificaciones</h1>


<form method="get" action="${pageContext.request.contextPath}/notificaciones"
      class="row g-2 align-items-end mb-3">

    <div class="col-md-4">
        <label for="tipo" class="form-label">Tipo</label>
        <select id="tipo" name="tipo" class="form-select">
            <option value="">Todos</option>
            <c:forEach var="t" items="${tipos}">
                <option value="${t}" ${filtroTipo == t.name() ? 'selected' : ''}>${t}</option>
            </c:forEach>
        </select>
    </div>

    <div class="col-md-4">
        <label for="orden" class="form-label">Orden</label>
        <select id="orden" name="orden" class="form-select">
            <option value="desc" ${filtroOrden != 'asc' ? 'selected' : ''}>Más recientes primero</option>
            <option value="asc" ${filtroOrden == 'asc' ? 'selected' : ''}>Más antiguas primero</option>
        </select>
    </div>

    <div class="col-md-4 d-flex gap-2">
        <button type="submit" class="btn btn-primary">Buscar</button>
        <a href="${pageContext.request.contextPath}/notificaciones" class="btn btn-outline-secondary">Limpiar</a>
    </div>
</form>


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
            <c:when test="${not empty filtroTipo}">No tenés notificaciones de ese tipo.</c:when>
            <c:otherwise>No tenés notificaciones.</c:otherwise>
        </c:choose>
    </div>
</c:if>

<c:if test="${not empty listaNotificaciones}">
    <p class="text-muted small">
        ${listaNotificaciones.size()} notificaciones ·
        Orden: ${filtroOrden == 'asc' ? 'más antiguas primero' : 'más recientes primero'}
    </p>

    <table class="table table-striped align-middle">
        <thead>
            <tr>
                <th scope="col">Fecha</th>
                <th scope="col">Tipo</th>
                <th scope="col">Mensaje</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="notificacion" items="${listaNotificaciones}">
                <tr>
                    <td><c:out value="${notificacion.fecha}" /></td>
                    <td><span class="badge text-bg-secondary"><c:out value="${notificacion.tipo}" /></span></td>
                    <td><c:out value="${notificacion.mensaje}" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

<jsp:include page="/layout/footer.jsp" />