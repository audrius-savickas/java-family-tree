package Tree;

import Main.Controller;
import javafx.scene.layout.AnchorPane;
import org.apache.pdfbox.debugger.ui.Tree;

import javax.swing.event.AncestorEvent;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class MakeRoot implements Filter{
    static LinkedHashMap<Integer, TreeNode> people = Controller.people;
    static int[][] levels;
    static int[][] oldLevels;
    TreeNode rootNode;

    public static void filterNodes(int id, AnchorPane mainPane) {
        levels = Controller.levels;
        oldLevels = new int[20][10];
        // Save previous levels array to oldLevels
        for (int i = 0; i < levels.length; i++) oldLevels[i] = Arrays.copyOf(levels[i], levels[i].length);

        // Get root node's main info
        TreeNode rootNode = people.get(id);
        int level = rootNode.level;
        int spouseId = rootNode.info.getSpouseId();

        // Remove all nodes above root from levels array and make invisible
        for (int i = 0; i < level; i++) {
            int[] floor = levels[i];
            for (int o = 0; o < floor.length; o++) {
                if (floor[o] != -1) {
                    people.get(floor[o]).setVisibleNode(false);
                    floor[o] = -1;
                }
            }
        }

        // Set all nodes and their children invisible, except spouse
        // and self, at the same level
        for (int i = 0; i < levels[level].length; i++) {
            if (levels[level][i] != spouseId && levels[level][i] != -1 && levels[level][i] != id) {
                recursiveRemove(levels[level][i]);
            }
        }
        // Rearrange nodes after removal
        for (int i = 0; i < levels.length; i++) TreeNode.rearrangeLevel(people, mainPane, i);
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
        for (int i = 0; i < levels.length; i++) {
            // Iterate through floor's nodes
            for (int o = 0; o < levels[i].length; o++) {
                if (levels[i][o] != -1) people.get(levels[i][o]).setVisibleNode(true);
            }
            TreeNode.rearrangeLevel(people, mainPane, i);
        }
        TreeNode.drawLines(mainPane);
        Controller.printArray(levels);
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
        for (int i = 0; i < levels[level].length; i++) {
            if (levels[level][i] == id) {
                levels[level][i] = -1;
                people.get(id).setVisibleNode(false);
            }
        }
    }
}
