package Controller;

import Controller.http.Controller;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(name = "scegliTestoServlet", value = "/scegliTesto")
public class scegliTestoServlet extends Controller {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if(session == null)
            response.sendRedirect(view("site/index"));
        else{
            Element body = (Element) session.getAttribute("bodyHTML");

            session.setAttribute("titoloNews",request.getParameter("titolo"));

            System.out.println(request.getParameter("titolo"));

            Elements elements = body.getElementsByTag("input");
            for(int i=0; i<elements.size(); i++){
                Element element = elements.get(i);
             element.attr("type","checkbox");
             element.attr("name","textNews");
            }


            request.setAttribute("bodyHTML", body.html());

            request.getRequestDispatcher(view("site/scegliTesto")).forward(request,response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     doGet(request,response);
    }

}
