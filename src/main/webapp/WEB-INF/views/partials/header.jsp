<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-light" style="background-color: #269aff;">
    <div class="container-fluid">
        <div class="grid-x justify-between align-center" id="header" style="width: 100%">
            <div class="grid-inline justify-between align-center" id="logoHeader">
                <a href="http://localhost:8080/FakeNewsDetector?c=tb78L" class="grid-inline justify-between align-center"
                   style="text-decoration: none; margin-right: 10px">
                    <img onclick="abortPreloader()" src="/FakeNewsDetector/images/icon/logoWorld.png" alt="" width="60" height="60" class="d-inline-block align-text-top">
                    <p style="color: white;font-size: 1.5rem;margin: 0;">FakeNewsDetector</p>
                </a>
                <c:if test="${not empty param.link}">
                    <div class="input-group" style="width: 60%;">
                        <form class="grid-inline justify-center align-center" method="post" action="feedback" style="width: 100%;margin: auto">
                            <input type="text" class="form-control" placeholder="News Link" name="link"
                                   aria-label="Recipient's username" aria-describedby="button-addon2">
                            <button class="btn btn-secondary" type="submit" onclick="startPreloader()" id="button-addon2">Invia</button>
                        </form>
                    </div>
                </c:if>
            </div>
            <a onclick="abortPreloader()" href="http://localhost:8080/FakeNewsDetector/login" id="adminLink">Admin</a>
            <a onclick="abortPreloader()" href="http://localhost:8080/FakeNewsDetector/aboutus" id="aboutLink">About us</a>
        </div>
    </div>
</nav>