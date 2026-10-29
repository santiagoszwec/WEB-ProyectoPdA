<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ocultarMenu" value="true" scope="request"/>

<jsp:include page="/layout/header.jsp" />
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">

                <h1 class="text-center mb-4">
                    Iniciar sesión
                </h1>

                <c:if test="${param.registro == 'ok'}">
                    <div class="alert alert-success" role="alert">
                        Cuenta creada correctamente. Ya podés iniciar sesión.
                    </div>
                </c:if>

                 <c:if test="${param.recuperada == 'ok'}">
                    <div class="alert alert-success" role="alert">
                        Contraseña actualizada. Ya podés iniciar sesión con la nueva.
                    </div>
                 </c:if>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <%-- ★ CAMBIO 1: se borró la alerta amarilla "¿Olvidaste tu contraseña? / Sí, quiero recuperarla" --%>

                <form action="${pageContext.request.contextPath}/login"
                      method="post">

                    <div class="mb-3">
                        <label for="correo" class="form-label">
                            Correo electrónico
                        </label>

                        <input type="email"
                               class="form-control"
                               id="correo"
                               placeholder="nombre@estudiantes.utec.edu.uy"
                               autocomplete="email"
                               name="correo"
                               value="<c:out value='${correo}'/>"
                               required>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label">
                            Contraseña
                        </label>

                        <input type="password"
                               class="form-control"
                               id="password"
                               name="password"
                               autocomplete="current-password"
                               required>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit"
                                class="btn btn-primary">
                            Iniciar sesión
                        </button>

                        <%-- ★ CAMBIO 2 (NUEVO): link "¿Olvidaste tu contraseña?", solo desde el 2º fallo seguido --%>
                        <c:if test="${ofrecerRecuperar}">
                            <button type="submit" form="formRecuperar"
                                    class="btn btn-link text-decoration-none">
                                ¿Olvidaste tu contraseña?
                            </button>
                        </c:if>

                        <hr class="my-1">

                        <a href="${pageContext.request.contextPath}/registro"
                           class="btn btn-outline-secondary">
                            Crear una cuenta
                        </a>
                    </div>

                </form>

                <%-- ★ CAMBIO 3 (NUEVO): formulario oculto que dispara la recuperación (paso 2 del caso de uso) --%>
                <c:if test="${ofrecerRecuperar}">
                    <form id="formRecuperar"
                          action="${pageContext.request.contextPath}/recuperar"
                          method="post" class="d-none">
                        <input type="hidden" name="accion" value="confirmar">
                    </form>
                </c:if>

            </div>
        </div>
    </div>

</div>

<jsp:include page="/layout/footer.jsp"/>