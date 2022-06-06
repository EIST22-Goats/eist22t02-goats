package eist.tum_social.tum_social.location;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static eist.tum_social.tum_social.location.util.Requests.escapeQueryValue;
import static eist.tum_social.tum_social.location.util.Requests.getRequest;

public class OpenStreetMap {
    public static void main(String[] args) {
        List<OpenStreetmapLocation> locations = findLocation("forschungszentrum garching");

        for (OpenStreetmapLocation location : locations) {
            System.out.println(location.getCoords() + bboxString(location.getBbox()));
        }

    }

    public static List<OpenStreetmapLocation> findLocation(String query) {
        String sURL = "https://nominatim.openstreetmap.org/search.php?format=json&q=" + escapeQueryValue(query);

        List<OpenStreetmapLocation> results = new ArrayList<>();

        JsonArray rootobj = getRequest(sURL).getAsJsonArray();
        for (var result : rootobj) {
            JsonObject resultObject = result.getAsJsonObject();
            OpenStreetmapLocation openStreetmapLocation = jsonToLocation(resultObject);
            results.add(openStreetmapLocation);
        }

        return results;
    }

    private static OpenStreetmapLocation jsonToLocation(JsonObject json) {
        String latitude = json.get("lat").getAsString();
        String longitude = json.get("lon").getAsString();

        Coordinate coords = new Coordinate();
        coords.setLatitude(latitude);
        coords.setLongitude(longitude);

        String name = json.get("display_name").getAsString();
        JsonArray jsonBoundingbox = json.get("boundingbox").getAsJsonArray();

        System.out.println(">>> json bbox: "+jsonBoundingbox);

        BoundingBox boundingBox = jsonToBoundingBox(jsonBoundingbox);

        return new OpenStreetmapLocation(name, boundingBox, coords);
    }

    public static BoundingBox jsonToBoundingBox(JsonArray json) {
        return new BoundingBox(
                json.get(2).getAsString(),
                json.get(0).getAsString(),
                json.get(3).getAsString(),
                json.get(1).getAsString()
        );
    }

    public static String createUrl(OpenStreetmapLocation location) {
        return createUrl(location.getBbox(), location.getCoords());
    }

    public static String createUrl(Room room) {
        Coordinate coords = Navigatum.getRoomCoords(room.getRoomId());
        return createUrl(boundingBoxFromCoordinate(coords), coords);
    }

    public static String createUrl(BoundingBox boundingBox, Coordinate coordinate) {
        return getBaseUrl() + "?" + bboxString(boundingBox) + "&amp&layer=mapnik&marker=" + coordinateString(coordinate);
    }

    public static String getBaseUrl() {
        return "https://www.openstreetmap.org/export/embed.html";
    }

    public static BoundingBox boundingBoxFromCoordinate(Coordinate coords) {
        double latitude = Double.parseDouble(coords.getLatitude());
        double longitude = Double.parseDouble(coords.getLongitude());

        String left = String.valueOf(longitude - 0.000001);
        String top = String.valueOf(latitude - 0.000001);
        String right = String.valueOf(longitude + 0.000001);
        String bottom = String.valueOf(latitude + 0.000001);

        return new BoundingBox(left, top, right, bottom);
    }

    public static String bboxString(BoundingBox boundingBox) {
        return "bbox=" + boundingBox.getLeft() + "%2C" +
                boundingBox.getTop() + "%2C" +
                boundingBox.getRight() + "%2C" +
                boundingBox.getBottom();
    }

    public static String coordinateString(Coordinate coordinate) {
        return coordinate.getLatitude() + "%2C" + coordinate.getLongitude();
    }
}
