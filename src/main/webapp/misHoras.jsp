<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/layout/header.jsp" />

<div class="container-fluid">
    <div class="row g-0">
        <jsp:include page="/layout/sidebar.jsp" />

        <div class="col-md-9 col-lg-10 p-4">

            <h1 class="mb-4">Mis horas de estudio</h1>

            <c:if test="${not empty mensajeExito}">
                <div class="alert alert-success" role="alert">
                    <c:out value="${mensajeExito}" />
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty materias}">
                    <div class="alert alert-info">
                        No estás inscripto en ninguna materia con estado "Cursando":
                        no podés registrar horas de estudio por ahora.
                    </div>
                </c:when>

                <c:otherwise>

                    <%-- ★ NUEVO: formulario con el mismo estilo que Mi perfil --%>
                    <div class="card border-0 shadow-sm rounded-4 mb-4">
                        <div class="card-body">
                            <h2 class="h5 fw-bold mb-3 d-flex align-items-center gap-2">
                                <i class="bi bi-clock"></i> Registrar horas
                            </h2>

                            <c:if test="${not empty errores}">
                                <div class="alert alert-danger" role="alert">
                                    <ul class="mb-0">
                                        <c:forEach var="error" items="${errores}">
                                            <li><c:out value="${error}" /></li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/misHoras"
                                  method="post" novalidate class="row g-3">

                                <div class="col-md-4">
                                    <label for="materia" class="form-label fw-semibold">Materia</label>
                                    <select id="materia" name="materia" class="form-select" required>
                                        <c:forEach var="materia" items="${materias}">
                                            <option value="${materia.key}"
                                                    ${materia.key == materiaSeleccionada ? 'selected' : ''}>
                                                <c:out value="${materia.value}" />
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="col-md-4">
                                    <label for="fecha" class="form-label fw-semibold">Fecha</label>
                                    <input type="date" id="fecha" name="fecha"
                                           class="form-control" required
                                           max="${hoy}"
                                           value="<c:out value='${valores.fecha}' />">
                                </div>

                                <div class="col-md-4">
                                    <label for="horas" class="form-label fw-semibold">Horas estudiadas</label>
                                    <input type="number" id="horas" name="horas"
                                           class="form-control" required
                                           min="0.5" max="24" step="0.5"
                                           placeholder="Ej.: 2.5"
                                           value="<c:out value='${valores.horas}' />">
                                    <div class="form-text">De 0,5 a 24 horas, de a media hora.</div>
                                </div>

                                <div class="col-12">
                                    <button type="submit" class="btn btn-dark rounded-pill px-4 fw-semibold">
                                        Guardar registro
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <div class="row g-4 mb-4">
                        <div class="col-lg-6">
                            <%-- ★ NUEVO: mismo estilo que "Progreso de estudio" del perfil --%>
                            <div class="card border-0 shadow-sm rounded-4 h-100">
                                <div class="card-body">
                                    <h2 class="h5 fw-bold mb-3 d-flex align-items-center gap-2">
                                        <i class="bi bi-book"></i> Horas por materia
                                    </h2>

                                    <c:choose>
                                        <c:when test="${empty totales}">
                                            <p class="text-muted mb-0">Todavía no hay registros.</p>
                                        </c:when>
                                        <c:otherwise>
                                            <%-- La materia con más horas = barra completa; las demás, proporcionales --%>
                                            <c:set var="maxHoras" value="0" />
                                            <c:forEach var="total" items="${totales}">
                                                <c:if test="${total.totalHoras gt maxHoras}">
                                                    <c:set var="maxHoras" value="${total.totalHoras}" />
                                                </c:if>
                                            </c:forEach>

                                            <ul class="list-unstyled d-flex flex-column gap-3 mb-3">
                                                <c:forEach var="total" items="${totales}">
                                                    <c:set var="ancho"
                                                           value="${maxHoras gt 0 ? total.totalHoras * 100 / maxHoras : 0}" />
                                                    <li>
                                                        <div class="d-flex justify-content-between small mb-1">
                                                            <span class="fw-semibold"><c:out value="${total.curso}" /></span>
                                                            <span class="text-body-secondary">
                                                                <c:out value="${total.totalHoras}" /> h
                                                            </span>
                                                        </div>
                                                        <div class="progress" style="height: 8px;">
                                                            <div class="progress-bar"
                                                                 role="progressbar"
                                                                 style="width: ${ancho}%; background-color: #2f6db5;"
                                                                 aria-valuenow="${ancho}"
                                                                 aria-valuemin="0"
                                                                 aria-valuemax="100"></div>
                                                        </div>
                                                    </li>
                                                </c:forEach>
                                            </ul>

                                            <div class="d-flex justify-content-between align-items-center border-top pt-3">
                                                <span class="fw-semibold">Total general</span>
                                                <span class="badge text-bg-dark rounded-pill fs-6">
                                                    <c:out value="${totalGeneral}" /> h
                                                </span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-6">
                            <div class="card shadow-sm h-100">
                                <div class="card-body">
                                    <h2 class="h5 mb-3">Estadísticas de estudio</h2>
                                    <canvas id="graficoTotales" height="220"
                                            aria-label="Gráfico de horas por materia"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card shadow-sm mb-4">
                        <div class="card-body">
                            <h2 class="h5 mb-3">Evolución de las horas estudiadas</h2>
                            <canvas id="graficoSerie" height="120"
                                    aria-label="Gráfico de evolución de horas estudiadas"></canvas>
                        </div>
                    </div>

                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="h5 mb-3">Mis registros</h2>

                            <c:choose>
                                <c:when test="${empty registros}">
                                    <div class="alert alert-info mb-0">
                                        Todavía no registraste horas de estudio.
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table align-middle mb-0">
                                            <thead>
                                                <tr>
                                                    <th scope="col">Fecha</th>
                                                    <th scope="col">Materia</th>
                                                    <th scope="col" class="text-end">Horas</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="item" items="${registros}">
                                                    <tr>
                                                        <td><c:out value="${item.registro.fecha}" /></td>
                                                        <td><c:out value="${item.curso}" /></td>
                                                        <td class="text-end">
                                                            <c:out value="${item.registro.cantidadDeHoras}" />
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

                    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
                    <script>
                        (function () {
                            var totales = ${totalesChart};
                            var serie = ${serieChart};

                            var ctxTotales = document.getElementById("graficoTotales");
                            if (ctxTotales) {
                                new Chart(ctxTotales, {
                                    type: "bar",
                                    data: {
                                        labels: totales.labels,
                                        datasets: [{
                                            label: "Horas estudiadas",
                                            data: totales.data,
                                            backgroundColor: "#0B1F3A"
                                        }]
                                    },
                                    options: {
                                        plugins: { legend: { display: false } },
                                        scales: { y: { beginAtZero: true } }
                                    }
                                });
                            }

                            var ctxSerie = document.getElementById("graficoSerie");
                            if (ctxSerie) {
                                new Chart(ctxSerie, {
                                    type: "line",
                                    data: {
                                        labels: serie.labels,
                                        datasets: [{
                                            label: "Horas por día",
                                            data: serie.data,
                                            borderColor: "#0B1F3A",
                                            backgroundColor: "rgba(11, 31, 58, 0.15)",
                                            fill: true,
                                            tension: 0.2
                                        }]
                                    },
                                    options: {
                                        plugins: { legend: { display: false } },
                                        scales: { y: { beginAtZero: true } }
                                    }
                                });
                            }
                        })();
                    </script>

                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/layout/footer.jsp" />