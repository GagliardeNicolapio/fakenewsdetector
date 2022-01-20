package Controller;

import Controller.http.Controller;
import Model.Components.Alert;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "HomeServlet", value = "/index.html", loadOnStartup = 0)
public class HomeServlet extends Controller {

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            NaiveBayes naive = (NaiveBayes) SerializationHelper.read(
                    "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model");
            getServletContext().setAttribute("naiveModel",naive);
        } catch (Exception e) {
            System.out.println("Modello NaiveBayes non presente");
        }
        try {
            J48 dTree = (J48) SerializationHelper.read(
                    "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model");
            getServletContext().setAttribute("dTreeModel",dTree);
        }catch (Exception e){
            System.out.println("Modello DecisionTree non presente");
        }
        try {
            Instances dataset = ConverterUtils.DataSource.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\FakeAndTrueRandomWithCovidTest.arff");
            dataset.removeIf(Instance::hasMissingValue);

            getServletContext().setAttribute("dataset",dataset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Alert alert = null;
        String check = request.getParameter("c");
        if(session != null && session.getAttribute("alert")!=null && check == null){
            alert = (Alert) session.getAttribute("alert");
            request.setAttribute("alert",alert);
        }else if(session != null && session.getAttribute("alert")!=null && check != null){
            session.removeAttribute("alert");
        }
        request.getRequestDispatcher(view("site/index")).forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}