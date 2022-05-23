package eist.tum_social.tum_social.model;

import java.util.Date;

public class Notification {

    protected String text;
    protected Date timestamp;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
