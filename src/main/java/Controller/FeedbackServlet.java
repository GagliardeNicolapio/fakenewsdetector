package Controller;

import Controller.http.Controller;
import Controller.http.InvalidRequestException;
import Controller.http.RequestValidator;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@WebServlet(name = "FeedbackServlet", value = "/feedback")
public class FeedbackServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Random random = new Random();
        try {
            RequestValidator validator = new RequestValidator(request);
            validate(validator.assertMatch("link",validator.getLinkPattern(), "Inserire un link valido"));
            TimeUnit.SECONDS.sleep(3);
            request.setAttribute("percentuale", random.nextInt(101));
            request.getRequestDispatcher(view("site/feedback")).forward(request,response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (InvalidRequestException ex){
            ex.handle(request,response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     doGet(request,response);
    }
}
