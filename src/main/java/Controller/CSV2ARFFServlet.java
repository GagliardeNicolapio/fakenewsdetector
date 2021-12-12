package Controller;

import Controller.http.Controller;
import Model.Components.Alert;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;

@WebServlet(name = "CSV2ARFFServlet", value = "/csv2arff", loadOnStartup = 0)
@MultipartConfig
public class CSV2ARFFServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(view("site/index")).forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //load CSV
        CSVLoader loader = new CSVLoader();
        Part fileCSV = request.getPart("csvFile");
        String fileName = fileCSV.getSubmittedFileName().replaceAll(".csv",".arff");
        loader.setSource(fileCSV.getInputStream());
        Instances data = loader.getDataSet();//get instance object

        //save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);// selezioniamo il dataset da convertire
        //salviamo in ARFF
        saver.setFile(new File(getUploadPath() + fileName));
        saver.writeBatch();
        Alert alert = new Alert("File CSV convertito correttamente");
        request.setAttribute("alert",alert);
        request.getRequestDispatcher(view("site/index")).forward(request,response);
    }
}