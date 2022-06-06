package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.util.DatabaseEntity;

@DatabaseEntity(tableName = "DegreeLevel")
public class DegreeLevel extends UniquelyIdentifiable {

    private int id = -1;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
