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
        <form action="csv2arff" method="post" enctype="multipart/form-data">
            <p>Converti un file CSV in ARFF</p>
            <input type="file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" name="csvFile" alt="Input CSV" title="Scegli un file csv" placeholder="CSV File">
            <button type="submit" name="submitCSV" title="Invia file">Invia</button>
        </form>
        <p>
            Il file CSV deve essere formattato nel formato corretto per la conversione eliminando i caratteri che creano conflitti
            come il carattere \t, @, % e altri che potrebbero influire sulla corretta conversione.
            Weka separa correttamente gli attributi solo se il carattere di separazione è la virgola.
        </p>
    </div>

</body>
</html>
