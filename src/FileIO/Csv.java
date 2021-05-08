package FileIO;

import Main.Controller;
import Tree.TreeNode;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Csv {
    public static LinkedHashMap<Integer, TreeNode> people = Controller.people;
    public static void loadCsvFile(AnchorPane mainAnchorPane, Pane informationPane) throws IOException {
        mainAnchorPane.getChildren().removeIf(n -> n.getId() == null || n.getId().equals("addPersonBtn"));
        // Remove all nodes on screen
        Controller.clearVariablesAndScreen();

        BufferedReader csvReader = new BufferedReader(new FileReader("FamilyTree.csv"));
        String row = csvReader.readLine(); // read first line which contains column names
        while ((row = csvReader.readLine()) != null) {
            int id, spouseId = -1, level;
            String firstName, lastName, birthPlace, birthDate;
            ArrayList<Integer> childrenIds = new ArrayList<Integer>();
            ArrayList <Integer> parentIds = new ArrayList<Integer>(2);
            String[] data = row.split(",");
            id = Integer.parseInt(data[0]);
            firstName = data[1].replace(" ", "");
            lastName = data[2];
            birthPlace = data[3];
            birthDate = data[4];
            String[] chars = data[5].replaceAll("\\[","").replaceAll("]","").split(" ");
            for (String aChar : chars) {
                if (!aChar.equals("")) parentIds.add(Integer.parseInt(aChar));
            }
            chars = data[6].replaceAll("\\[","").replaceAll("]","").split(" ");
            for (String aChar : chars) {
                if (!aChar.equals("")) childrenIds.add(Integer.parseInt(aChar));
            }
            spouseId = Integer.parseInt(data[7]);
            level = Integer.parseInt(data[8]);

            people.put(id, new TreeNode(id,300, 80 + level*250, mainAnchorPane, informationPane, level, spouseId));
            Controller.peopleCount++;
            people.get(id).saveInformation(id, firstName, lastName, LocalDate.parse(birthDate), birthPlace);
            people.get(id).addChildrenSpouseParentsFromFile(childrenIds, spouseId, parentIds);
        }
        for (int i = 0; i < Controller.levels.length; i++) TreeNode.rearrangeLevel(people, mainAnchorPane, i);
//        Controller.printArray(Controller.levels);
//        System.out.println("\n\n");
        TreeNode.drawLines(mainAnchorPane);
        csvReader.close();
    }
}
