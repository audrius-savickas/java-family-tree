package Main;

import FileIO.Csv;
import FileIO.Excel;
import FileIO.Pdf;
import Tree.PersonInfo;
import Tree.TreeNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Controller {
    public static LinkedHashMap<Integer, TreeNode> people = new LinkedHashMap<Integer, TreeNode>();
    public static int peopleCount = 0;
    public static ArrayList<ArrayList<Integer>> levels = makeEmptyArrayList(20);
    public static int toChange;
    public static ArrayList<Line> lines = new ArrayList<Line>();

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Pane informationPane;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField birthPlaceInput;

    @FXML
    private DatePicker birthDateInput;

    @FXML
    private Button addPersonBtn;

    @FXML
    void addFirstPerson(ActionEvent event) {
        addPersonBtn.setDisable(true);
        addPersonBtn.setVisible(false);
        people.put(peopleCount, new TreeNode(peopleCount, (int) 650, 80, mainAnchorPane, informationPane, 0, -1));
        people.get(peopleCount).showInformationPane(informationPane);
        peopleCount++;
    }

    @FXML
    void onSaveInfo(ActionEvent event) {
        TreeNode node = people.get(toChange);
        node.getFirstNameText().setText(firstNameInput.getText());
        node.getLastNameText().setText(lastNameInput.getText());

        String firstName, lastName, birthPlace;
        int id;
        LocalDate birthDate;
        firstName = firstNameInput.getText();
        lastName = lastNameInput.getText();
        birthPlace = birthPlaceInput.getText();
        id = node.getId();
        birthDate = birthDateInput.getValue();
        if (birthDate == null) birthDate = LocalDate.of(1970, 1, 1);
        for (int i = 0; i < levels.size(); i++) {
            for (int o = 0; o < levels.get(i).size(); o++) {
                if (levels.get(i).get(o) == toChange) {
                    levels.get(i).set(o, id);
                }
            }
        }
        node.info = new PersonInfo(firstName, lastName, id, birthDate, birthPlace);
        node.saveInformation(id, firstName, lastName, birthDate, birthPlace);

        people.put(id, node);
        if (toChange != id) {
            people.remove(toChange);
        }

        informationPane.setVisible(false);
        int level = 0;

        if (TreeNode.partnerToChangeId != -1 && TreeNode.parentToChangeId == -1) level = node.level;
        else if (TreeNode.partnerToChangeId == -1 && TreeNode.parentToChangeId != -1) level = node.level-1;
        TreeNode.rearrangeLevel(people, mainAnchorPane, level);
        TreeNode.drawLines(mainAnchorPane);

    }

    @FXML
    void pdfSave(ActionEvent event) throws IOException {
        Pdf.saveToPdf(mainAnchorPane);
    }

    @FXML
    void csvLoad(ActionEvent event) throws IOException {
        Csv.loadCsvFile(mainAnchorPane, informationPane);
    }

    @FXML
    void csvSave(ActionEvent event) throws IOException {
        Writer writer = null;

        try {
            File file = new File("FamilyTree.csv");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("ID, First name, Last name, Birth place, Birth date, Parent IDs, Children IDs, Spouse ID, Level\n");
            for (Map.Entry<Integer, TreeNode> entry : people.entrySet()) {
                int key = entry.getKey();
                TreeNode value = entry.getValue();
                ArrayList <Integer> childrenIds = value.info.getChildren();
                ArrayList <Integer> parentIds = value.info.getParents();

                String text = value.info.getId() + ", " + value.info.getFirstName() + "," + value.info.getLastName() + "," + value.info.getBirthCity() + "," + value.info.getBirthDate().toString() + ",";
                text += "[";
                for (int i = 0; i < parentIds.size(); i++) {
                    text += parentIds.get(i);
                    if (parentIds.size() == 2 && i == 0) text += " ";
                }
                text += "],";
                text += "[";
                for (int i = 0; i < childrenIds.size(); i++) {
                    text += childrenIds.get(i).toString();
                    if (i != childrenIds.size() - 1) {
                        text += " ";
                    }
                }
                text += "]" + ",";
                text += value.info.getSpouseId() + ",";
                text += value.level;
                text += "\n";

                writer.write(text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            assert writer != null;
            writer.flush();
            writer.close();
        }
    }

    @FXML
    void xlsxLoad(ActionEvent event) throws IOException {
        Excel.loadExcelFile(mainAnchorPane, informationPane);
    }

    @FXML
    void xlsxSave(ActionEvent event) {
        Writer writer = null;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("FamilyTree");
        sheet.setDefaultColumnWidth(256*6);
        Row row = sheet.createRow(0);
        XSSFFont rowFont = workbook.createFont();
        XSSFCellStyle style = workbook.createCellStyle();
        rowFont.setBold(true);
        style.setFont(rowFont);


        row.createCell(0).setCellValue("ID");
        row.createCell(1).setCellValue("First name");
        row.createCell(2).setCellValue("Last name");
        row.createCell(3).setCellValue("Birth place");
        row.createCell(4).setCellValue("Birth date");
        row.createCell(5).setCellValue("Parent IDs");
        row.createCell(6).setCellValue("Children IDs");
        row.createCell(7).setCellValue("Spouse ID");
        row.createCell(8).setCellValue("Level");

        for (int i=0; i<9; i++) row.getCell(i).setCellStyle(style);

        int i = 0;
        for (Map.Entry<Integer, TreeNode> entry : people.entrySet()) {
            int key = entry.getKey();
            TreeNode value = entry.getValue();
            ArrayList <Integer> childrenIds = value.info.getChildren();
            ArrayList <Integer> parentIds = value.info.getParents();

            row = sheet.createRow(i++ + 1);
            row.createCell(0).setCellValue(key);
            row.createCell(1).setCellValue(value.info.getFirstName());
            row.createCell(2).setCellValue(value.info.getLastName());
            row.createCell(3).setCellValue(value.info.getBirthCity());
            row.createCell(4).setCellValue(value.info.getBirthDate().toString());
            row.createCell(5).setCellValue(parentIds.toString().replaceAll("\\[", "").replaceAll("]",""));
            row.createCell(6).setCellValue(childrenIds.toString().replaceAll("\\[", "").replaceAll("]",""));
            row.createCell(7).setCellValue(value.info.getSpouseId());
            row.createCell(8).setCellValue(value.level);
        }
        for (int o=0; o<9; o++) sheet.autoSizeColumn(o);

        try (FileOutputStream outputStream = new FileOutputStream("FamilyTree.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static int getArrayLength(ArrayList<Integer> arr) {
        int cnt = 0;
        int i = 0;
        for (int ints : arr) if (ints >= 0) cnt++;
        return cnt;
    }

    public static int[][] makeEmptyArray(int x, int y) {
        int[][] arr = new int[x][y];
        for (int[] ints : arr) {
            Arrays.fill(ints, -1);
        }
        return arr;
    }

    public static void pushToArr(ArrayList<Integer> arr, int value, int priority) {
        int n = getArrayLength(arr);
        if (priority == -1) {
            arr.set(n, value);
            return;
        }
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) == priority) {
                arr.add(i+1, value);
                arr.remove(arr.size()-1);
                return;
            }
        }
        arr.set(n, value);
    }

    public static void printArray(ArrayList<ArrayList<Integer>> arr) {
        for (int i = 0; i < arr.size(); i++) {
            System.out.println("\nLevel: " + i);
            for (int o = 0; o < arr.get(i).size(); o++) {
                System.out.print(arr.get(i).get(o) + " ");
            }
        }
        System.out.println("\n\n\n");
    }

    public static void clearVariablesAndScreen() {
        people.clear();
        Controller.peopleCount = 0;
        levels = null;
        levels = makeEmptyArrayList(20);
        toChange = 0;
        lines.clear();
    }

    public static int getLastFreeId() {
        int last = 0;
        for( int item : people.keySet() ) last = item;
        return last + 1;
    }

    public static ArrayList<ArrayList<Integer>> makeEmptyArrayList(int levels) {
        ArrayList<ArrayList<Integer> > aList = new ArrayList<ArrayList<Integer> >(levels);
        for (int i = 0; i < levels; i++) {
            ArrayList<Integer> a = new ArrayList<Integer>(10);
            for (int o = 0; o < 10; o++) {
                a.add(-1);
            }
            aList.add(a);
        }
        return aList;
    }
}
