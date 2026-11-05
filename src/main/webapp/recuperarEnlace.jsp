<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ocultarMenu" value="true" scope="request"/>

<jsp:include page="/layout/header.jsp" />
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">

                <h1 class="text-center mb-3">Revisá tu correo</h1>
                <p class="text-center">
                    Si <strong><c:out value="${correoRecuperar}" /></strong> está registrado,
                    te enviamos un enlace para elegir una contraseña nueva. Vence en 30 minutos
                    y solo se puede usar una vez.
                </p>
                <p class="text-center text-muted small">
                    Si no lo ves, mirá la carpeta de spam.
                </p>

                <div class="d-grid">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary">Volver al inicio de sesión</a>
                </div>

            </div>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp"/>