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
    <div class="container-sm grid-x justify-center align-center" style="height: 23vh; width: auto">
        <c:if test="${not empty alert}">
            <%@include file="../partials/alert.jsp"%>
        </c:if>
    </div>
    <div class="container-sm grid-y justify-center align-center"
         style="background-color: white;
                border-radius: 10px;
                width: 450px;
                padding: 10px">
        <p id="loginLabel">Accedi</p>
        <div class="grid-y justify-center align-center" id="formContainer">
            <form class="grid-y justify-center align-center" id="loginForm" action="login" method="post">
                <div class="grid-inline">
                    <label for="password" style="margin-right: 5px">Password: </label>
                    <input type="password" id="password" name="password" alt="Password" title="Password">
                    <button class="btn btn-secondary" id="button-addon2" type="submit" name="submit" title="Invia">
                        Invia
                    </button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
