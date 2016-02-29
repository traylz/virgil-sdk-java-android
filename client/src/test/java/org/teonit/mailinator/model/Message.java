package org.teonit.mailinator.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 *
 * @author Andrii Iakovenko
 */
public class Message {
    private String id;
    private String fromfull;
    private String from;
    private String ip;
    private String to;
    private String subject;
    
    private Long time;
    
    @SerializedName("seconds_ago")
    private Long secondsAgo;
    
    private boolean been_read;
    
    @SerializedName("parts")
    private List<MessagePart> parts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromfull() {
        return fromfull;
    }

    public void setFromfull(String fromfull) {
        this.fromfull = fromfull;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getSecondsAgo() {
        return secondsAgo;
    }

    public void setSecondsAgo(Long secondsAgo) {
        this.secondsAgo = secondsAgo;
    }

    public boolean isBeen_read() {
        return been_read;
    }

    public void setBeen_read(boolean been_read) {
        this.been_read = been_read;
    }

    public List<MessagePart> getParts() {
        return parts;
    }

    public void setParts(List<MessagePart> parts) {
        this.parts = parts;
    }
    
    
    
}
