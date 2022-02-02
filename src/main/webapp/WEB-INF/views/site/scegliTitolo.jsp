<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script src="${initParam['jqueryPath']}" defer></script>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="Scegli titolo"/>
        <jsp:param name="styles" value="feedback.css"/>
        <jsp:param name="script" value="feedback.js"/>
    </jsp:include>

    <!--per moficare lo pseudo elemento-->
    <style>
        <c:choose>
        <c:when test="${percentuale<=49}">
        .percent_more:after{
            background: red !important;
        }
        </c:when>
        <c:when test="${percentuale >= 90}">
        .percent_more:after{
            background: green !important;
        }
        </c:when>
        <c:otherwise>
        .percent_more:after{
            background: #ffdd55 !important;
        }
        </c:otherwise>
        </c:choose>

    </style>
</head>

<body>
<header>
    <jsp:include page="../partials/header.jsp">
        <jsp:param name="link" value="true"/>
    </jsp:include>
</header>
<div id="containerFeedback">
    <div class="card">
        <div class="card-body">
            <h5 class="card-title">Info</h5>
            <p><b>Selezionare soltanto il titolo della news cliccando su uno dei successivi blocchi e infine inviare il titolo selezionato.
                <br>In questo modo il sistema sarà in grado di identificare separatamente titolo e testo della notizia per una predizione più accurata.</b></p>
            <p style="display: inline;">Se non trovi il titolo o c'è qualche errore, incolla direttamente il titolo e il testo cliccando il tasto: </p>

            <!-- Button trigger modal -->
            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
                Incolla il testo
            </button>

            <!-- Modal -->
            <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLabel">Fake News Detector</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form method="post" action="feedback">
                                <input type="hidden" name="flagCopyPaste" value="1">
                                <div class="mb-3">
                                        <label for="titleNews" class="form-label">Titolo della news</label>
                                    <input type="text" name="titoloNews" class="form-control" id="titleNews" aria-describedby="emailHelp" placeholder="Incolla qui il titolo della news">

                                </div>
                                <div class="mb-3">
                                    <label for="textNews" class="form-label">Testo della news</label>
                                    <textarea name="textNews" class="form-control" id="textNews" placeholder="Incolla qui il testo della news"></textarea>

                                </div>

                                <button type="submit" class="btn btn-primary">Analizza</button>
                            </form>
                        </div>

                    </div>
                </div>
            </div>

            <p class="card-text">
            <form method="post" action="scegliTesto">
                ${textNews}
                <button type="submit" class="btn btn-primary">Analizza</button>
            </form>

            </p>
        </div>
    </div>
</div>


<div id="contenitoreProg" style="display:none;">
    <div class="h-100 row align-items-center">
        <div id="wrapper">
            <div>Stiamo analizzando la news. Attendi...</div>
            <div>
                <svg>
                    <circle cx="50" cy="50" r="40" stroke="red" stroke-dasharray="78.5 235.5" stroke-width="3" fill="none" />
                    <circle cx="50" cy="50" r="30" stroke="blue" stroke-dasharray="62.8 188.8" stroke-width="3" fill="none" />
                    <circle cx="50" cy="50" r="20" stroke="green" stroke-dasharray="47.1 141.3" stroke-width="3" fill="none" />
                </svg>
            </div>
        </div>
    </div>
</div>

</body>
</html>
