package eist.tum_social;

import eist.tum_social.tum_social.location.OpenStreetMap;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenStreetmapTest {

    @Disabled
    @Test
    void findLocationTest() {
        // TODO
    }

    @Disabled
    @Test
    void createUrlLocationTest() {
        // TODO
    }

    @Disabled
    @Test
    void createUrlRoomTest() {
        // TODO
    }

    @Disabled
    @Test
    void createUrlBboxAndCoordsTest() {
        // TODO
    }

    @Test
    void getBaseUrlTest() {
        assertEquals("https://www.openstreetmap.org/export/embed.html", OpenStreetMap.getBaseUrl());
    }
}
