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

                    <div class="card shadow-sm mb-4">
                        <div class="card-body">
                            <h2 class="h5 mb-3">Registrar horas</h2>

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
                                  method="post" novalidate>

                                <div class="row g-3 mb-3">
                                    <div class="col-md-4">
                                        <label for="materia" class="form-label">Materia</label>
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
                                        <label for="fecha" class="form-label">Fecha</label>
                                        <input type="date" id="fecha" name="fecha"
                                               class="form-control" required
                                               max="${hoy}"
                                               value="<c:out value='${valores.fecha}' />">
                                    </div>

                                    <div class="col-md-4">
                                        <label for="horas" class="form-label">Horas estudiadas</label>
                                        <input type="number" id="horas" name="horas"
                                               class="form-control" required
                                               min="0.5" max="24" step="0.5"
                                               placeholder="Ej.: 2.5"
                                               value="<c:out value='${valores.horas}' />">
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary">Guardar registro</button>
                            </form>
                        </div>
                    </div>

                    <div class="row g-4 mb-4">
                        <div class="col-lg-6">
                            <div class="card shadow-sm h-100">
                                <div class="card-body">
                                    <h2 class="h5 mb-3">Horas por materia</h2>

                                    <c:choose>
                                        <c:when test="${empty totales}">
                                            <p class="text-muted mb-0">Todavía no hay registros.</p>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="table-responsive">
                                                <table class="table table-sm align-middle mb-0">
                                                    <thead>
                                                        <tr>
                                                            <th scope="col">Materia</th>
                                                            <th scope="col" class="text-end">Horas</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="total" items="${totales}">
                                                            <tr>
                                                                <td><c:out value="${total.curso}" /></td>
                                                                <td class="text-end">
                                                                    <c:out value="${total.totalHoras}" />
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                    <tfoot>
                                                        <tr class="table-light fw-semibold">
                                                            <td>Total general</td>
                                                            <td class="text-end">
                                                                <c:out value="${totalGeneral}" />
                                                            </td>
                                                        </tr>
                                                    </tfoot>
                                                </table>
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
