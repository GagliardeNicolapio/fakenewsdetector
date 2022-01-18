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

            //DATA CLEANING
            System.out.println("Num campi vuoti: "+instances.attributeStats(1).missingCount);
            System.out.println("Num attributes: "+instances.numInstances());
            instances.removeIf(Instance::hasMissingValue);
            System.out.println("Num campi vuoti: "+instances.attributeStats(1).missingCount);
            System.out.println("Num attributes: "+instances.numInstances());


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
            stringToWordVector.setWordsToKeep(1000000);
            instances = Filter.useFilter(instances,stringToWordVector);
            System.out.println("fine to word vector");

            //setto il num di colonna della var target
            if(instances.classIndex() == -1)
                instances.setClassIndex(instances.numAttributes()-1);

            System.out.println("Num attributes: "+instances.numAttributes());
            System.out.println("Num instances: "+instances.numInstances());
            System.out.println("Num classes: "+instances.numClasses());

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

            //ATTRIBUTE REMOVE
            int j=0;
            for(int i=0; i<instances.numAttributes(); i++){
                if(indices[j]!=i){
                    instances.remove(i);
                }else{
                    j++;
                    continue;
                }
            }

            System.out.println("Num tot attibutes rimasti: "+instances.numAttributes());


            //ADDESTRAMENTO NAIVE BAYES
            NaiveBayes naiveBayes = new NaiveBayes();
            Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(naiveBayes, instances, K_FOLDS, new Random(new Date().getTime()));
            System.out.println("Naive Bayes: "+evaluation.toClassDetailsString());

            //ADDESTRAMENTO J48
            J48 decisionTree = new J48();
            Evaluation evaluationTree = new Evaluation(instances);
            evaluationTree.crossValidateModel(decisionTree, instances, K_FOLDS, new Random(new Date().getTime()));
            System.out.println("Decision Tree: "+ evaluationTree.toClassDetailsString());

            //SALVATAGGIO MODELLI
            SerializationHelper.write("../model/naiveBayes.model", naiveBayes);
            SerializationHelper.write("../model/j48.model", decisionTree);
            getServletContext().setAttribute("naiveModel",SerializationHelper.read("../model/naiveBayes.model"));
            getServletContext().setAttribute("dTreeModel",SerializationHelper.read("../model/j48.model"));

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