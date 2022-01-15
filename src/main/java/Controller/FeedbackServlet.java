package Controller;

import Controller.http.Controller;
import Model.Components.Alert;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
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

            Classifier naive = (Classifier) getServletContext().getAttribute("naiveModel");
            Classifier dTree = (Classifier) getServletContext().getAttribute("dTreeModel");

            if(naive != null && dTree != null){
                Instances datasetCopy;
                try {
                    datasetCopy = new Instances((Instances) getServletContext().getAttribute("dataset"));
                    Instance newInstance = new DenseInstance(3);
                    newInstance.setValue(0,titolo);
                    newInstance.setValue(1,testo);
                    datasetCopy.add(newInstance);

                    double naiveIndex = naive.classifyInstance(datasetCopy.lastInstance());
                    double dTreeIndex = dTree.classifyInstance(datasetCopy.lastInstance());
                    String naiveLabel = naiveIndex < 1 ? "false":"true";
                    String dTreeLabel = dTreeIndex < 1 ? "false":"true";

                    request.setAttribute("naiveLabel",naiveLabel);
                    request.setAttribute("dTreeLabel",dTreeLabel);
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
