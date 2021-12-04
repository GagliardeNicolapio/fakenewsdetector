package Controller.http;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class RequestValidator {

    private String error;
    private final HttpServletRequest request;
    private final Pattern LINK_PATTERN =
            Pattern.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");

    public RequestValidator(HttpServletRequest request) {
        this.error = null;
        this.request = request;
    }

    private boolean required(String value){
        return value != null && !value.trim().isEmpty();
    }

    public RequestValidator assertMatch(String value, Pattern regexp, String msg){
        String param = request.getParameter(value);
        if(!(required(param) && regexp.matcher(param).matches())){
            error = msg;
        }
        return this;
    }

    public boolean hasError(){
        return error != null;
    }

    public String getError(){
        return error;
    }

    public Pattern getLinkPattern(){
        return this.LINK_PATTERN;
    }
}
