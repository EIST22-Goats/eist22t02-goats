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

import static eist.tum_social.tum_social.controllers.util.DateUtils.formatTimestamp;

@DatabaseEntity(tableName = "Comments")
public class Comment extends UniquelyIdentifiable {

    private int id = -1;
    private String text;
    private LocalDate date;
    private LocalTime time;
    @BridgingTable(
            bridgingTableName = "CommentChildren",
            ownForeignColumnName = "parentId",
            otherForeignColumnName = "childId")
    private BridgingEntities<Comment> childCommentEntities = new BridgingEntities<>();
    @ForeignTable(
            foreignTableName = "Persons",
            ownColumnName = "authorId")
    private ForeignEntity<Person> authorForeignEntity = new ForeignEntity<>();
    @BridgingTable(
            bridgingTableName = "PersonLikes",
            ownForeignColumnName = "commentId",
            otherForeignColumnName = "personId")
    private BridgingEntities<Person> likeEntities = new BridgingEntities<>();

    @Override
    public int getId() {
        return id;
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

    public ForeignEntity<Person> getAuthorForeignEntity() {
        return authorForeignEntity;
    }

    public void setAuthorForeignEntity(ForeignEntity<Person> authorForeignEntity) {
        this.authorForeignEntity = authorForeignEntity;
    }

    public Person getAuthor() {
        return authorForeignEntity.get();
    }

    public void setAuthor(Person p) {
        authorForeignEntity.set(p);
    }

    public String getFormattedTimestamp() {
        return formatTimestamp(date, time);
    }

    public BridgingEntities<Person> getLikeEntities() {
        return likeEntities;
    }

    public List<Person> getLikes() {
        return likeEntities.get();
    }

    public void setLikeEntities(BridgingEntities<Person> likeEntities) {
        this.likeEntities = likeEntities;
    }

    public void setLikes(List<Person> likes) {
       likeEntities.set(likes);
    }
}
