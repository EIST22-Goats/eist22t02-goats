package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.BridgingEntities;
import eist.tum_social.tum_social.datastorage.ForeignEntity;
import eist.tum_social.tum_social.datastorage.util.BridgingTable;
import eist.tum_social.tum_social.datastorage.util.DatabaseEntity;
import eist.tum_social.tum_social.datastorage.util.ForeignTable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@DatabaseEntity(tableName = "Comments")
public class Comment extends UniquelyIdentifiable {

    private int id = -1;
    private String text;
    private Person author;
    private LocalDate date;
    private LocalTime time;
    @BridgingTable(
            bridgingTableName = "CommentChildren",
            ownForeignColumnName = "parentId",
            otherForeignColumnName = "childId")
    private BridgingEntities<Comment> childCommentEntities;
    @Override
    public int getId() {
        return 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
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

    public BridgingEntities<Comment> getChildCommentEntities() {
        return childCommentEntities;
    }

    public void setChildCommentEntities(BridgingEntities<Comment> childCommentEntities) {
        this.childCommentEntities = childCommentEntities;
    }

    public List<Comment> getChildComments() {
        return childCommentEntities.get();
    }

    public void setChildComments(List<Comment> comments) {
        childCommentEntities.set(comments);
    }
}
