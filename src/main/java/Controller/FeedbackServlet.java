package Controller;

import Controller.http.Controller;
import Controller.http.InvalidRequestException;
import Controller.http.RequestValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            ArrayList<String> listaTagDaEliminare = new ArrayList<>(Arrays.asList("time","img","iframe","footer","nav","figure","button","form","input","script","noscript","header","select"));
            /*lista di tag inline */
            ArrayList<String> listaTagInline = new ArrayList<>(Arrays.asList("b","big","i","small","tt","abbr","acronym","cite","code","dfn","em","kbd","strong","samp","var","a","bdo","br","img","map","object","q","script","span","sub","sup","button","input","label","select","textarea"));


            //jsoup include page
            Document document = Jsoup.connect(link).get();
            Element body = document.body(); //prendo il body

            for(String tadDaEliminare : listaTagDaEliminare){  //ciclo i tag da eliminare
                Elements lista = body.getElementsByTag(tadDaEliminare); //prendo gli elementi con quel tag
                for(int i=0; i<lista.size(); i++)
                    lista.get(i).remove(); //rimuovo dal DOM
            }

            body.getElementsByAttribute("src").forEach(item->item.remove()); //rimuovo gli elementi che hanno l'attributo src

            Elements lista = body.getAllElements();  //rimuovo gli elementi che non hanno testo
            for(int i=0; i<lista.size(); i++)
                if(lista.get(i).text().replace(" ","").equals(""))
                    lista.get(i).remove();


            body.getAllElements().forEach(item->item.removeAttr("class")); //rimuovo gli attributi class e id (per eliminare il css)
            body.getAllElements().forEach(item->item.removeAttr("id"));


            //applico le label ai div
            int contLabel=0;
            Elements listaDIV = body.getElementsByTag("div"); //prendo tutti i div
            for(int i=0; i<listaDIV.size(); i++){
                Element element = listaDIV.get(i);
                if((!element.text().equals("")) && !checkChildrenBlockElem(element.children())){ //controllo se hanno testo e se non hanno elementi block come figli
                   Element label = document.createElement("label");
                   label.attr("for","check"+contLabel);

                    element.replaceWith(label);//metto il div come figlio di label, cosi creo il blocco
                    label.appendChild(element);
                    label.prepend("<input class='form-check-input'  type='checkbox' id='"+("check"+contLabel)+"' name='"+("check"+contLabel)+"' value='"+ label.text() +"'>");
                    contLabel++;
                }
            }

            //elimino i div che contengono solo un link
            for (Element element : body.getElementsByTag("div")){
                 if(element.childNodes().size()==1 && element.childNodes().get(0).nodeName().equals("a")){ //ATTENZIONE FARE TEST CON NEWS CHE HANNO LINK NEL TESTO
                     element.remove();
                 }
            }

            //rimuovo i tag strong
           for (Element element : body.getElementsByTag("strong")){
                element.after(element.text() + " ");
                element.remove();
            }


            //applico le label ai tag p
            Elements listaP = body.getElementsByTag("p");
            for(int i=0; i<listaP.size(); i++){
                Element element = listaP.get(i);
                if((!element.text().equals("")) && !checkChildrenBlockElem(element.children())){
                    Element label = document.createElement("label");
                    label.attr("for","check"+contLabel);

                    element.replaceWith(label);
                    label.appendChild(element);
                    label.prepend("<input class='form-check-input' type='checkbox' id='"+("check"+contLabel)+"' name='"+("check"+contLabel)+"' value='"+ label.text() +"'>");
                    contLabel++;

                }
            }


            //applico le label ai textNode, faccio il parse altrimenti jsoup mi vede le eliminazioni del tag strong come altri nodi
             body =  Jsoup.parse(body.html());
            listaDIV = body.getElementsByTag("div");

            for (int i = 0; i < listaDIV.size(); i++) {
                List<Node> elementsChild = listaDIV.get(i).childNodes();
                for (int j = 0; j < elementsChild.size(); j++) {
                    Node element = elementsChild.get(j);

                    if (element instanceof TextNode) {
                        Element label = document.createElement("label");
                        label.attr("for", "check" + contLabel);

                        element.replaceWith(label);
                        label.appendChild(element);
                        label.prepend("<input class='form-check-input' type='checkbox' id='" + ("check" + contLabel) + "' name='" + ("check" + contLabel) + "' value='" + label.text() + "'>");
                        contLabel++;

                    }
                }
            }




            //rimuovo le label vuote
            Elements listaLabel = body.getElementsByTag("label");
            for(int i=0; i<listaLabel.size(); i++){
                Element element = listaLabel.get(i);
                if(element.text().replace(" ","").equals(""))
                    element.remove();
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

    public static boolean checkChildrenBlockElem(Elements children){
        ArrayList<String> listaBlockElem = new ArrayList<>(Arrays.asList("address","article","aside","blockquote","canvas","dd","div","dl","dt","fieldset","figcaption","figure","footer","form","h1>-<h6","header","hr","li","main","nav","noscript","ol","p","pre","section","table","tfoot","ul","video"));
        for(Element element : children){
            if(listaBlockElem.contains(element.tagName()))
                return true;
        }
        return false;
    }
}

/*
 *controllare le unknow host exception quando l'utente mette un url sconosciuto
 * */