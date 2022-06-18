package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.BridgingEntities;
import eist.tum_social.tum_social.datastorage.ForeignEntity;
import eist.tum_social.tum_social.datastorage.util.*;
import org.mockito.internal.matchers.Not;

import java.time.LocalDate;
import java.util.List;

@DatabaseEntity(tableName = "Persons")
public class Person extends UniquelyIdentifiable {

    private int id = -1;
    private String firstname;
    private String lastname;

    private String biography;
    private LocalDate birthdate;
    private String tumId;
    private String email;
    private int semesterNr;
    private String password;
    @BridgingTable(
            bridgingTableName = "CourseParticipants",
            ownForeignColumnName = "personId",
            otherForeignColumnName = "courseId")
    private BridgingEntities<Course> courseEntities = new BridgingEntities<>();
    @BridgingTable(
            bridgingTableName = "PersonAppointments",
            ownForeignColumnName = "personId",
            otherForeignColumnName = "appointmentId")
    private BridgingEntities<Appointment> appointmentEntities = new BridgingEntities<>();
    @ForeignTable(
            foreignTableName = "DegreePrograms",
            ownColumnName = "degreeProgramId")
    private ForeignEntity<DegreeProgram> degreeProgramEntity = new ForeignEntity<>();
    @BridgingTable(
            bridgingTableName = "Friends",
            ownForeignColumnName = "person1",
            otherForeignColumnName = "person2",
            bidirectional = true)
    private BridgingEntities<Person> friendEntities = new BridgingEntities<>();
    @BridgingTable(
            bridgingTableName = "FriendRequests",
            ownForeignColumnName = "receiverId",
            otherForeignColumnName = "senderId")
    private BridgingEntities<Person> incomingFriendRequestEntities = new BridgingEntities<>();
    @BridgingTable(
            bridgingTableName = "FriendRequests",
            ownForeignColumnName = "senderId",
            otherForeignColumnName = "receiverId")
    private BridgingEntities<Person> outgoingFriendRequestEntities = new BridgingEntities<>();
    @BridgingTable(
            bridgingTableName = "PersonNotifications",
            ownForeignColumnName = "personId",
            otherForeignColumnName = "notificationId")
    private BridgingEntities<Notification> notificationEntities = new BridgingEntities<>();
    @BridgingTable(
            bridgingTableName = "PersonLikes",
            ownForeignColumnName = "personId",
            otherForeignColumnName = "commentId")
    private BridgingEntities<Comment> likedCommentEntities = new BridgingEntities<>();

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getTumId() {
        return tumId;
    }

    public void setTumId(String tumId) {
        this.tumId = tumId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Person> getFriends() {
        return friendEntities.get();
    }

    public void setFriends(List<Person> friends) {
        friendEntities.set(friends);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSemesterNr() {
        return semesterNr;
    }

    public void setSemesterNr(int semesterNr) {
        this.semesterNr = semesterNr;
    }

    public ForeignEntity<DegreeProgram> getDegreeProgramEntity() {
        return degreeProgramEntity;
    }

    public DegreeProgram getDegreeProgram() {
        return degreeProgramEntity.get();
    }

    public void setDegreeProgram(DegreeProgram degreeProgram) {
        degreeProgramEntity.set(degreeProgram);
    }

    public void setDegreeProgramEntity(ForeignEntity<DegreeProgram> degreeProgramEntity) {
        this.degreeProgramEntity = degreeProgramEntity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BridgingEntities<Course> getCourseEntities() {
        return courseEntities;
    }

    public List<Course> getCourses() {
        return courseEntities.get();
    }

    public void setCourseEntities(BridgingEntities<Course> courseEntities) {
        this.courseEntities = courseEntities;
    }

    public BridgingEntities<Appointment> getAppointmentEntities() {
        return appointmentEntities;
    }

    public List<Appointment> getAppointments() {
        return appointmentEntities.get();
    }

    public void setAppointmentEntities(BridgingEntities<Appointment> appointmentEntities) {
        this.appointmentEntities = appointmentEntities;
    }

    public BridgingEntities<Person> getFriendEntities() {
        return friendEntities;
    }

    public void setFriendEntities(BridgingEntities<Person> friendEntities) {
        this.friendEntities = friendEntities;
    }

    public BridgingEntities<Person> getIncomingFriendRequestEntities() {
        return incomingFriendRequestEntities;
    }

    public void setIncomingFriendRequestEntities(BridgingEntities<Person> incomingFriendRequestEntities) {
        this.incomingFriendRequestEntities = incomingFriendRequestEntities;
    }

    public List<Person> getIncomingFriendRequests() {
        return incomingFriendRequestEntities.get();
    }

    public void setIncomingFriendRequests(List<Person> friendRequests) {
        incomingFriendRequestEntities.set(friendRequests);
    }

    public BridgingEntities<Person> getOutgoingFriendRequestEntities() {
        return outgoingFriendRequestEntities;
    }

    public void setOutgoingFriendRequestEntities(BridgingEntities<Person> outgoingFriendRequestEntities) {
        this.outgoingFriendRequestEntities = outgoingFriendRequestEntities;
    }

    public List<Person> getOutgoingFriendRequests() {
        return outgoingFriendRequestEntities.get();
    }

    public void setOutgoingFriendRequests(List<Person> friendRequests) {
        outgoingFriendRequestEntities.set(friendRequests);
    }

    public BridgingEntities<Notification> getNotificationEntities() {
        return notificationEntities;
    }

    public void setNotificationEntities(BridgingEntities<Notification> notificationEntities) {
        this.notificationEntities = notificationEntities;
    }

    public List<Notification> getNotifications() {
        return notificationEntities.get();
    }

    public void setNotifications(List<Notification> notifications) {
        notificationEntities.set(notifications);
    }

    public BridgingEntities<Comment> getLikedCommentEntities() {
        return likedCommentEntities;
    }

    public List<Comment> getLikedComments() {
        return likedCommentEntities.get();
    }

    public void setLikedCommentEntities(BridgingEntities<Comment> likedCommentEntities) {
        this.likedCommentEntities = likedCommentEntities;
    }

    public void setLikedComments(List<Comment> likedComments) {
        likedCommentEntities.set(likedComments);
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
