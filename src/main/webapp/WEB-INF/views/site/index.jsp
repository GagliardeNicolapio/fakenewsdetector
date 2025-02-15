<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="Fake News Detector"/>
        <jsp:param name="styles" value="index.css,feedback.css"/>
        <jsp:param name="script" value="preloader.js,alert.js"/>
    </jsp:include>
    <style>
        ul{
            list-style: none;
            padding: 0;
        }
    </style>
</head>
<header>
    <%@include file="../partials/header.jsp"%>
</header>
<body>
    <div class="grid-y justify-center align-center container-sm mainContent" id="homeContainer">
        <div class="container-sm grid-x justify-center align-center" style="height: 23vh; width: auto">
            <c:if test="${not empty alert}">
                <%@include file="../partials/alert.jsp"%>
            </c:if>
        </div>
        <div class="input-group mb-3">
            <p>Inserisci il link della news</p>
            <form class="grid-inline" method="post" action="scegliTitolo" style="width: 100%">
                <input type="text" class="form-control" placeholder="News Link" name="link"
                       aria-label="Recipient's username" aria-describedby="button-addon2">
                <button class="btn btn-secondary" type="submit" id="button-addon2"  onclick="startPreloader()" >Invia</button>
            </form>
       </div>

        <p>Oppure</p>
        <form action="feedback" method="post" style="width: 50%; color: white;">
            <input type="hidden" name="flagCopyPaste" value="1">
            <div class="row mb-3">
                <label for="titoloNews" class="col-sm-2 col-form-label">Titolo della news</label>
                <div class="col-sm-10">
                    <input type="text" placeholder="Incolla qui il titolo della news" class="form-control" id="titoloNews" name="titoloNews" required>
                </div>
            </div>
            <div class="row mb-3">
                <label for="textNews" class="col-sm-2 col-form-label">Testo della news</label>
                <div class="col-sm-10">
                    <textarea name="textNews" class="form-control" id="textNews" rows="10" placeholder="Incolla qui il testo della news" required></textarea>

                </div>
            </div>

            <button type="submit" class="btn btn-primary">Analizza</button>
        </form>

        <div class="infoArea container-sm" style="text-align: center;">
            <h2>FakeNewsDetector</h2>
            <div>FakeNewsDetector è un classificatore di fake news,
                utilizzando il machine learining è in grado di analizzare il testo di una news e
                indicare la veridicità della notizia. Verranno utilizzati due algoritmi: Naive Bayes e J48, quindi nella pagina
            feedback verranno forniti due risultati.</div>
            <hr>
            <h2>J48</h2>
            <ul>
                <li>Accuracy: 68%</li>
                <li>Precision: 79%</li>
                <li>Recall: 68%</li>
            </ul>
            <hr>
            <h2>Naive Bayes</h2>
            <ul>
                <li>Accuracy: 68%</li>
                <li>Precision: 73%</li>
                <li>Recall: 68%</li>
            </ul>
            <hr>
            <h2>Autori</h2>
            <ul>
                <li>Carmine Leo - <a href="https://github.com/ilgrafico" target="_blank">ilgrafico</a></li>
                <li>Gagliarde Nicolapio - <a href="https://github.com/GagliardeNicolapio" target="_blank">GagliardeNicolapio</a></li>
            </ul>

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