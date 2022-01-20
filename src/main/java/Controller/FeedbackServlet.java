package Controller;

import Controller.http.Controller;
import Model.Components.Alert;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.trees.J48;
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

            NaiveBayes naive = (NaiveBayes) getServletContext().getAttribute("naiveModel");
            J48 dTree = (J48) getServletContext().getAttribute("dTreeModel");

            if(naive != null && dTree != null){
                try {
                    /*Questa prima parte commentata serve a creare un Instances simulando il dataset
                    * ma con una sola istanza, ossia quella nuova da predire con titolo e testo dell'utente
                    * e visto che l'errore è uguale anche se usiamo un istanza del dataset, utilizzeremo questa
                    * parte credo, almeno non bisogna fare stringToVector su tutto il dataset ogni volta
                    * ma solo su una istanza*/
                    //ArrayList<Attribute> attributeList = new ArrayList<>();

                    /*Attribute title = new Attribute("title", (List<String>) null);
                    Attribute text = new Attribute("text", (List<String>) null);*/

                    /*ArrayList<String> classVal = new ArrayList<>();
                    classVal.add("fake");
                    classVal.add("true");*/

                    /*attributeList.add(title);
                    attributeList.add(text);
                    attributeList.add(new Attribute("varTarget",classVal));*/

                    //Instances data = new Instances("stream",attributeList,0); //dataset che conterrà soltanto la nuova istanza da predire
                    Instances originalDataset = (Instances)getServletContext().getAttribute("dataset");
                    Instance inst_co = new DenseInstance(originalDataset.numAttributes());
                    inst_co.setDataset(originalDataset); //da sostituire con data, ossia il dataset con una sola istanza

                    System.out.println("Titolo ricevuto: "+titolo);
                    System.out.println("Testo ricevuto: "+testo);

                    //settiamo il valore degli attributi della nuova istanza
                    inst_co.setValue(0,titolo);
                    inst_co.setValue(1, testo);
                    System.out.println(inst_co.toString());
                    //inserisco in coda la nuova istanza, quando utilizzeremo il dataset con una istanza, sarà l'unica presente nel dataset
                    originalDataset.add(inst_co);

                    //Sostituire con lastInstance per stampare la nuova istanza con titolo e testo dell'utente
                    System.out.println("Prima istanza preStringtoVector: "+originalDataset.lastInstance().toString());

                    StringToWordVector stringToWordVector = new StringToWordVector();
                    stringToWordVector.setIDFTransform(true);
                    stringToWordVector.setTFTransform(true);
                    stringToWordVector.setAttributeIndices("first-last"); //tutti gli indici
                    stringToWordVector.setStemmer(new SnowballStemmer());
                    stringToWordVector.setStopwordsHandler(new Rainbow());
                    WordTokenizer wordTokenizer = new WordTokenizer();
                    wordTokenizer.setDelimiters(".,;:'\"()?!/ -_><&#");
                    stringToWordVector.setTokenizer(wordTokenizer);
                    stringToWordVector.setInputFormat(originalDataset);
                    stringToWordVector.setWordsToKeep(10);
                    // applico string to word vector al dataset originale, che contiene la nuova istanza in coda
                    originalDataset = Filter.useFilter(originalDataset,stringToWordVector);

                    originalDataset.setClassIndex(0);
                    //data.setRelationName("stream"); potrebbe servire quando si utilizza il dataset simulato con un istanza

                    //System.out.println("Stampa delle istanze data: "+data.toString()); è possibile solo con il dataset con un istanza, altrimenti stampa troppe cose
                    System.out.println("Class index data: "+originalDataset.classIndex());
                    System.out.println("Numero istanze data: "+originalDataset.numInstances());
                    System.out.println("Numero classi, dovrebbero essere 2 (data): "+originalDataset.numClasses());

                    //lastInstance per stampare la nuova istanza dell'utente con TF-IDF applicato
                    System.out.println("Prima istanza: "+originalDataset.lastInstance().toString());

                    //con distributionForInstance abbiamo le probabilità e possiamo usare il grafico in percentuale
                    //lastInstance per fare la predizione sulla nuova istanza
                    double[] naiveIndex = naive.distributionForInstance(originalDataset.lastInstance()); //istanza nuova, quindi predizione sul testo inserito sul sito
                    //double dTreeIndex = dTree.classifyInstance(originalDataset.firstInstance()); //errore index out of bound
                    //String naiveLabel = naiveIndex < 1 ? "false":"true";
                    //String dTreeLabel = dTreeIndex < 1 ? "false":"true";

                    for(int i=0; i<naiveIndex.length; i++){
                        System.out.println("Naive ha predetto: "+naiveIndex[i]);
                    }
                    /*for(int i=0; i<dTreeIndex.length; i++){
                        System.out.println("J48 ha predetto: "+dTreeIndex[i]);
                    }*/


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
