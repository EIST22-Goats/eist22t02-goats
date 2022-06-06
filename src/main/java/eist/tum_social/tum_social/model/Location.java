package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.util.DatabaseEntity;

@DatabaseEntity(tableName = "Locations")
public class Location {

    private int id = -1;
    private String description;
    private String address;
    private String roomName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

}
