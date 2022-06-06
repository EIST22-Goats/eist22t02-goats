package eist.tum_social.tum_social.location;

public class Marker {
    private double x;
    private double y;

    public Marker(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
