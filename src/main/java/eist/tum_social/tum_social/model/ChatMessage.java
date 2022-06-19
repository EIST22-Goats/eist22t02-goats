package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.ForeignEntity;
import eist.tum_social.tum_social.datastorage.util.DatabaseEntity;
import eist.tum_social.tum_social.datastorage.util.ForeignTable;

import java.time.LocalDate;
import java.time.LocalTime;

@DatabaseEntity(tableName = "ChatMessages")
public class ChatMessage extends UniquelyIdentifiable {

    private int id = -1;
    @ForeignTable(
            foreignTableName = "Persons",
            ownColumnName = "senderId")
    private ForeignEntity<Person> senderEntity;
    @ForeignTable(
            foreignTableName = "Persons",
            ownColumnName = "senderId")
    private ForeignEntity<Person> receiverEntity;
    private String message;
    private LocalDate date;
    private LocalTime time;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ForeignEntity<Person> getReceiverEntity() {
        return receiverEntity;
    }

    public Person getReceiver() {
        return receiverEntity.get();
    }

    public void setReceiverEntity(ForeignEntity<Person> receiverEntity) {
        this.receiverEntity = receiverEntity;
    }

    public ForeignEntity<Person> getSenderEntity() {
        return senderEntity;
    }

    public Person getSender() {
        return senderEntity.get();
    }

    public void setSenderEntity(ForeignEntity<Person> senderEntity) {
        this.senderEntity = senderEntity;
    }
}
