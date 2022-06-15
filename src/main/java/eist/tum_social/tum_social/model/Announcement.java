package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.BridgingEntities;
import eist.tum_social.tum_social.datastorage.util.BridgingTable;
import eist.tum_social.tum_social.datastorage.util.DatabaseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@DatabaseEntity(tableName = "Announcements")
public class Announcement extends UniquelyIdentifiable {

    private int id = -1;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime time;
    @BridgingTable(
            bridgingTableName = "AnnouncementComments",
            ownForeignColumnName = "announcementId",
            otherForeignColumnName = "rootCommentId")
    private BridgingEntities<Comment> commentBridgingEntities;

    @Override
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BridgingEntities<Comment> getCommentBridgingEntities() {
        return commentBridgingEntities;
    }

    public List<Comment> getComments() {
        return commentBridgingEntities.get();
    }

    public void setComments(List<Comment> comments) {
        commentBridgingEntities.set(comments);
    }

    public void setCommentBridgingEntities(BridgingEntities<Comment> commentBridgingEntities) {
        this.commentBridgingEntities = commentBridgingEntities;
    }
}
