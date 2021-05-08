package Tree;

import Main.Controller;
import javafx.scene.layout.AnchorPane;
import org.apache.pdfbox.debugger.ui.Tree;

import javax.swing.event.AncestorEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class MakeRoot implements Filter{
    static LinkedHashMap<Integer, TreeNode> people = Controller.people;
    static ArrayList<ArrayList<Integer>> levels;
    static ArrayList<ArrayList<Integer>> oldLevels;
    TreeNode rootNode;

    public static void filterNodes(int id, AnchorPane mainPane) {
        levels = Controller.levels;
        oldLevels = Controller.makeEmptyArrayList(20);
        // Save previous levels array to oldLevels
        for (int i = 0; i < levels.size(); i++) {
            oldLevels.set(i, (ArrayList<Integer>) levels.get(i).clone());
        }

        // Get root node's main info
        TreeNode rootNode = people.get(id);
        int level = rootNode.level;
        int spouseId = rootNode.info.getSpouseId();

        // Remove all nodes above root from levels array and make invisible
        for (int i = 0; i < level; i++) {
            ArrayList <Integer> floor = levels.get(i);
            for (int o = 0; o < floor.size(); o++) {
                if (floor.get(o) != -1) {
                    people.get(floor.get(o)).setVisibleNode(false);
                    floor.set(o, -1);
                }
            }
        }

        // Set all nodes and their children invisible, except spouse
        // and self, at the same level
        for (int i = 0; i < levels.get(level).size(); i++) {
            if (levels.get(level).get(i) != spouseId && levels.get(level).get(i) != -1 && levels.get(level).get(i) != id) {
                recursiveRemove(levels.get(level).get(i));
            }
        }
        // Rearrange nodes after removal
        for (int i = 0; i < levels.size(); i++) TreeNode.rearrangeLevel(people, mainPane, i);
        TreeNode.drawLines(mainPane);
    }

    public static void unfilterNodes(AnchorPane mainPane) {
        if (oldLevels == null) levels = Controller.levels; // If wasn't filtered before
        else {
            Controller.levels = oldLevels;
            levels = oldLevels;
        }
        //Controller.printArray(levels);
        // Iterate through floors
        for (int i = 0; i < levels.size(); i++) {
            // Iterate through floor's nodes
            for (int o = 0; o < levels.get(i).size(); o++) {
                if (levels.get(i).get(o) != -1) people.get(levels.get(i).get(o)).setVisibleNode(true);
            }
            TreeNode.rearrangeLevel(people, mainPane, i);
        }
        TreeNode.drawLines(mainPane);
    }

    private static void recursiveRemove(int id) {
        TreeNode node = people.get(id);
        // Remove self
        removeNodeFromLevel(id, node.level);
        // Remove spouse
        if (node.info.getSpouseId() != -1) {
            TreeNode spouse = people.get(node.info.getSpouseId());
            removeNodeFromLevel(spouse.info.getId(), spouse.level);
        }
        // Remove children (recursive)
        for (int childId : node.info.getChildren()) {
            recursiveRemove(childId);
        }
    }
    private static void removeNodeFromLevel(int id, int level) {
        for (int i = 0; i < levels.get(level).size(); i++) {
            if (levels.get(level).get(i) == id) {
                levels.get(level).set(i, -1);
                people.get(id).setVisibleNode(false);
            }
        }
    }
}
