package Controller;

import Controller.http.Controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "FeedbackServlet", value = "/feedback")
public class FeedbackServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if(session == null){
            response.sendRedirect(view("site/index"));
        }
        else {
            request.setAttribute("testoAnalizzato", request.getParameter("textNews"));

            if(request.getParameter("flagCopyPaste") != null && request.getParameter("flagCopyPaste").equals("1"))
                request.setAttribute("titoloNews", request.getParameter("titoloNews"));
            else
                request.setAttribute("titoloNews", session.getAttribute("titoloNews"));

            request.getRequestDispatcher(view("site/feedback")).forward(request, response);

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
