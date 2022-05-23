package eist.tum_social.tum_social.model;

public class Student extends Person {

    private int matriculationNr;
    private int semester;
    private DegreeProgram degreeProgram;

    public int getMatriculationNr() {
        return matriculationNr;
    }

    public void setMatriculationNr(int matriculationNr) {
        this.matriculationNr = matriculationNr;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public DegreeProgram getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(DegreeProgram degreeProgram) {
        this.degreeProgram = degreeProgram;
    }
}
