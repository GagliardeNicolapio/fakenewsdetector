package Controller;

import Controller.http.Controller;
import Controller.http.InvalidRequestException;
import Controller.http.RequestValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

            String link = request.getParameter("link");
            if(!(link.substring(0,7).equals("http://") || link.substring(0,8).equals("https://")))   //controllo se è stato inserito un url con protocollo
                link = "http://" + link;                 //se si riceve un redirect a https jsoup lo fa in automatico

            /*lista di tag da eliminare dal DOM*/
            ArrayList<String> listaTagDaEliminare = new ArrayList<>(Arrays.asList("img","iframe","footer","nav","figure","button","form","input"));
            /*lista di tag inline (al momento non in uso)*/
            //ArrayList<String> listaTagInline = new ArrayList<>(Arrays.asList("b","big","i","small","tt","abbr","acronym","cite","code","dfn","em","kbd","strong","samp","var","a","bdo","br","img","map","object","q","script","span","sub","sup","button","input","label","select","textarea"));

            //jsoup include page
            Element body = Jsoup.connect(link).get().body(); //prendo il body

            for(String tadDaEliminare : listaTagDaEliminare){  //ciclo i tag da eliminare
                Elements lista = body.getElementsByTag(tadDaEliminare); //prendo gli elementi con quel tag
                for(int i=0; i<lista.size(); i++)
                    lista.get(i).remove(); //rimuovo dal DOM
            }

            request.setAttribute("textNews", body.html());

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

/*
 *controllare le unknow host exception quando l'utente mette un url sconosciuto
 * */