package AbstractAndGenerics;

public class River extends Line {
    private String name;

    public River(String name, String... coordinates) {
        super(coordinates);
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " River";
    }
}
