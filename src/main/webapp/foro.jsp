<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <h1 class="mb-4">Foro</h1>

            <form method="get"
                  action="${pageContext.request.contextPath}/foro"
                  class="card shadow-sm mb-4">
                <div class="card-body">
                    <input type="hidden" name="enviado" value="1">

                    <div class="row g-3 align-items-end">
                        <div class="col-md-6">
                            <label for="q" class="form-label">Buscar dudas</label>
                            <input type="text" id="q" name="q" class="form-control"
                                   maxlength="200"
                                   placeholder="Palabras clave del mensaje"
                                   value="<c:out value='${q}' />">
                        </div>

                        <div class="col-md-4">
                            <label for="categoria" class="form-label">Categoría</label>
                            <select id="categoria" name="categoria" class="form-select">
                                <option value="">Todas</option>
                                <c:forEach var="opcion" items="${categoriasDisponibles}">
                                    <option value="${opcion}"
                                            ${categoria == opcion.name() ? 'selected' : ''}>
                                        <c:out value="${opcion}" />
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-md-2 d-flex gap-2">
                            <button type="submit" class="btn btn-outline-secondaryclass="btn btn-primary">Buscar dudas</button>">Limpiar filtros</a>
                        </div>
                    </div>

                    <a href="${pageContext.request.contextPath}/foro"
                       class="btn btn-outline-secondary btn-sm mt-3">
                        Limpiar búsqueda
                    </a>
                </div>
            </form>

            <h2 class="h5 mb-3">
                <c:choose>
                    <c:when test="${busco}">Resultados de la búsqueda</c:when>
                    <c:otherwise>Dudas recientes de mis materias</c:otherwise>
                </c:choose>
            </h2>

            <c:choose>
                <c:when test="${empty resultados}">
                    <div class="alert alert-info">
                        No se encontraron dudas que coincidan con la búsqueda.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="d-flex flex-column gap-3">
                        <c:forEach var="duda" items="${resultados}">
                            <div class="card shadow-sm">
                                <div class="card-body">

                                    <div class="d-flex flex-wrap gap-2 mb-2">
                                        <span class="badge text-bg-primary">
                                            <c:out value="${duda.curso}" />
                                        </span>
                                        <span class="badge text-bg-info">
                                            <c:out value="${duda.categoria}" />
                                        </span>
                                        <span class="badge ${duda.estado eq 'Abierta' ? 'text-bg-warning' : 'text-bg-success'}">
                                            <c:out value="${duda.estado}" />
                                        </span>
                                    </div>

                                    <p class="mb-3">
                                        <c:out value="${duda.mensaje}" />
                                    </p>

                                    <p class="text-muted small mb-0">
                                        Por <c:out value="${empty duda.autor ? 'Usuario eliminado' : duda.autor}" />
                                        · Fecha: <c:out value="${duda.fechaPublicacion}" />
                                    </p>

                                    <div class="mt-3">
                                        <a href="${pageContext.request.contextPath}/duda?id=${duda.id}"
                                           class="btn btn-sm btn-outline-primary">
                                            ${duda.estado eq 'Abierta' ? 'Ver duda y responder' : 'Ver duda'}
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />
