<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="../partials/head.jsp">
        <jsp:param name="title" value="About us"/>
        <jsp:param name="styles" value="about.css,feedback.css"/>
        <jsp:param name="script" value="preloader.js"/>
    </jsp:include>
</head>
<body>
<header>
    <jsp:include page="../partials/header.jsp">
        <jsp:param name="link" value="true"/>
    </jsp:include>
</header>
    <div id="aboutBody" class="mainContent">
        <div style="border-bottom: 1px solid grey; margin-bottom: 10px">
            <h1 class="fw-bolder">About us</h1>
        </div>
            <ul id="aboutList">
                <li>
                    <h2>Obiettivi</h2>
                    <ul class="list-unstyled fs-5">
                        <li>L'obiettivo del sito è quello di analizzare la news ed indicare con una certa
                            probabilità la veridicità o meno del contenuto.
                        </li>
                    </ul>
                </li>
                <li>
                    <h2>Github</h2>
                    <ul class="list-unstyled fs-5">
                        <li>
                            <a href="https://github.com/ilgrafico/fakenewsdetector">https://github.com/ilgrafico/fakenewsdetector</a>
                        </li>
                    </ul>
                </li>
            </ul>
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
