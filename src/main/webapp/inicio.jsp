
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="ocultarMenu" value="false" scope="request"/>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <div class="p-4 mb-4 bg-light rounded-3">
                <h1>Inicio</h1>
                <p class="mb-0">
                    Últimas publicaciones de tus cursos
                </p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <c:out value="${error}" />
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty publicaciones and empty error}">
                    <div class="alert alert-info">
                        Todavía no hay publicaciones en tus cursos
                    </div>
                </c:when>

                <c:otherwise>
                    <div>
                        <c:forEach var="publicacion" items="${publicaciones}">
                            <c:set var="pub" value="${publicacion}" scope="request"/>
                            <jsp:include page="/layout/publicacion.jsp"/>
                        </c:forEach>
                    </div>
                    <%-- ★ FIN NUEVO --%>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />