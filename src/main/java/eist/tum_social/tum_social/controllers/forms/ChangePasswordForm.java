package eist.tum_social.tum_social.controllers.forms;

import eist.tum_social.tum_social.controllers.AuthenticationController;
import eist.tum_social.tum_social.model.Person;

public class ChangePasswordForm {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void apply(Person person) {
        String hashedPassword = AuthenticationController.hashPassword(password);
        person.setPassword(hashedPassword);
    }
}
