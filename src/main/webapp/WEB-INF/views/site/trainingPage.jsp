<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="Fake News Detector"/>
        <jsp:param name="styles" value="training.css"/>
        <jsp:param name="script" value="training.js,alert.js"/>
    </jsp:include>
</head>
<header>
    <%@include file="../partials/header.jsp"%>
</header>
<body>

    <div class="container-sm" style="width: auto; padding: 10px; background-color: white">
        <div class="grid-y justify-start align-center">
            <p class="marginContainer" style="font-size: 18px">
                L'addestramento prevede la creazione di due diversi modelli, un modello di classificazione basato sull'algoritmo NaiveBayes ed un secondo modello
                di classificazione basato sull'algoritmo J48 che restituisce in output un albero decisionale, in modo da confrontare le prestazioni dei modelli.
                <br>Per la creazione dei modelli è necessario inserire un dataset in formato ARFF, specificare i parametri delle valutazioni che si desiderano ricevere e
                specificare se salvare o meno i modelli finali per poterli utilizzare.
            </p>
            <div class="container-sm grid-x justify-center align-center">
                <c:if test="${not empty alert}">
                    <%@include file="../partials/alert.jsp"%>
                </c:if>
            </div>
            <div class="marginContainer grid-y justify-start align-center" style="font-size: 18px; margin-top: 0">
                <p>
                    Si ricorda che Weka separa correttamente gli attributi solo se il carattere di separazione è la virgola.
                    <br>Il file convertito dovrà essere modificato con un qualsiasi editor di testo modificando il tipo degli attributi,
                    per le colonne che contengono il titolo ed il testo, il tipo dovrà essere string.
                    <br>Il file sarà salvato nella directory di Tomcat nella cartella "dataset".
                </p>
                <form class="grid-y justify-start align-center" action="csv2arff" method="post" enctype="multipart/form-data">
                    <label for="csvtoarff">Converti un file CSV in ARFF</label>
                    <div style="margin: 5px;">
                        <input type="file" id="csvtoarff" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" name="csvFile" alt="Input CSV" title="Scegli un file csv" placeholder="CSV File">
                        <button class="btn btn-primary" style="margin-top: 0" type="submit" name="submitCSV" title="Invia file">Invia</button>
                    </div>
                </form>
            </div>
        </div>


        <div class="marginContainer borderDiv">
            <form action="trainingModel" method="post" enctype="multipart/form-data" class="marginContainer grid-y justify-start align-center">
                <p style="font-size: 18px">
                    Il tempo di addestramento dipende dalle valutazioni selezionate e dal numero di parole che si desiderano utilizzare.
                    <br>Per selezionare il dataset, il file .arff dovrà essere inserito nel seguente percorso: /Tomcat 9.0/dataset/fileName.arff
                </p>
                <div class="marginContainer grid-y justify-start align-center" id="newArff">
                    <label for="fileArff">Seleziona un dataset ARFF: </label><br>
                    <select name="fileArff" id="fileArff">
                        <c:forEach items="${fileList}" var="file">
                            <option value="${file}">${file}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="marginContainer">
                    <label for="wordToKeep">Inserire il numero di parole da utilizzare (e.g 1000):</label><br>
                    <input type="text" id="wordToKeep" name="wordToKeep">
                </div>

                <div class="marginContainer" style="margin-top: 20px">
                    <p style="margin-bottom: 5px">
                        Selezionare le valutazioni da effettuare:
                    </p>
                    <input type="checkbox" id="j48Split" name="j48Split" value="j48Split">
                    <label for="j48Split">J48 Percentage Split Evaluation</label><br>
                    <input type="checkbox" id="naiveSplit" name="naiveSplit" value="naiveSplit">
                    <label for="naiveSplit">NaiveBayes Percentage Split Evaluation</label><br>
                    <input type="checkbox" id="naiveCross" name="naiveCross" value="naiveCross">
                    <label for="naiveCross">NaiveBayes K-Folds Cross Validation</label><br>
                    <input type="checkbox" id="naiveNtimesCV" name="naiveNtimesCV" value="naiveNtimesCV">
                    <label for="naiveNtimesCV">NaiveBayes N-Times K-Folds Cross Validation Stratified</label><br>
                </div>

                <div class="marginContainer borderDiv grid-y justify-start align-center evalDiv" id="j48Eval" style="display: none">
                    <b>J48 Percentage Split Evaluation</b><br><br>
                    <label for="percentagej48" style="margin-top: 5px">Inserire la percentuale di split (e.g 66):</label>
                    <input type="text" id="percentagej48" name="percentagej48" style="margin: 5px; width: 60px">
                </div>

                <div class="marginContainer borderDiv grid-y justify-start align-center evalDiv" id="NaiveEval" style="display: none">
                    <b>NaiveBayes Percentage Split Evaluation</b><br><br>
                    <label for="percentageNaive" style="margin-top: 5px">Inserire la percentuale di split (e.g 66):</label>
                    <input type="text" id="percentageNaive" name="percentageNaive" style="margin: 5px; width: 60px">
                </div>

                <div class="marginContainer borderDiv grid-y justify-start align-center evalDiv" id="NaiveKfold" style="display: none">
                    <b>NaiveBayes K-Folds Cross Validation</b><br><br>
                    <label for="kFoldNaive" style="margin-top: 5px">Specificare K:</label>
                    <input type="text" id="kFoldNaive" name="kFoldNaive" style="margin: 5px; width: 60px">
                </div>

                <div class="marginContainer borderDiv grid-y justify-start align-center evalDiv" id="NaiveNtimes" style="display: none">
                    <b>NaiveBayes N-Times K-Folds Cross Validation Stratified</b><br><br>
                    <label for="nTimesNaive" style="margin-top: 5px">Specificare N:</label>
                    <input type="text" id="nTimesNaive" name="nTimesNaive" style="margin: 5px; width: 60px">
                    <label for="kFoldNaive2" style="margin-top: 5px">Specificare K:</label>
                    <input type="text" id="kFoldNaive2" name="kFoldNaive2" style="margin: 5px; width: 60px">
                </div>

                <div style="margin: 20px">
                    <input type="checkbox" id="save" name="save" value="save">
                    <label for="save">Salva i modelli</label><br>
                </div>

                <button type="submit" name="training" title="Inizia addestramento" class="btn btn-primary">
                    Inizia addestramento
                </button>
            </form>
        </div>
    </div>
</body>
</html>
