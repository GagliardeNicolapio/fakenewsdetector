<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="Fake News Detector"/>
        <jsp:param name="styles" value="index.css,feedback.css"/>
        <jsp:param name="script" value="preloader.js"/>
    </jsp:include>
</head>
<header>
    <%@include file="../partials/header.jsp"%>
</header>
<body>
    <div class="grid-y justify-center align-center container-sm mainContent" style="margin-top: 0; margin-bottom: 0">
        <div class="container-sm grid-x justify-center align-center" style="height: 23vh; width: auto">
            <c:if test="${not empty alert}">
                <%@include file="../partials/alert.jsp"%>
            </c:if>
        </div>
        <div class="input-group mb-3">
            <form class="grid-inline" method="post" action="scegliTitolo" style="width: 100%">
                <input type="text" class="form-control" placeholder="News Link" name="link"
                       aria-label="Recipient's username" aria-describedby="button-addon2">
                <button class="btn btn-secondary" type="submit" id="button-addon2"  onclick="startPreloader()" >Invia</button>
            </form>
       </div>
        <div class="infoArea container-sm">
            <p>
                Info
            </p>
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
    document.getElementById("notification-close").addEventListener('click',function (){
        document.getElementById("alertContainer").style.display = 'none'
    });
</script>
</body>
</html>