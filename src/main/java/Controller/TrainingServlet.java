package Controller;

import Controller.http.Controller;
import Model.Components.Alert;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import weka.attributeSelection.*;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Prediction;
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

            //carico il dataset
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\FakeAndTrueRandomWithCovidTest.arff");
            Instances instances = dataSource.getDataSet();

            String preStat;
            //DATA CLEANING
            System.out.println("Numero instance data cleaning: "+instances.numInstances());
            System.out.println("Numero campi vuoti: "+instances.attributeStats(1).missingCount);
            preStat = "Numero instanze data cleaning: "+instances.numInstances()+
                    "\nNumero campi vuoti: "+instances.attributeStats(1).missingCount;
            instances.removeIf(Instance::hasMissingValue);
            System.out.println("Numero campi vuoti dopo remove: "+instances.attributeStats(1).missingCount);
            preStat = preStat + "\nNumero campi vuoti dopo remove: "+instances.attributeStats(1).missingCount;
            //FEATURE SELECTION
            //sono state gia tolte le colonne subject e date
            /*Remove remove = new Remove();
            remove.setOptions(new String[]{"-R","3"});
            remove.setInputFormat(instances);
            instances = Filter.useFilter(instances,remove);*/

            //STRING TO WORD VECTOR
            System.out.println("Inizio TF-IDF con StringToWordVector");
            preStat = preStat + "\nStart TF-IDF with StringToWordVector";
            StringToWordVector stringToWordVector = new StringToWordVector();
            stringToWordVector.setIDFTransform(true);
            stringToWordVector.setTFTransform(true);
            stringToWordVector.setAttributeIndices("first-last"); //tutti gli indici
            stringToWordVector.setStemmer(new SnowballStemmer());
            stringToWordVector.setStopwordsHandler(new Rainbow());
            WordTokenizer wordTokenizer = new WordTokenizer();
            wordTokenizer.setDelimiters(".,;:'\"()?!/ -_><&#");
            stringToWordVector.setTokenizer(wordTokenizer);
            stringToWordVector.setInputFormat(instances);
            stringToWordVector.setWordsToKeep(10);
            instances = Filter.useFilter(instances,stringToWordVector);
            System.out.println("Fine TF-IDF con StringToWordVector");
            preStat = preStat + "\nFine TF-IDF con StringToWordVector";

            //setto il num di colonna della var target
            instances.setClassIndex(0);

            //stampo il numero di parole trovate
            System.out.println("Numero di parole identificate: "+instances.numAttributes());
            preStat = preStat + "\nNumero di parole identificate: "+instances.numAttributes();
            //ATTRIBUTE SELECTION
            System.out.println("Inizio selection");
            preStat = preStat + "\nInizio selection";
            AttributeSelection attributeSelection = new AttributeSelection();
            CfsSubsetEval eval = new CfsSubsetEval();
            BestFirst bestFirst = new BestFirst();
            attributeSelection.setSearch(bestFirst);
            attributeSelection.setEvaluator(eval);
            attributeSelection.SelectAttributes(instances);

            int[] indices = attributeSelection.selectedAttributes();
            System.out.println("Indici da conservare: "+Utils.arrayToString(indices));
            System.out.println("Numero indici(parole da utilizzare): "+indices.length);
            preStat = preStat + "\nIndici da conservare: "+Utils.arrayToString(indices)+
                                "\nNumero indici(parole da utilizzare): "+indices.length;
            writeIndices(indices); //salvo gli indici in un file
            System.out.println("Fine selection");
            preStat = preStat + "\nFine selection";

            //ATTRIBUTE REMOVE
            Remove removeFilter = new Remove();
            removeFilter.setAttributeIndicesArray(indices);
            removeFilter.setInvertSelection(true);
            removeFilter.setInputFormat(instances);
            Instances newData = Filter.useFilter(instances, removeFilter);

            System.out.println("Numero di attributi(parole) rimossi: "+(instances.numAttributes()-newData.numAttributes()));

            System.out.println("Numero Classi: "+instances.numClasses());
            System.out.println("Indice Classe: "+instances.classIndex());
            System.out.println("Stats Class attribute: "+instances.attributeStats(instances.classIndex()));

            preStat = preStat + "\nNumero Classi: "+instances.numClasses()+
                    "\nIndice Classe: "+instances.classIndex() +
                    "\nStats Class attribute: "+instances.attributeStats(instances.classIndex());

            writeStats(preStat,"preStatFile-"+new Date().getTime());

                    //ADDESTRAMENTO NAIVE BAYES
            NaiveBayes naiveBayes = new NaiveBayes();
            Evaluation evaluation = new Evaluation(newData);
            evaluation.crossValidateModel(naiveBayes, newData, K_FOLDS, new Random(new Date().getTime()));
            String naiveStats = "Naive Bayes Summary: "+evaluation.toSummaryString() +
                            "\nNaive Bayes: "+evaluation.toClassDetailsString() +
                            "\nNaive Bayes: "+evaluation.toMatrixString();
            writeStats(naiveStats,"NaiveStats-"+new Date().getTime());
            System.out.println("Naive Bayes Summary: "+evaluation.toSummaryString());
            System.out.println("Naive Bayes: "+evaluation.toClassDetailsString());
            System.out.println("Naive Bayes: "+evaluation.toMatrixString());
            writePredictions(evaluation.predictions(),"predictionsNaiveBayes.txt");


            //ADDESTRAMENTO J48
            J48 decisionTree = new J48();
            Evaluation evaluationTree = new Evaluation(newData);
            evaluationTree.crossValidateModel(decisionTree, newData, K_FOLDS, new Random(new Date().getTime()));
            String j48Stats = "Decision Tree Summary: "+evaluationTree.toSummaryString() +
                    "\nDecision Tree: "+evaluationTree.toClassDetailsString() +
                    "\nDecision Tree: "+evaluationTree.toMatrixString();
            writeStats(j48Stats,"j48Stats-"+new Date().getTime());
            System.out.println("Decision Tree Summary: "+ evaluationTree.toSummaryString());
            System.out.println("Decision Tree: "+ evaluationTree.toClassDetailsString());
            System.out.println("Decision Tree: "+ evaluationTree.toMatrixString());
            writePredictions(evaluationTree.predictions(),"predictionsJ48.txt");

            //SALVATAGGIO MODELLI
            SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model", naiveBayes);
            SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model", decisionTree);
            getServletContext().setAttribute("naiveModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model"));
            getServletContext().setAttribute("dTreeModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model"));

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
            fileWriter.append("Num: "+i+" Actual: "+ prediction.actual()+" Predicted: "+prediction.predicted()+" error: "+(prediction.actual()==prediction.predicted() ? "" : "+"));
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