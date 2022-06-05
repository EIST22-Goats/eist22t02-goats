package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.LazyDataStorage.ForeignEntity;
import eist.tum_social.tum_social.persistent_data_storage.util.ForeignTable;
import eist.tum_social.tum_social.persistent_data_storage.util.DatabaseEntity;

@DatabaseEntity(tableName = "DegreePrograms")
public class DegreeProgram extends UniquelyIdentifiable {

    private int id = -1;
    private String name;
    private String description;
    @ForeignTable(
            foreignTableName = "DegreeLevel",
            ownColumnName = "degreeLevelId")
    private ForeignEntity<DegreeLevel> degreeLevelEntity;

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

    public ForeignEntity<DegreeLevel> getDegreeLevelEntity() {
        return degreeLevelEntity;
    }

    public DegreeLevel getDegreeLevel() {
        return degreeLevelEntity.get();
    }

    public void setDegreeLevelEntity(ForeignEntity<DegreeLevel> degreeLevel) {
        this.degreeLevelEntity = degreeLevel;
    }

}
