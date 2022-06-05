package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.persistent_data_storage.util.DatabaseEntity;

@DatabaseEntity(tableName = "Locations")
public class Location {

    private int id = -1;
    private String description;
    private String address;

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
}
