package AbstractAndGenerics;

public class Park extends Point {
    private String name;

    public Park(String name, String coordinates) {
        super(coordinates);
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " National Park";
    }
}
