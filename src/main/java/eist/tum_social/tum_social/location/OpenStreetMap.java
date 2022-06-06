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
                json.get(0).getAsString(),
                json.get(1).getAsString(),
                json.get(2).getAsString(),
                json.get(3).getAsString()
        );
    }

    public static String createUrl(OpenStreetmapLocation location) {
        return createUrl(location.getBbox(), location.getCoords());
    }

    public static String createUrl(Room room) {
        Coordinate coords = Navigatum.getRoomCoords(room.getRoomId());
        return createUrl(boundingBoxFrom(coords), coords);
    }

    public static String createUrl(BoundingBox boundingBox, Coordinate coordinate) {
        return "https://www.openstreetmap.org/export/embed.html?" + bboxString(boundingBox) + "&amp;layer=mapnik&amp;marker=" + coordinateString(coordinate);
    }

    public static BoundingBox boundingBoxFrom(Coordinate coords) {
        double latitude = Double.parseDouble(coords.getLatitude());
        double longitude = Double.parseDouble(coords.getLongitude());

        String upperLeftLatitude = String.valueOf((latitude - 0.0001));
        String upperLeftLongitude = String.valueOf(longitude - 0.0001);
        String lowerRightLatitude = String.valueOf(latitude + 0.0001);
        String lowerRightLongitude = String.valueOf(latitude + 0.0001);

        return new BoundingBox(
                upperLeftLatitude,
                upperLeftLongitude,
                lowerRightLatitude,
                lowerRightLongitude
        );
    }

    public static String bboxString(BoundingBox boundingBox) {
        return "bbox=" + boundingBox.getLowerRightLatitude() + "%2C" +
                boundingBox.getUpperLeftLatitude() + "%2C" +
                boundingBox.getLowerRightLongitude() + "%2C" +
                boundingBox.getUpperLeftLongitude();
    }

    public static String coordinateString(Coordinate coordinate) {
        return coordinate.getLatitude() + "%2C" + coordinate.getLongitude();
    }
}
