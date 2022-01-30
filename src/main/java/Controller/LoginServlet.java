package Controller;

import Controller.http.Controller;
import Model.Components.Alert;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "LoginServlet", value = "/login", loadOnStartup = 0)
public class LoginServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(view("site/loginAdmin")).forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String check = request.getParameter("password");
        if(check != null && check.equals("supermario")){
            File folder = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset");
            ArrayList<String> fileList = listFilesForFolder(folder);
            request.setAttribute("fileList",fileList);
            request.getRequestDispatcher(view("site/trainingPage")).forward(request,response);
        }else{
            Alert alert = new Alert();
            alert.addMessage("Password errata");
            request.setAttribute("alert",alert);
            request.getRequestDispatcher(view("site/loginAdmin")).forward(request,response);
        }
    }

}