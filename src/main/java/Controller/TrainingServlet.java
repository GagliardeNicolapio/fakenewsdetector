package Controller;

import Controller.http.Controller;
import Model.Components.Alert;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

@WebServlet(name = "TrainingServlet", value = "/trainingModel", loadOnStartup = 0)
public class TrainingServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            final int K_FOLDS = 10;
            long totalTime, startTime, endTime;
            startTime = System.nanoTime();
            //carico il dataset
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\FakeAndTrueRandomWithCovidTest.arff");
            Instances instances = dataSource.getDataSet();
            FilteredClassifier naiveClassifier = new FilteredClassifier();
            FilteredClassifier j48Classifier = new FilteredClassifier();

            // Utilizzo attuale dell'heap
            long heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);

            // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
            long heapMaxSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);

            // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
            long heapFreeSize = Runtime.getRuntime().freeMemory() / (1024 * 1024);

            System.out.println("Utilizzo heap attuale: "+heapSize+" MB"+
                    "\nMax Heap: "+heapMaxSize+" MB"+
                    "\nFree heap space: "+heapFreeSize+" MB");

            String preStat;
            //DATA CLEANING
            System.out.println("Numero instance data cleaning: "+instances.numInstances());
            System.out.println("Numero campi vuoti: "+instances.attributeStats(1).missingCount);
            preStat = "Numero instanze data cleaning: "+instances.numInstances()+
                    "\nNumero campi vuoti: "+instances.attributeStats(1).missingCount;
            instances.removeIf(Instance::hasMissingValue);
            System.out.println("Numero campi vuoti dopo remove: "+instances.attributeStats(1).missingCount);

            //STRING TO WORD VECTOR
            StringToWordVector stringToWordVector = new StringToWordVector();
            stringToWordVector.setIDFTransform(true);
            stringToWordVector.setTFTransform(true);
            int[] attributeToProcess = {0,1};
            stringToWordVector.setAttributeIndicesArray(attributeToProcess); //applichiamo il filtro ai primi due attributi, quindi titolo e testo
            stringToWordVector.setStemmer(new SnowballStemmer());
            stringToWordVector.setStopwordsHandler(new Rainbow());
            WordTokenizer wordTokenizer = new WordTokenizer();
            wordTokenizer.setDelimiters(".,;:'\"()?!/ -_><&#");
            stringToWordVector.setTokenizer(wordTokenizer);
            stringToWordVector.setInputFormat(instances);
            stringToWordVector.setWordsToKeep(1000);
            instances.setClassIndex(instances.numAttributes()-1); //perchè prima di applicare il filtro, nella gui e nel file compare come terzo attributo
            naiveClassifier.setFilter(stringToWordVector); //si aggiunge il filtro che dovrà usare il classificatore
            naiveClassifier.setClassifier(new NaiveBayes()); //si setta il classificatore

            // effettuare la cross validation in questo punto
            //Validazione naive
            /*Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(naiveClassifier, instances, K_FOLDS, new Random(new Date().getTime()));
            String naiveStats = "Naive Bayes Summary: "+evaluation.toSummaryString() +
                    "\nNaive Bayes: "+evaluation.toClassDetailsString() +
                    "\nNaive Bayes: "+evaluation.toMatrixString();
            writeStats(naiveStats,"NaiveStats-"+new Date().getTime());
            System.out.println("Naive Bayes Summary: "+evaluation.toSummaryString());
            System.out.println("Naive Bayes: "+evaluation.toClassDetailsString());
            System.out.println("Naive Bayes: "+evaluation.toMatrixString());
            writePredictions(evaluation.predictions(),"predictionsNaiveBayes"); //la stampa delle predizioni funziona*/

            //Validazione J48
            /*Evaluation evaluationTree = new Evaluation(instances);
            evaluationTree.crossValidateModel(j48Classifier, instances, K_FOLDS, new Random(new Date().getTime()));
            String j48Stats = "Decision Tree Summary: "+evaluationTree.toSummaryString() +
                    "\nDecision Tree: "+evaluationTree.toClassDetailsString() +
                    "\nDecision Tree: "+evaluationTree.toMatrixString();
            writeStats(j48Stats,"j48Stats-"+new Date().getTime());
            System.out.println("Decision Tree Summary: "+ evaluationTree.toSummaryString());
            System.out.println("Decision Tree: "+ evaluationTree.toClassDetailsString());
            System.out.println("Decision Tree: "+ evaluationTree.toMatrixString());*/

            System.out.println("Building naiveClassifier...");
            naiveClassifier.buildClassifier(instances); //il modello conterrà il filtro che applicherà on the flyyyy
            System.out.println("Naive build finish");
            System.out.println("Building j48Classifier...");
            //j48Classifier.buildClassifier(instances);
            System.out.println("J48 build finish");

            //writePredictions(evaluationTree.predictions(),"predictionsJ48");

            //SALVATAGGIO MODELLI
            SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model", naiveClassifier);
            //SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model", decisionTree);
            getServletContext().setAttribute("naiveModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model"));
            //getServletContext().setAttribute("dTreeModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model"));
            endTime = System.nanoTime();
            totalTime = endTime - startTime;
            System.out.println("Total time: "+totalTime/1000000+" ms");
            //preStat = preStat + "\nTotal time: "+totalTime/1000000+" ms";

            //writeStats(preStat,"preStatFile-"+new Date().getTime());

            System.out.println("Salvataggio completato");
            Alert alert = new Alert("Modello addestrato con successo");
            request.setAttribute("alert",alert);
            request.getRequestDispatcher(view("site/index")).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeIndices(int[] indices) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\indiciBestFirst.txt");
        for(int i:indices)
            fileWriter.append(i+"\n");

        fileWriter.append("\n"+"totale: "+indices.length);
        fileWriter.flush();
        fileWriter.close();
    }

    public void writeStats(String stats, String filename) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+filename+".txt");
        fileWriter.write(stats);
        fileWriter.flush();
        fileWriter.close();
    }

    public void writePredictions(ArrayList<Prediction> predictions, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+fileName+".txt");
        int i=0;
        for(Prediction prediction : predictions){
            fileWriter.append("\nNum: "+i+" Actual: "+ prediction.actual()+" Predicted: "+prediction.predicted()+" error: "+(prediction.actual()==prediction.predicted() ? "" : "+"));
            fileWriter.flush();
            i++;
        }
        fileWriter.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}