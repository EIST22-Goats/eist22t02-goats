package eist.tum_social;

import eist.tum_social.tum_social.controllers.IndexController;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(IndexController.class)
@ContextConfiguration(classes = IndexController.class)
class TestTest {

    @Test
    public void testSayHello() throws Exception {
        Storage storage = new Storage();

        Person person = storage.getPerson("ge47bit");
        System.out.println(person);

    }

}
