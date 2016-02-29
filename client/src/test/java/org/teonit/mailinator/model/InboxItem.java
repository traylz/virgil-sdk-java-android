package org.teonit.mailinator.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Andrii Iakovenko
 */
public class InboxItem {

    private String id;
    private String subject;
    private String fromfull;
    private String from;
    private String ip;
    private String to;
    private Long time;
    
    @SerializedName("seconds_ago")
    private Long secondsAgo;
    
    @SerializedName("been_read")
    private boolean beenRead;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public boolean isBeenRead() {
        return beenRead;
    }

    public void setBeenRead(boolean beenRead) {
        this.beenRead = beenRead;
    }

    
}
