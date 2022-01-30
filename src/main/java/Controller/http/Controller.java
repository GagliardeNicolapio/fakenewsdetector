package Controller.http;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public abstract class Controller extends HttpServlet {

    protected String view(String viewPath){
        String basePath = getServletContext().getInitParameter("basePath");
        String engine = getServletContext().getInitParameter("engine");
        return basePath + viewPath + engine;
    }

    protected void validate(RequestValidator validator) throws InvalidRequestException{
        if(validator.hasError()){
            throw new InvalidRequestException("Validation Error", validator.getError(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected String getUploadPath(){
        return System.getenv("CATALINA_HOME") + File.separator + "dataset" + File.separator;
    }

    public ArrayList<String> listFilesForFolder(final File folder) {

        ArrayList<String> list = new ArrayList<>();

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                list.add(fileEntry.getName());
            }
        }
        return list;
    }

}
