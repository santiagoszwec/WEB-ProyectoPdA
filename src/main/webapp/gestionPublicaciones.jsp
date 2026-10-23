<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-4">
                <h1 class="mb-0">Gestión de publicaciones</h1>
                <a href="${pageContext.request.contextPath}/inicio"
                   class="btn btn-outline-secondary">
                    Volver al inicio
                </a>
            </div>

            <c:if test="${not empty mensajeExito}">
                <div class="alert alert-success" role="alert">
                    <c:out value="${mensajeExito}" />
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty publicaciones}">
                    <div class="alert alert-info">No hay publicaciones en el sistema.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-striped align-middle">
                            <thead>
                                <tr>
                                    <th scope="col">ID</th>
                                    <th scope="col">Tipo</th>
                                    <th scope="col">Curso</th>
                                    <th scope="col">Autor</th>
                                    <th scope="col">Mensaje</th>
                                    <th scope="col">Fecha</th>
                                    <th scope="col">Estado</th>
                                    <th scope="col" class="text-end">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="publicacion" items="${publicaciones}">
                                    <tr>
                                        <td><c:out value="${publicacion.id}" /></td>
                                        <td>
                                            <span class="badge text-bg-dark">
                                                <c:out value="${publicacion.tipo}" />
                                            </span>
                                        </td>
                                        <td><c:out value="${publicacion.curso}" /></td>
                                        <td>
                                            <c:out value="${empty publicacion.autor ? 'Usuario eliminado' : publicacion.autor}" />
                                        </td>
                                        <td style="max-width: 320px;">
                                            <c:out value="${publicacion.mensaje}" />
                                        </td>
                                        <td><c:out value="${publicacion.fechaPublicacion}" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${publicacion.activa}">
                                                    <span class="badge text-bg-success">Activa</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge text-bg-danger">Dada de baja</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-end">
                                            <a href="${pageContext.request.contextPath}/publicacion/editar?id=${publicacion.id}"
                                               class="btn btn-sm btn-outline-primary">
                                                Editar publicación
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />
