package Controller;

import Controller.http.Controller;
import Model.Components.Alert;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "FeedbackServlet", value = "/feedback")
public class FeedbackServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        HttpSession session = request.getSession(false);
        if(session == null){
            response.sendRedirect(view("site/index"));
        }
        else {
            String titolo;
            String testo = request.getParameter("textNews");
            request.setAttribute("testoAnalizzato", testo);

            if(request.getParameter("flagCopyPaste") != null && request.getParameter("flagCopyPaste").equals("1")) {
                titolo = request.getParameter("titoloNews");
            }else{
                titolo = (String) session.getAttribute("titoloNews");
            }
            request.setAttribute("titoloNews", titolo);

            FilteredClassifier naive = (FilteredClassifier) getServletContext().getAttribute("naiveModel");
            //FilteredClassifier dTree = (FilteredClassifier) getServletContext().getAttribute("dTreeModel");

            if(naive != null){
                try {
                    ArrayList<Attribute> attributeList = new ArrayList<>();

                    Attribute title = new Attribute("title", (List<String>) null);
                    Attribute text = new Attribute("text", (List<String>) null);

                    ArrayList<String> classVal = new ArrayList<>();
                    classVal.add("fake");
                    classVal.add("true");

                    attributeList.add(title);
                    attributeList.add(text);
                    attributeList.add(new Attribute("varTarget",classVal));

                    Instances data = new Instances("stream",attributeList,1); //dataset che conterrà soltanto la nuova istanza da predire, la capacità indica che conterrà una sola istanza
                    data.setClassIndex(2); //la classe è sempre il terzo attributo in ordine di inserimento nella lista
                    DenseInstance inst_co = new DenseInstance(data.numAttributes());
                    //settiamo il valore degli attributi della nuova istanza
                    inst_co.setValue(title,titolo);
                    inst_co.setValue(text,testo);
                    data.add(inst_co);

                    double naiveIndex = naive.classifyInstance(data.instance(0)); //istanza nuova, quindi predizione sul testo inserito sul sito
                    //double dTreeIndex = dTree.classifyInstance(data.instance(0)); //da provare

                    String prediction = (int)naiveIndex == 0 ? "fake" : "true";

                    System.out.println("Naive ha predetto: "+prediction);


                    //request.setAttribute("percentuale",);


                    /*request.setAttribute("naiveLabel",naiveLabel);
                    request.setAttribute("dTreeLabel",dTreeLabel);*/
                    request.getRequestDispatcher(view("site/feedback")).forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else{
                String errorMsg;
                if(naive == null){
                    errorMsg = "Naive model non presente";
                }else{
                    errorMsg = "Classificatori non presenti";
                }
                Alert alert = new Alert(errorMsg);
                request.setAttribute("alert",alert);
                request.getRequestDispatcher(view("site/index")).forward(request,response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

}
