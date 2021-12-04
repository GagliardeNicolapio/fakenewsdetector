package Controller.http;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

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

}
