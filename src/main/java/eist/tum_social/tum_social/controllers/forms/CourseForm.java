package eist.tum_social.tum_social.controllers.forms;


import eist.tum_social.tum_social.model.Course;

public class CourseForm extends Form<Course> {

    private String name;
    private String acronym;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
