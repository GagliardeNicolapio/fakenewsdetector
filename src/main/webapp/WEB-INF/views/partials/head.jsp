<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="context" value="${pageContext.request.contextPath}"/>
<meta charset="utf-8">
<meta name="author" content="co-authored by Carmine Leo, Nicolapio Gagliarde">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<title>${param.title}</title>
<meta name="description" content="Fake news detector">
<link rel="icon" type="image/png" href="${context}/images/icon/logoWorld.png">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="format-detection" content="telephone-no">
<meta name="apple-mobile-web-app-title" content="MetaGames">
<meta name="apple-mobile-web-app-status-bar-style" content="default">
<link rel="apple-touch-icon" href="${context}/images/icon/logoWorld.png">
<link rel="apple-touch-startup-image" href="${context}/images/icon/logoWorld.png">
<meta name="theme-color" content="#1B5773">
<link rel="stylesheet" href="${context}/CSS/header.css">
<link rel="stylesheet" href="${context}/CSS/library.css">
<!-- Bootstrap CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-F3w7mX95PdgyTmZZMECAngseQB83DfGTowi0iMjiWaeVhAn4FJkqJByhZMI3AhiU" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-/bQdsTh/da6pkI1MST/rWKFNjaCP5gBSY4sEBT38Q/9RBh9AH40zEOg7Hlq2THRZ" crossorigin="anonymous"></script>
<link rel="icon" href="/images/icon/lodoWorld.png" type="image/gif" sizes="16x16">

<c:if test="${not empty param.styles}">
    <c:forTokens delims="," items="${param.styles}" var="style">
        <link rel="stylesheet" href="${context}/CSS/${style}">
    </c:forTokens>
</c:if>
<c:if test="${not empty param.script}">
    <c:forTokens delims="," items="${param.script}" var="script">
        <script src="${context}/JS/${script}" defer></script>
    </c:forTokens>
</c:if>