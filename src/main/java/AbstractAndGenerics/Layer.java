package AbstractAndGenerics;

import java.util.ArrayList;
import java.util.List;

//can use mtds on Mappable w/o casting
public class Layer<T extends Mappable> {    //upper bound
    private List<T> layerElements;

    public Layer(T[] layerElements) {
        this.layerElements = new ArrayList<>(List.of(layerElements));
    }

    public void addElements(T... elements) {
        layerElements.addAll(List.of(elements));
    }

    public void renderLayer() {
        for (T elt : layerElements) {
            elt.render();
        }
    }
}
