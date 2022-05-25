package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.database.util.DatabaseEntity;
import eist.tum_social.tum_social.database.util.PrimaryKey;

@DatabaseEntity(tableName = "DegreeLevel")
public class DegreeLevel {
    @PrimaryKey
    private int id;
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
