package eist.tum_social;

import eist.tum_social.tum_social.controllers.IndexController;
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
        assertEquals(42, 42);
    }

}
