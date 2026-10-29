<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ocultarMenu" value="true" scope="request"/>

<jsp:include page="/layout/header.jsp" />
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">

                <h1 class="text-center mb-2">Nueva contraseña</h1>
                <p class="text-center text-muted mb-4">
                    Cuenta: <strong><c:out value="${correoRecuperar}" /></strong>
                </p>

                <c:if test="${not empty errorRecuperar}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${errorRecuperar}" />
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/recuperar" method="post">
                    <input type="hidden" name="accion" value="cambiar">

                    <div class="mb-3">
                        <label for="nuevaPassword" class="form-label">Contraseña nueva</label>
                        <input type="password" class="form-control" id="nuevaPassword"
                               name="nuevaPassword" autocomplete="new-password" required>
                    </div>

                    <div class="mb-3">
                        <label for="repetirPassword" class="form-label">Repetir contraseña</label>
                        <input type="password" class="form-control" id="repetirPassword"
                               name="repetirPassword" autocomplete="new-password" required>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">Guardar contraseña</button>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary">Cancelar</a>
                    </div>
                </form>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp"/>