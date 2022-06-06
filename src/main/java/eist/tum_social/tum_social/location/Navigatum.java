package eist.tum_social.tum_social.location;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static eist.tum_social.tum_social.location.util.Requests.escapeQueryValue;
import static eist.tum_social.tum_social.location.util.Requests.getRequest;

public class Navigatum {

    public static void main(String[] args) {
        List<Room> rooms = findRooms("interims");

        for (Room room:rooms) {
            System.out.println(room.getRoomId()+" "+room.getName()+" "+room.getBuilding());
            System.out.println(getRoomCoords(room.getRoomId()));
        }

    }

    public static List<Room> findRooms(String query) {
        String sURL = "https://nav.tum.sexy/api/search?q=" + escapeQueryValue(query);

        JsonObject rootobj = getRequest(sURL).getAsJsonObject();
        JsonArray sections = rootobj.get("sections").getAsJsonArray();
        
        JsonObject roomsFacet = getJsonFacet(sections, "rooms");
        if (roomsFacet == null) {
            return new ArrayList<>();
        }
        JsonArray roomEntries = roomsFacet.get("entries").getAsJsonArray();
        return jsonToRooms(roomEntries);
    }

    public static Coordinate getRoomCoords(String roomId) {
        String sURL = "https://nav.tum.sexy/api/get/" + roomId;

        JsonObject rootobj = getRequest(sURL).getAsJsonObject();
        JsonObject jsonCoords = rootobj.get("coords").getAsJsonObject();
        Coordinate coords = new Coordinate();
        coords.setLatitude(jsonCoords.get("lat").getAsString());
        coords.setLongitude(jsonCoords.get("lon").getAsString());
        return coords;
    }

    public static JsonObject getJsonFacet(JsonArray array, String name) {
        for (var section:array) {
            var sectionObject = section.getAsJsonObject();
            if (sectionObject.get("facet").getAsString().equals(name)) {
                return sectionObject;
            }
        }
        return null;
    }

    public static List<Room> jsonToRooms(JsonArray json) {
        List<Room> rooms = new ArrayList<>();
        for (var entry:json) {
            rooms.add(jsonToRoom(entry.getAsJsonObject()));
        }
        return rooms;
    }

    public static Room jsonToRoom(JsonObject json) {
        String id = json.get("id").getAsString();
        String name = json.get("name").getAsString();
        String room = json.get("subtext").getAsString();
        return new Room(id, name, room);
    }

}