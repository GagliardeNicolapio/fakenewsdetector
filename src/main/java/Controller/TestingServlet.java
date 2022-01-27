package Controller;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.border.EmptyBorder;
import javax.xml.crypto.dsig.spec.XPathType;
import java.io.IOException;
import java.rmi.Remote;

@WebServlet(name = "TestingServlet", value = "/testingModel")
public class TestingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\nuovoDatasetDa0a500.arff");
            Instances instancesForAllWords = dataSource.getDataSet();
            FilteredClassifier naiveBayesAllWords = (FilteredClassifier) SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model");
            FilteredClassifier j48AllWords = (FilteredClassifier) SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model");


            instancesForAllWords.setClassIndex(instancesForAllWords.numAttributes()-1);

            System.out.println("Naive bayes addestrato con tutte le parole");
            checkNaiveBayes(instancesForAllWords,naiveBayesAllWords);
            System.out.println("J48 addestrato con tutte le parole");
            checkJ48(instancesForAllWords,j48AllWords);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkNaiveBayes(Instances instances, FilteredClassifier naiveBayes) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.evaluateModel(naiveBayes,instances);

        System.out.println("Naive Bayes Summary: "+evaluation.toSummaryString());
        System.out.println("Naive Bayes: "+evaluation.toClassDetailsString());
        System.out.println("Naive Bayes: "+evaluation.toMatrixString());
    }

    private void checkJ48(Instances instances, FilteredClassifier j48) throws Exception {
        Evaluation evaluationTree = new Evaluation(instances);
        evaluationTree.evaluateModel(j48,instances);

        System.out.println("J48 Summary: "+evaluationTree.toSummaryString());
        System.out.println("J48: "+evaluationTree.toClassDetailsString());
        System.out.println("J48: "+evaluationTree.toMatrixString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
