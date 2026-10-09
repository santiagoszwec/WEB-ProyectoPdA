<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/layout/header.jspf"/>

<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">

                <h1 class="text-center mb-4">Registrarse</h1>

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
<jsp:include page="/layout/footer.jspf"/>
