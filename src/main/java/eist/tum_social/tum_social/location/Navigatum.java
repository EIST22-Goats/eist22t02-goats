package eist.tum_social.tum_social.location;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eist.tum_social.tum_social.datastorage.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.location.util.Requests.escapeQueryValue;
import static eist.tum_social.tum_social.location.util.Requests.getRequest;

public class Navigatum {

    public static void main(String[] args) {
        List<Room> rooms = findRooms("galileo");

        for (Room room:rooms) {
            System.out.println(room.getRoomId()+" "+room.getName()+" "+room.getBuilding());
        }
        System.out.println(getRoomImageData(rooms.get(0).getRoomId()));
        System.out.println(getRoomCoords(rooms.get(0).getRoomId()));

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

    public static Pair<String, Marker> getRoomImageData(String roomId) {
        String sURL = "https://nav.tum.sexy/api/get/" + roomId;

        JsonObject root = getRequest(sURL).getAsJsonObject();

        JsonObject jsonObject = root.get("maps").getAsJsonObject();

        if (jsonObject.keySet().contains("roomfinder")) {
            JsonObject jsonEntry = jsonObject
                    .get("roomfinder").getAsJsonObject()
                    .get("available").getAsJsonArray()
                    .get(0).getAsJsonObject();

            String fileName = jsonEntry.get("file").getAsString();
            String source = jsonEntry.get("source").getAsString().toLowerCase();
            String url = "https://nav.tum.sexy/cdn/maps/" + source + "/" + fileName;

            int width = jsonEntry.get("width").getAsInt();
            int height = jsonEntry.get("height").getAsInt();
            int markerX = jsonEntry.get("x").getAsInt();
            int markerY = jsonEntry.get("y").getAsInt();

            Marker marker = new Marker((double) markerX / width * 100, (double) markerY / height * 100);

            return new Pair<>(url, marker);
        } else {
            return null;
        }
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