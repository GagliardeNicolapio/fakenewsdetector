<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="notification grid-inline justify-between align-center" id="alertContainer"
     style="background-color: darkred; color: white; padding: 10px; border-radius: 10px; width: 500px" >
    <ul class="cell" style="margin: 0">
        <c:forEach var="msg" items="${alert.messages}">
            <li>${msg}</li>
        </c:forEach>
    </ul>
    <span id="notification-close" class="close grid-x justify-center align-center" style="width: 20px; height: 20px;">
        <img src="/FakeNewsDetector/images/icon/close.png" width="15" height="15">
    </span>
</div>