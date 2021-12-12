package Controller;

import Controller.http.Controller;
import Model.Components.Alert;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;
import weka.core.converters.ConverterUtils;

@WebServlet(name = "TrainingServlet", value = "/trainingModel", loadOnStartup = 0)
public class TrainingServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(view("site/trainingPage")).forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //load dataset
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("/FakeNewsDetector/Dataset/True.arff"); //path corretta?
            Instances dataset = source.getDataSet();
            //set class index to the last attribute
            dataset.setClassIndex(dataset.numAttributes()-1);
            //create and build the classifier
            NaiveBayes nb = new NaiveBayes();
            nb.buildClassifier(dataset);
            //print out capabilities
            System.out.println(nb.getCapabilities().toString());

            Alert alert = new Alert("Modello addestrato");
            request.setAttribute("alert",alert);
            request.getRequestDispatcher(view("site/index")).forward(request,response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}