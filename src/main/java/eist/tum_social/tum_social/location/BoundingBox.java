package eist.tum_social.tum_social.location;

import net.bytebuddy.asm.Advice;

public class BoundingBox {

    private String upperLeftLatitude;
    private String upperLeftLongitude;
    private String lowerRightLatitude;
    private String lowerRightLongitude;

    public BoundingBox(String upperLeftLatitude, String upperLeftLongitude, String lowerRightLatitude, String lowerRightLongitude) {
        this.upperLeftLatitude = upperLeftLatitude;
        this.upperLeftLongitude = upperLeftLongitude;
        this.lowerRightLatitude = lowerRightLatitude;
        this.lowerRightLongitude = lowerRightLongitude;
    }

    public String getUpperLeftLatitude() {
        return upperLeftLatitude;
    }

    public void setUpperLeftLatitude(String upperLeftLatitude) {
        this.upperLeftLatitude = upperLeftLatitude;
    }

    public String getUpperLeftLongitude() {
        return upperLeftLongitude;
    }

    public void setUpperLeftLongitude(String upperLeftLongitude) {
        this.upperLeftLongitude = upperLeftLongitude;
    }

    public String getLowerRightLatitude() {
        return lowerRightLatitude;
    }

    public void setLowerRightLatitude(String lowerRightLatitude) {
        this.lowerRightLatitude = lowerRightLatitude;
    }

    public String getLowerRightLongitude() {
        return lowerRightLongitude;
    }

    public void setLowerRightLongitude(String lowerRightLongitude) {
        this.lowerRightLongitude = lowerRightLongitude;
    }
}
