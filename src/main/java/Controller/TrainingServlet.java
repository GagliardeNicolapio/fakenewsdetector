package Controller;

import Controller.http.Controller;
import Model.Components.Alert;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import weka.attributeSelection.*;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingWithUserConstant;
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

            //DATA CLEANING
            System.out.println("Num instance data cleaning: "+instances.numInstances());
            System.out.println("Num campi vuoti: "+instances.attributeStats(1).missingCount);
            instances.removeIf(Instance::hasMissingValue);
            System.out.println("Num campi vuoti after remove: "+instances.attributeStats(1).missingCount);

            //FEATURE SELECTION
            //sono state gia tolte le colonne subject e date
            /*Remove remove = new Remove();
            remove.setOptions(new String[]{"-R","3"});
            remove.setInputFormat(instances);
            instances = Filter.useFilter(instances,remove);*/

            //STRING TO WORD VECTOR
            System.out.println("Inizio to word vector");
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
            stringToWordVector.setWordsToKeep(50);
            instances = Filter.useFilter(instances,stringToWordVector);
            System.out.println("fine to word vector");

            //setto il num di colonna della var target
            instances.setClassIndex(0);

            //ATTRIBUTE SELECTION
            System.out.println("inizio selection");
            AttributeSelection attributeSelection = new AttributeSelection();
            CfsSubsetEval eval = new CfsSubsetEval();
            BestFirst bestFirst = new BestFirst();
            attributeSelection.setSearch(bestFirst);
            attributeSelection.setEvaluator(eval);
            attributeSelection.SelectAttributes(instances);

            int[] indices = attributeSelection.selectedAttributes();
            System.out.println("Indici da conservare: "+Utils.arrayToString(indices));
            System.out.println("Num tot indici: "+indices.length);
            writeIndices(indices); //salvo gli indici in un file
            System.out.println("Fine selection");

            //ATTRIBUTE REMOVE
            Remove removeFilter = new Remove();
            removeFilter.setAttributeIndicesArray(indices);
            removeFilter.setInvertSelection(true);
            removeFilter.setInputFormat(instances);
            Instances newData = Filter.useFilter(instances, removeFilter);

            System.out.println("Numero Classi: "+instances.numClasses());
            System.out.println("Indice Classe: "+instances.classIndex());
            System.out.println("Stats Class attribute: "+instances.attributeStats(instances.classIndex()));

            //ADDESTRAMENTO NAIVE BAYES
            NaiveBayes naiveBayes = new NaiveBayes();
            Evaluation evaluation = new Evaluation(newData);
            evaluation.crossValidateModel(naiveBayes, newData, K_FOLDS, new Random(new Date().getTime()));
            System.out.println("Naive Bayes Summary: "+evaluation.toSummaryString());
            System.out.println("Naive Bayes: "+evaluation.toClassDetailsString());
            System.out.println("Naive Bayes: "+evaluation.toMatrixString());

            //ADDESTRAMENTO J48
            J48 decisionTree = new J48();
            Evaluation evaluationTree = new Evaluation(newData);
            evaluationTree.crossValidateModel(decisionTree, newData, K_FOLDS, new Random(new Date().getTime()));
            System.out.println("Decision Tree Summary: "+ evaluationTree.toSummaryString());
            System.out.println("Decision Tree: "+ evaluationTree.toClassDetailsString());
            System.out.println("Decision Tree: "+ evaluationTree.toMatrixString());

            //SALVATAGGIO MODELLI
            SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model", naiveBayes);
            SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model", decisionTree);
            getServletContext().setAttribute("naiveModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model"));
            getServletContext().setAttribute("dTreeModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeIndices(int[] indices) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\indici.txt");
        for(int i:indices)
            fileWriter.append(i+"\n");

        fileWriter.append("\n"+"totale: "+indices.length);
        fileWriter.flush();
        fileWriter.close();
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}