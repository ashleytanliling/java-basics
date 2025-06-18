package Interface;

enum Geometry {LINE, POINT, POLYGON}

enum Color {BLACK, BLUE, GREEN, ORANGE, RED}

enum PointMarker {CIRCLE, PUSH_PIN, STAR, SQUARE, TRIANGLE} //for Building

enum LineMarker {DASHED, DOTTED, SOLID}                     //for UtilityLine

public interface Mappable {
    //field = public, static, final - i.e. constant that everyone can access
    String JSON_PROPERTY = """
            "properties" : {%s} """;

    //method w/o body = public, abstract
    String getLabel();

    String getMarker();

    Geometry getShape();

    //concrete method (note: can call abstract method above, from this concrete method)
    //default - prints default String if implementing classes dun override
    default String toJSON() {
        return """
                "type": "%s", "label": "%s", "marker": "%s" """
                .formatted(getShape(), getLabel(), getMarker());
    }

    //1) JSON_PROPERTY - format JSON_PROPERTY static String field
    //2) mappable.toJSON() - JSON_PROPERTY String will then be replaced by String we get back from .toJSON() concrete mtd
    static void mapIt(Mappable mappable) {
        System.out.println(JSON_PROPERTY.formatted(mappable.toJSON()));
    }
}
