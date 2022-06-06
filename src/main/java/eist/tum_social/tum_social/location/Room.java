package eist.tum_social.tum_social.location;

public class Room {

    private String roomId;
    private String name;
    private String building;

    public Room(String roomId, String name, String building) {
        this.roomId = roomId;
        this.name = name;
        this.building = building;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public String getBuilding() {
        return building;
    }

}
