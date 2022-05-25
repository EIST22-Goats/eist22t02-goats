package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.database.util.ColumnMapping;
import eist.tum_social.tum_social.database.util.DatabaseEntity;
import eist.tum_social.tum_social.database.util.PrimaryKey;

@DatabaseEntity(tableName = "DegreePrograms")
public class DegreeProgram {

    @PrimaryKey
    private int id;
    private String name;
    private String description;
    @ColumnMapping(columnName = "degreeLevelId", isForeignKey = true)
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
