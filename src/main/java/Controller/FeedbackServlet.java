package Controller;

import Controller.http.Controller;
import Model.Components.Alert;
import weka.classifiers.Classifier;
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

            Classifier naive = (Classifier) getServletContext().getAttribute("naiveModel");
            Classifier dTree = (Classifier) getServletContext().getAttribute("dTreeModel");

            if(naive != null && dTree != null){
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

                    Instances data = new Instances("stream",attributeList,0); //dataset che conterrà la nuova istanza da predire
                    Instance inst_co = new DenseInstance(data.numAttributes());
                    inst_co.setDataset(data);

                    System.out.println("Titolo ricevuto: "+titolo);
                    System.out.println("Testo ricevuto: "+testo);

                    //settiamo il valore degli attributi dell'istanza aggiunta alle istanze
                    inst_co.setValue(title,titolo);
                    inst_co.setValue(text, testo);
                    System.out.println(inst_co.toString());
                    data.add(inst_co); //ora abbiamo un dataset con una sola istanza da predire

                    StringToWordVector stringToWordVector = new StringToWordVector();
                    stringToWordVector.setIDFTransform(true);
                    stringToWordVector.setTFTransform(true);
                    stringToWordVector.setAttributeIndices("first-last"); //tutti gli indici
                    stringToWordVector.setStemmer(new SnowballStemmer());
                    stringToWordVector.setStopwordsHandler(new Rainbow());
                    WordTokenizer wordTokenizer = new WordTokenizer();
                    wordTokenizer.setDelimiters(".,;:'\"()?!/ -_><&#");
                    stringToWordVector.setTokenizer(wordTokenizer);
                    stringToWordVector.setInputFormat(data);
                    stringToWordVector.setWordsToKeep(10);
                    data = Filter.useFilter(data,stringToWordVector); // applichiamo string to word vector al dataset con una singola istanza

                    data.setClassIndex(0);
                    data.setRelationName("stream");

                    System.out.println("Stampa delle istanze data: "+data.toString());
                    System.out.println("Class index data: "+data.classIndex());
                    System.out.println("Numero istanze data: "+data.numInstances());
                    System.out.println("Numero classi, dovrebbero essere 2 (data): "+data.numClasses());

                    System.out.println("Prima istanza: "+data.firstInstance().toString());

                    double naiveIndex = naive.classifyInstance(data.firstInstance());
                    double dTreeIndex = dTree.classifyInstance(data.firstInstance());
                    String naiveLabel = naiveIndex < 1 ? "false":"true";
                    String dTreeLabel = dTreeIndex < 1 ? "false":"true";

                    request.setAttribute("naiveLabel",naiveLabel);
                    request.setAttribute("dTreeLabel",dTreeLabel);
                    request.getRequestDispatcher(view("site/feedback")).forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else{
                String errorMsg;
                if(naive == null){
                    errorMsg = "Naive model non presente";
                }else if(dTree == null){
                    errorMsg = "DecisionTree model non presente";
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
