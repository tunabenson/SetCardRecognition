package Filters.kmeans;

public class Point {
    private short r,g,b;

    public Point(short r, short g, short b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public String toString() {
        return "Point{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }

    public double distanceTo(Point other){
        double rDiff = r-other.r;
        double gDiff = g-other.g;
        double bDiff = b-other.b;

        return Math.sqrt(rDiff*rDiff + gDiff*gDiff + bDiff*bDiff);
    }
    public short getR() {
        return r;
    }

    public void setR(short r) {
        this.r = r;
    }

    public short getG() {
        return g;
    }

    public void setG(short g) {
        this.g = g;
    }

    public short getB() {
        return b;
    }

    public void setB(short b) {
        this.b = b;
    }
}
