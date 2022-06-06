package eist.tum_social.tum_social.location;

public class OpenStreetmapLocation {
    private final String name;
    private final Coordinate coords;
    private final BoundingBox bbox;

    public OpenStreetmapLocation(String name, BoundingBox bbox, Coordinate coords) {
        this.name = name;
        this.bbox = bbox;
        this.coords = coords;
    }

    public String getName() {
        return name;
    }

    public BoundingBox getBbox() {
        return bbox;
    }

    public Coordinate getCoords() {
        return coords;
    }
}
