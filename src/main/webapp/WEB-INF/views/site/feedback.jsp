<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script src="${initParam['jqueryPath']}" defer></script>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="Feedback"/>
        <jsp:param name="styles" value="feedback.css"/>
        <jsp:param name="script" value="feedback.js,preloader.js"/>
    </jsp:include>

</head>

<body>
    <header>
        <jsp:include page="../partials/header.jsp">
            <jsp:param name="link" value="true"/>
        </jsp:include>
    </header>
    <div id="containerFeedback">
        <div class="row justify-center">
            <div class="cardFeedback">
                <h4>Naive Bayes</h4>
                <c:choose>
                    <c:when test="${naivePrediction == 'fake'}">
                        <img src="./images/icon/dislike.png" style="width: 20%;">
                        <div>Naive bayes ha predetto:
                            <span style="color: red">fake</span>
                        </div>
                    </c:when>
                    <c:when test="${naivePrediction == 'true'}">
                        <img src="./images/icon/like.png" style="width: 20%;">
                        <div>Naive bayes ha predetto:
                            <span style="color: green">true</span>
                        </div>
                    </c:when>
                </c:choose>
            </div>

            <div class="cardFeedback" style="margin-right: 0px;">
                <h4>J48</h4>
                <c:choose>
                    <c:when test="${treePrediction == 'fake'}">
                        <img src="./images/icon/dislike.png" style="width: 20%;">
                        <div>J48 ha predetto:
                            <span style="color: red">fake</span>
                        </div>
                    </c:when>
                    <c:when test="${treePrediction == 'true'}">
                        <img src="./images/icon/like.png" style="width: 20%;">
                        <div>J48 ha predetto:
                            <span style="color: green">true</span>
                        </div>
                    </c:when>
                </c:choose>
            </div>
        </div>

        <div class="card">
            <div class="card-body">
                <h5 class="card-title">News analizzata</h5>
                <p class="card-text">
                    <b>Titolo analizzato:</b> ${titoloNews}
                    <br><br>
                    <b>Testo analizzato:</b> ${testoAnalizzato}
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
