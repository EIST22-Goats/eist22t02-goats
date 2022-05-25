package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.persistent_data_storage.util.ColumnMapping;
import eist.tum_social.tum_social.persistent_data_storage.util.DatabaseEntity;
import eist.tum_social.tum_social.persistent_data_storage.util.PrimaryKey;

@DatabaseEntity(tableName = "DegreePrograms")
public class DegreeProgram {

    @PrimaryKey
    private int id;
    private String name;
    private String description;
    @ColumnMapping(columnName = "degreeLevelId", isForeignKey = true, foreignKey = "id")
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
