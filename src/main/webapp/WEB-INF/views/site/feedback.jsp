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
        <div class="conainer" style="text-align: center">
            <div class="circle_percent" data-percent="${percentuale}">
                <div class="circle_inner">
                    <div class="round_per"></div>
                </div>
            </div>
        </div>

        <div class="row">
            <ul class="legend col-4 mx-auto" style="width: auto; border-radius: 5px; border: 2px solid #eeeeee;  padding-bottom: 2px;">
                <li><img src="images/icon/triangle.svg">0-49</li>
                <li><img src="images/icon/square.svg">50-89</li>
                <li ><img src="images/icon/circle.svg">90-100</li>
                <li style="margin: 0;"  tooltip="Info sulla veridicità della fonte"><img src="images/icon/info.png"></li>

            </ul>
        </div>
        <div class="row">
                <c:if test="${naivePrediction == 'fake'}">
                    <span style="text-align: center;">Naive Bayes ha predetto: <span style="color: red">fake</span></span>
                </c:if>
                <c:if test="${treePrediction == 'fake'}">
                    <span style="text-align: center;">J48 ha predetto: <span style="color: red">fake</span></span>
                </c:if>
                <c:if test="${naivePrediction == 'true'}">
                    <span style="text-align: center;">Naive Bayes ha predetto: <span style="color: green">true</span></span>
                </c:if>
                <c:if test="${treePrediction == 'true'}">
                    <span style="text-align: center;">J48 ha predetto: <span style="color: green">true</span></span>
                </c:if>
        </div>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Info</h5>
                <p class="card-text">
                   Titolo analizzato: ${titoloNews}
                    <br>
                    Testo analizzato: ${testoAnalizzato}
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
