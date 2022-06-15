package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.util.DatabaseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

@DatabaseEntity(tableName = "Notifications")
public class Notification extends UniquelyIdentifiable {

    private int id = -1;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
