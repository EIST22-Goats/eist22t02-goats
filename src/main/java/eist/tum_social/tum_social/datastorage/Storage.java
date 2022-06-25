package eist.tum_social.tum_social.datastorage;

import eist.tum_social.tum_social.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
public class Storage implements StorageFacade {

    private final Database db;
    public Storage(@Autowired Database db) {
        this.db = db;
    }

    @Override
    public Person getPerson(String tumId) {
        return firstOrNull(db.select(Person.class, "tumId='" + tumId + "'"));
    }

    @Override
    public List<Person> getPersons() {
        return db.select(Person.class, "1");
    }

    @Override
    public DegreeProgram getDegreeProgram(String degreeProgramName) {
        return firstOrNull(db.select(DegreeProgram.class, "name='" + degreeProgramName + "'"));
    }

    @Override
    public List<DegreeProgram> getDegreePrograms() {
        return db.select(DegreeProgram.class, "1");
    }

    @Override
    public Course getCourse(int courseId) {
        return firstOrNull(db.select(Course.class, "id=" + courseId));
    }

    @Override
    public List<Course> getCourses() {
        return db.select(Course.class, "1");
    }

    @Override
    public Appointment getAppointment(int id) {
        return firstOrNull(db.select(Appointment.class, "id=" + id));
    }

    public List<Appointment> getAppointments() {
        return db.select(Appointment.class, "1");
    }


    public Comment getComment(int id) {
        return firstOrNull(db.select(Comment.class, "id=" + id));
    }

    public Announcement getAnnouncement(int id) {
        return firstOrNull(db.select(Announcement.class, "id=" + id));
    }

    public List<ChatMessage> getChatMessages(int person1Id, int person2Id) {
        return db.select(ChatMessage.class,
                "(receiverId=" + person1Id + " AND senderId=" + person2Id + ") OR ("
                        + "receiverId=" + person2Id + " AND senderId=" + person1Id + ")");
    }

    public List<ChatMessage> getChatMessages(int personId) {
        return db.select(ChatMessage.class, "receiverId=" + personId + " OR senderId=" + personId);
    }

    @Override
    public void update(Object object) {
        db.update(object);
    }

    @Override
    public void delete(Object object) {
        db.delete(object);
    }

    private <T> T firstOrNull(List<T> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static String getResourcePath() {
        return new File("src/main/resources/static/").getAbsolutePath();
    }
    public void copyFile(String src, String dst) {
        try {
            Files.copy(Paths.get(src), Paths.get(dst), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
