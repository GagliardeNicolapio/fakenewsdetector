<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="Fake News Detector"/>
        <jsp:param name="styles" value="training.css"/>
        <jsp:param name="script" value="alert.js"/>
    </jsp:include>
</head>
<header>
    <%@include file="../partials/header.jsp"%>
</header>
<body>

    <div class="container-sm" style="height: 100vh; width: auto; padding: 10px; background-color: white">
        <div class="container-sm grid-x justify-center align-center">
            <c:if test="${not empty alert}">
                <%@include file="../partials/alert.jsp"%>
            </c:if>
        </div>
        <c:if test="${not empty statJ48}">
            <div class="borderDiv marginContainer" style="padding: 10px">
                <pre>${statJ48}</pre>
            </div>
        </c:if>
        <c:if test="${not empty statNaiveSplit}">
            <div class="borderDiv marginContainer" style="padding: 10px">
                <pre>${statNaiveSplit}</pre>
            </div>
        </c:if>
        <c:if test="${not empty statNaiveCross}">
            <div class="borderDiv marginContainer" style="padding: 10px">
                <pre>${statNaiveCross}</pre>
            </div>
        </c:if>

        <c:forEach items="${statsNaiveNTimes}" var="item">
            <div class="borderDiv marginContainer" style="padding: 10px">
                <pre>${item}</pre>
            </div>
        </c:forEach>
    </div>

</body>
</html>
