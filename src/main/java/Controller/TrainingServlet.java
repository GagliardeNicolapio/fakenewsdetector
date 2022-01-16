package Controller;

import Controller.http.Controller;
import Model.Components.Alert;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingWithUserConstant;
import weka.filters.unsupervised.attribute.StringToWordVector;

@WebServlet(name = "TrainingServlet", value = "/trainingModel", loadOnStartup = 0)
public class TrainingServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            final int K_FOLDS = 10;

            //carico il dataset
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("C:\\Users\\gagli\\Desktop\\FakeAndTrueRandomWithCovidTest.arff");
            Instances instances = dataSource.getDataSet();

            //setto il num di colonna della var target
            if(instances.classIndex() == -1)
                instances.setClassIndex(instances.numAttributes()-1);

            //DATA CLEANING
            //riempio i campi vuoti //instances.attributeStats(0).missingCount per vedere i campi vuoti //ok funziona
            ReplaceMissingWithUserConstant replaceMissingValues = new ReplaceMissingWithUserConstant();
            replaceMissingValues.setInputFormat(instances);
            instances = Filter.useFilter(instances,replaceMissingValues);


            //FEATURE SELECTION
            //sono state gia tolte le colonne subject e date
            /*Remove remove = new Remove();
            remove.setOptions(new String[]{"-R","3"});
            remove.setInputFormat(instances);
            instances = Filter.useFilter(instances,remove);*/

            System.out.println("Inizio to word vector");
            StringToWordVector stringToWordVector = new StringToWordVector();
            stringToWordVector.setIDFTransform(true);
            stringToWordVector.setTFTransform(true);
            stringToWordVector.setAttributeIndices("first-last"); //tutti gli indici
            stringToWordVector.setStemmer(new SnowballStemmer());
            stringToWordVector.setStopwordsHandler(new Rainbow());
            stringToWordVector.setTokenizer(new WordTokenizer());
            stringToWordVector.setInputFormat(instances);
            instances = Filter.useFilter(instances,stringToWordVector);
            System.out.println("fine to word vector");


            NaiveBayes naiveBayes = new NaiveBayes();
            Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(naiveBayes, instances, K_FOLDS, new Random(new Date().getTime()));
            System.out.println("Naive Bayes: "+evaluation.toClassDetailsString());


            J48 decisionTree = new J48();
            Evaluation evaluationTree = new Evaluation(instances);
            evaluationTree.crossValidateModel(decisionTree, instances, K_FOLDS, new Random(new Date().getTime()));
            System.out.println("Decision Tree: "+ evaluationTree.toClassDetailsString());


            SerializationHelper.write("../model/naiveBayes.model", naiveBayes);
            SerializationHelper.write("../model/j48.model", decisionTree);
            getServletContext().setAttribute("naiveModel",SerializationHelper.read("../model/naiveBayes.model"));
            getServletContext().setAttribute("dTreeModel",SerializationHelper.read("../model/j48.model"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}