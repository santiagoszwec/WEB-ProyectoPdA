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

                <form action="${pageContext.request.contextPath}/login"
                      method="post">

                    <div class="mb-3">
                        <label for="correo" class="form-label">
                            Correo electrónico
                        </label>

                        <input type="email"
                               class="form-control"
                               id="correo"
                               name="correo"
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
                               required>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit"
                                class="btn btn-primary">
                            Iniciar sesión
                        </button>

                        <a href="${pageContext.request.contextPath}/registro"
                           class="btn btn-outline-secondary">
                            Registrarse
                        </a>
                    </div>

                </form>

            </div>
        </div>
    </div>

</div>

<jsp:include page="/layout/footer.jsp"/>
