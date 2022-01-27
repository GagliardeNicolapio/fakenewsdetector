<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="Fake News Detector"/>
        <jsp:param name="styles" value="training.css"/>
    </jsp:include>
</head>
<header>
    <%@include file="../partials/header.jsp"%>
</header>
<body>

    <div class="container-sm grid-y justify-evenly align-center" style="height: 23vh; width: auto; background-color: white">
        <form action="trainingModel" method="post">
            <button type="submit" name="training" title="Inizia addestramento" class="btn btn-primary">
                Inizia addestramento
            </button>
        </form>
        <p>
            Per addestrare i modelli deve essere presente il file FakeAndTrueRandomWithCovidTest.arff nella cartella C:\Program Files\Apache Software Foundation\Tomcat 9.0\dataset\
            <br>Per la conversione CSV to arff si rimanda al sito <a href="https://ikuz.eu/csv2arff/" target="_blank">ikuz.eu</a>
            <br>Weka separa correttamente gli attributi solo se il carattere di separazione è la virgola.

        </p>
    </div>

</body>
</html>
