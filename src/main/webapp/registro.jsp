<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ocultarMenu" value="true" scope="request"/>

<jsp:include page="/layout/header.jsp" />
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">

                <h1 class="text-center mb-4">Registrarse</h1>

                <c:if test="${not empty requestScope.error}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${requestScope.error}" />
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/registro"
                      method="post">

                    <div class="mb-3">
                        <label for="nombre" class="form-label">Nombre</label>
                        <input type="text" class="form-control"
                               id="nombre" name="nombre" required>
                    </div>

                    <div class="mb-3">
                        <label for="correo" class="form-label">Correo electrónico</label>
                        <input type="email" class="form-control"
                               id="correo" name="correo" required>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label">Contraseña</label>
                        <input type="password" class="form-control"
                               id="password" name="password" required>
                    </div>

                    <div class="mb-3">
                        <label for="anioDeGeneracion" class="form-label">Año de generación</label>
                        <input type="number" class="form-control"
                               id="anioDeGeneracion" name="anioDeGeneracion"
                               min="2008" max="2027" placeholder="Ej.: 2025" required>
                        <div class="form-text">Año en que ingresaste a la carrera.</div>
                    </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary">
                            Crear cuenta
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
