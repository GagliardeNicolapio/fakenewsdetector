<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script src="${initParam['jqueryPath']}" defer></script>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="Scegli testo"/>
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
            <p>Scegli il testo da analizzare cliccando sui blocchi.</p>
            <p class="card-text">
            <form method="post" action="feedback">
                ${bodyHTML}
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
<script>

</script>
</body>
</html>
