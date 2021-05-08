package Tree;

import javafx.scene.layout.AnchorPane;

public interface Filter {
    static void filterNodes(int id, AnchorPane mainPane){}
    static void unfilterNodes(AnchorPane mainPane) {}
}
