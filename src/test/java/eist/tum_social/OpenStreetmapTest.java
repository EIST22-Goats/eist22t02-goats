package eist.tum_social;

import eist.tum_social.tum_social.location.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OpenStreetmapTest {

    @Test
    void findLocationExistingTest() {
        List<OpenStreetmapLocation> locations = OpenStreetMap.findLocation("Arcisstraße 21");
        assertFalse(locations.isEmpty());
        OpenStreetmapLocation location = locations.get(0);
        assertEquals("Technische Universität München, 21, Arcisstraße," +
                " Am alten nördlichen Friedhof," +
                " Königsplatz, Maxvorstadt, München," +
                " Bayern, 80333, Deutschland", location.getName());
        assertEquals(48.1477055,Double.parseDouble(location.getBbox().getTop()), 0.000000001);
        assertEquals(11.5699075, Double.parseDouble(location.getBbox().getRight()), 0.000000001);
        assertEquals(48.1514056, Double.parseDouble(location.getBbox().getBottom()), 0.000000001);
        assertEquals(11.5656093, Double.parseDouble(location.getBbox().getLeft()), 0.000000001);
        assertEquals(48.149554550000005, Double.parseDouble(location.getCoords().getLatitude()), 0.000000001);
        assertEquals(11.567753141783761, Double.parseDouble(location.getCoords().getLongitude()), 0.000000001);
    }

    @Test
    void findLocationNonExistingTest() {
        List<OpenStreetmapLocation> locations = OpenStreetMap.findLocation("a galaxy far far away");
        assertTrue(locations.isEmpty());
    }

    @Test
    void createUrlLocationTest() {
        OpenStreetmapLocation location = OpenStreetMap.findLocation("Arcisstraße 21").get(0);
        assertEquals("https://www.openstreetmap.org/export/embed.html?"+
                "bbox=11.5656093%2C48.1477055%2C11.5699075%2C48.1514056&amp&layer=mapnik&"+
                "marker=48.149554550000005%2C11.567753141783761", OpenStreetMap.createUrl(location));
    }

    @Test
    void createUrlRoomTest() {
        Room room = Navigatum.findRooms("00.11.038").get(0);
        assertEquals("https://www.openstreetmap.org/export/embed.html?"+
                "bbox=11.667287010131773%2C48.262924450158145%2C11.667289010131771%2C48.26292645015814&amp&layer=mapnik&"+
                "marker=48.26292545015814%2C11.667288010131772", OpenStreetMap.createUrl(room));
    }

    @Test
    void createUrlBboxAndCoordsTest() {
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude("42");
        coordinate.setLongitude("24");
        assertEquals("https://www.openstreetmap.org/export/embed.html?"+
                "bbox=0%2C10%2C20%2C30&amp&layer=mapnik&"+
                "marker=42%2C24", OpenStreetMap.createUrl(
                        new BoundingBox("0", "10", "20", "30"),
                        coordinate
        ));
    }

    @Test
    void getBaseUrlTest() {
        assertEquals("https://www.openstreetmap.org/export/embed.html", OpenStreetMap.getBaseUrl());
    }
}
