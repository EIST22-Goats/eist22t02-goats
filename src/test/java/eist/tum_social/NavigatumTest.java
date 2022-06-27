package eist.tum_social;

import eist.tum_social.tum_social.datastorage.util.Pair;
import eist.tum_social.tum_social.location.Coordinate;
import eist.tum_social.tum_social.location.Marker;
import eist.tum_social.tum_social.location.Navigatum;
import eist.tum_social.tum_social.location.Room;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NavigatumTest {

    @Test
    void findRoomsExistingTest() {
        List<Room> rooms = Navigatum.findRooms("00.11.038");
        assertFalse(rooms.isEmpty());
        Room room = rooms.get(0);
        assertEquals("5611.EG.038", room.getRoomId());
    }

    @Test
    void findRoomsNonExistingTest() {
        List<Room> rooms = Navigatum.findRooms("898878");
        assertTrue(rooms.isEmpty());
    }

    @Test
    void getRoomCoordsTest() {
        Coordinate coordinate = Navigatum.getRoomCoords("5611.EG.038");
        assertEquals(48.26292545015814, Double.parseDouble(coordinate.getLatitude()), 0.000000001);
        assertEquals(11.667288010131772, Double.parseDouble(coordinate.getLongitude()), 0.000000001);
    }

    @Test
    void getRoomImageDataTest() {
        Pair<String, Marker> roomImageData = Navigatum.getRoomImageData("5611.EG.038");
        assertEquals("https://nav.tum.sexy/cdn/maps/roomfinder/rf91.webp", roomImageData.first());
        assertEquals(49.152542372881356, roomImageData.second().getX(), 0.000000001);
        assertEquals(61.65289256198348, roomImageData.second().getY(), 0.000000001);
    }
}
