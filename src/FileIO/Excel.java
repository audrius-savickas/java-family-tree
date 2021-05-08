package FileIO;

import Main.Controller;
import Tree.TreeNode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Excel {
    public static LinkedHashMap<Integer, TreeNode> people = Controller.people;
    public static void loadExcelFile(AnchorPane mainAnchorPane, Pane informationPane) throws IOException {
        mainAnchorPane.getChildren().removeIf(n -> n.getId() == null || n.getId().equals("addPersonBtn"));
        // Remove all nodes on screen
        Controller.clearVariablesAndScreen();

        FileInputStream file = new FileInputStream(new File("FamilyTree.xlsx"));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        sheet.removeRow(sheet.getRow(0)); // Delete the row with the column names
        for (Row row : sheet) {
            int id, spouseId = -1, level;
            String firstName, lastName, birthPlace, birthDate;
            ArrayList<Integer> childrenIds = new ArrayList<Integer>();
            ArrayList<Integer> parentIds = new ArrayList<Integer>(2);

            id = (int)row.getCell(0).getNumericCellValue();
            firstName = row.getCell(1).getStringCellValue().replace(" ", "");
            lastName = row.getCell(2).getStringCellValue();
            birthPlace = row.getCell(3).getStringCellValue();
            birthDate = row.getCell(4).getStringCellValue();
            String [] chars = row.getCell(5).getStringCellValue().split(", ");
            for (String aChar : chars) if (!aChar.equals("")) parentIds.add(Integer.parseInt(aChar));
            chars = row.getCell(6).getStringCellValue().split(", ");
            for (String aChar : chars) if (!aChar.equals("")) childrenIds.add(Integer.parseInt(aChar));
            spouseId = (int) row.getCell(7).getNumericCellValue();
            level = (int) row.getCell(8).getNumericCellValue();

            people.put(id, new TreeNode(300, 80 + level*250, mainAnchorPane, informationPane, level, spouseId));
            Controller.peopleCount++;
            people.get(id).saveInformation(id, firstName, lastName, LocalDate.parse(birthDate), birthPlace);
            people.get(id).addChildrenSpouseParentsFromFile(childrenIds, spouseId, parentIds);
            TreeNode.rearrangeLevel(people, mainAnchorPane,level);
        }
        TreeNode.drawLines(mainAnchorPane);
        file.close();
    }
}
