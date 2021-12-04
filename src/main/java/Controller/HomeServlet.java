package Controller;

import Controller.http.Controller;
import Model.Components.Alert;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "HomeServlet", value = "/index.html", loadOnStartup = 0)
public class HomeServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Alert alert = null;
        String check = request.getParameter("c");
        if(session != null && session.getAttribute("alert")!=null && check == null){
            alert = (Alert) session.getAttribute("alert");
            request.setAttribute("alert",alert);
        }else if(session != null && session.getAttribute("alert")!=null && check != null){
            session.removeAttribute("alert");
        }
        request.getRequestDispatcher(view("site/index")).forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}