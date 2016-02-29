package org.teonit.mailinator.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 *
 * @author Andrii Iakovenko
 */
public class MessagePart {
    
    @SerializedName("headers")
    private Map<String, String> headers;
    
    private String body;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    
}
