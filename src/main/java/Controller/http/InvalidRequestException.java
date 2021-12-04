package Controller.http;
import Model.Components.Alert;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class InvalidRequestException extends Exception{

    private final String error;
    private final int errorCode;

    public InvalidRequestException(String message, String error, int errorCode) {
        super(message);
        this.error = error;
        this.errorCode = errorCode;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(true);
        session.setAttribute("alert",new Alert(error));
        response.setStatus(errorCode);
        response.sendRedirect("http://localhost:8080/FakeNewsDetector");
    }

}
