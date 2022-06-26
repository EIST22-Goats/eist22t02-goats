package eist.tum_social.tum_social.datastorage;


import eist.tum_social.tum_social.model.*;

import java.util.List;

/**
 * The interface for the data management.
 */
public interface StorageFacade {

    Person getPerson(String tumId);
    List<Person> getPersons();

    DegreeProgram getDegreeProgram(String degreeProgramName);
    List<DegreeProgram> getDegreePrograms();

    Course getCourse(int courseId);
    List<Course> getCourses();

    Appointment getAppointment(int id);

    Announcement getAnnouncement(int id);

    Comment getComment(int commentId);

    List<ChatMessage> getChatMessages(int person1Id, int person2Id);

    public List<ChatMessage> getChatMessages(int personId);

    void update(Object object);

    void delete(Object object);

}
