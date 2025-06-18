package AbstractAndGenerics;

import java.util.Arrays;

public interface Mappable {
    //mtd = public, abstract
    void render();

    static double[] stringToLatLon(String coordinates) {
        var splits = coordinates.split(",");
        double lat = Double.valueOf(splits[0]);
        double lon = Double.valueOf(splits[1]);
        return new double[]{lat, lon};
    }
}

abstract class Point implements Mappable {
    private double[] coordinates = new double[2];

    public Point(String coordinates) {
        this.coordinates = Mappable.stringToLatLon(coordinates);
    }

    @Override
    public void render() {
        //this = string representation of the class calling this mtd
        System.out.println("Render " + this + " as POINT (" + location() + ")");
    }

    private String location() {
        return Arrays.toString(coordinates);
    }
}

abstract class Line implements Mappable {
    private double[][] coordinates;

    public Line(String... coordinates) {
        this.coordinates = new double[coordinates.length][];
        int index = 0;
        for (var coor : coordinates) {
            this.coordinates[index++] = Mappable.stringToLatLon(coor);
        }
    }

    @Override
    public void render() {
        //this = string representation of the class calling this mtd
        System.out.println("Render " + this + " as LINE (" + locations() + ")");
    }

    private String locations() {
        return Arrays.deepToString(coordinates);
    }
}
