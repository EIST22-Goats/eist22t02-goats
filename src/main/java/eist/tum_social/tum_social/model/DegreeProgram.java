package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.persistent_data_storage.util.ForeignTable;
import eist.tum_social.tum_social.persistent_data_storage.util.DatabaseEntity;

@DatabaseEntity(tableName = "DegreePrograms")
public class DegreeProgram {

    private int id;
    private String name;
    private String description;
    @ForeignTable(ownColumnName = "degreeLevelId")
    private DegreeLevel degreeLevel;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DegreeLevel getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(DegreeLevel degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

}
