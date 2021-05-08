package Tree;

import Main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import Main.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TreeNode {
    public static int parentToChangeId = -1;
    public static int partnerToChangeId = -1;

    public int level;
    public PersonInfo info;
    private int id;
    public int x, y;
    private ArrayList<Integer> childrenId = new ArrayList<Integer>();
    private int partnerId = -1;

    private ComboBox<String> dropDownMenu;
    private Pane nodePane;
    private Circle nodeCircle;
    private Text firstNameText;
    private Text lastNameText;


    public TreeNode(int id, int x, int y, AnchorPane mainPane, Pane informationPane, int level, int priority) {
        ObservableList<String> addPersonList = FXCollections.observableArrayList("Add child", "Add spouse", "Filter root", "Unfilter root", "Remove", "");
        nodePane = new Pane();
        nodeCircle = new Circle();
        dropDownMenu = new ComboBox<String>();
        HBox textBox1 = new HBox();
        HBox textBox2 = new HBox();
        firstNameText = new Text();
        lastNameText = new Text();
        info = new PersonInfo("First name", "Last name", id, LocalDate.now(), "Vilnius");
        parentToChangeId = -1;
        partnerToChangeId = -1;

        this.x = x;
        this.y = y;
        this.level = level;
        if (id == -1) this.id = Controller.peopleCount;
        else this.id = id;
        Controller.pushToArr(Controller.levels.get(level), id, priority);

        nodePane.setLayoutX(x);
        nodePane.setLayoutY(y);
        nodePane.setPrefWidth(200);
        nodePane.setPrefHeight(200);

        nodeCircle.setFill(Paint.valueOf("#dae3ec"));
        nodeCircle.setLayoutX(100);
        nodeCircle.setLayoutY(100);
        nodeCircle.setRadius(100);
        nodeCircle.setStroke(javafx.scene.paint.Color.BLACK);
        nodeCircle.setStrokeType(StrokeType.INSIDE);

        firstNameText.setText("First Name");
        firstNameText.setFont(Font.font("Baskerville Old Face", 34));
        firstNameText.setTextAlignment(TextAlignment.CENTER);

        lastNameText.setText("Last Name");
        lastNameText.setFont(Font.font("Baskerville Old Face", 34));
        lastNameText.setTextAlignment(TextAlignment.CENTER);

        textBox1.setAlignment(Pos.CENTER);
        textBox1.setLayoutX(0);
        textBox1.setLayoutY(50);
        textBox1.setMinWidth(200);
        textBox1.getChildren().add(firstNameText);

        textBox2.setAlignment(Pos.CENTER);
        textBox2.setLayoutX(0);
        textBox2.setLayoutY(90);
        textBox2.setMinWidth(200);
        textBox2.getChildren().add(lastNameText);

        dropDownMenu.setItems(addPersonList);
        dropDownMenu.setLayoutX(75);
        dropDownMenu.setLayoutY(139);
        dropDownMenu.setMaxWidth(15);
        dropDownMenu.setOnAction((ActionEvent actionEvent) -> {
            LinkedHashMap<Integer, TreeNode> people = Controller.people;

            int peopleCount = Controller.peopleCount;
            boolean doChangeInfo = false;
            int lastFreeId = Controller.getLastFreeId();

            if (dropDownMenu.getValue().equals("Add child")) {
                people.put(lastFreeId, new TreeNode(lastFreeId, x, y+250, mainPane, informationPane, level+1, -1));
                Controller.peopleCount++;
                parentToChangeId = this.id;

                rearrangeLevel(people, mainPane, level+1);
                drawLines(mainPane);
                Controller.toChange = lastFreeId;
                doChangeInfo = true;
            }
            else if (dropDownMenu.getValue().equals("Add spouse") && partnerId == -1) {
                people.put(lastFreeId, new TreeNode(lastFreeId, x + 250, y, mainPane, informationPane, level, this.id));
                Controller.peopleCount++;
                partnerToChangeId = this.id;

                rearrangeLevel(people, mainPane, level);
                drawLines(mainPane);
                Controller.toChange = lastFreeId;
                doChangeInfo = true;
            }
            else if (dropDownMenu.getValue().equals("Remove")) {
                removeNode(mainPane);
                rearrangeLevel(people, mainPane, level);
                drawLines(mainPane);
                Controller.peopleCount--;
                if (Controller.peopleCount == 0) {
                    Button addPersonBtn = (Button) Main.getStage().getScene().lookup("#addPersonBtn");
                    addPersonBtn.setVisible(true);
                    addPersonBtn.setDisable(false);
                }
            }
            else if (dropDownMenu.getValue().equals("Filter root")) {
                MakeRoot.filterNodes(id, mainPane);
            }
            else if (dropDownMenu.getValue().equals("Unfilter root")) {
                MakeRoot.unfilterNodes(mainPane);
            }
            if (doChangeInfo) {
                showInformationPane(informationPane);
            }
            Controller.printArray(Controller.levels);
            dropDownMenu.getSelectionModel().select("");
        });

        nodePane.getChildren().addAll(nodeCircle, dropDownMenu, textBox1, textBox2);

        mainPane.getChildren().addAll(nodePane);
    }

    public Text getFirstNameText(){
        return firstNameText;
    }
    public Text getLastNameText(){
        return lastNameText;
    }
    public int getId(){
        return id;
    }
    private static void clearInformationPane(Pane informationPane){
        ((TextField)informationPane.getChildren().get(6)).clear();
        ((TextField)informationPane.getChildren().get(7)).clear();
        ((TextField)informationPane.getChildren().get(8)).clear();
        ((DatePicker)informationPane.getChildren().get(9)).getEditor().clear();
    }

    public void showInformationPane(Pane informationPane){
        clearInformationPane(informationPane);
        informationPane.toFront();
        informationPane.setLayoutX(Controller.people.get(Controller.toChange).nodePane.getLayoutX()-40);
        informationPane.setLayoutY(Controller.people.get(Controller.toChange).nodePane.getLayoutY());
        informationPane.setVisible(true);
    }

    public void saveInformation(int id, String firstName, String lastName, LocalDate birthDate, String birthPlace) {
        this.firstNameText.setText(firstName);
        this.lastNameText.setText(lastName);
        this.info = null;
        this.info = new PersonInfo(firstName, lastName, id, birthDate, birthPlace);
        if (partnerToChangeId != -1) { // if is spouse of sb else
            TreeNode partner = Controller.people.get(partnerToChangeId);
            partner.partnerId = id;
            partner.info.addSpouse(id);
            partnerId = partnerToChangeId;
            info.addSpouse(partnerToChangeId);
            if (partner.childrenId.size() != 0) {
                info.addChildren(partner.childrenId);
                childrenId.addAll(partner.childrenId);
                for (int child : childrenId) {
                    Controller.people.get(child).info.addParent(this.id);
                }
            }
        }
        else if (parentToChangeId != -1) { // if is child of parent
            TreeNode parent = Controller.people.get(parentToChangeId);
            parent.info.addChildren(id);
            parent.childrenId.add(id);
            info.addParent(parentToChangeId);
            if (parent.partnerId != -1) { // if parent has partner
                Controller.people.get(parent.partnerId).childrenId.add(id);
                Controller.people.get(parent.partnerId).info.addChildren(id);
                info.addParent(parent.partnerId);
            }
        }

    }

    public static void rearrangeLevel(LinkedHashMap<Integer, TreeNode> people, AnchorPane mainPane, int level) {
        int levelElementCount = Controller.getArrayLength(Controller.levels.get(level));

        // Align the only element
        if (levelElementCount == 1) {
            for (int i = 0; i < Controller.levels.get(level).size(); i++) { // find the only element
                if (Controller.levels.get(level).get(i) != -1) people.get(Controller.levels.get(level).get(i)).nodePane.setLayoutX(mainPane.getWidth()/2-100);
            }
        }
        // Align all elements
        else {
            int count = 0;
            int space = (int) mainPane.getWidth() / (levelElementCount+1);
            for (int i = 0; i < Controller.levels.get(level).size(); i++) {
                if (Controller.levels.get(level).get(i) == -1) continue;
                TreeNode value = people.get(Controller.levels.get(level).get(i));

                value.nodePane.setLayoutX(space + (space*count) - 100);
                value.x = space + (space*count) - 100;
                value.nodePane.setLayoutY(100 + level * 250);
                value.y = 100 + level * 250;
                count++;
            }
        }
    }
    public static void drawLines(AnchorPane mainPane) {
        mainPane.getChildren().removeAll(Controller.lines);
        Controller.lines.clear();
        //Controller.printArray(Controller.levels);

        TreeNode node1;
        TreeNode node2;
        for (int i = 0; i < Controller.levels.size(); i++) {
            ArrayList<Integer> level = Controller.levels.get(i);
            int ignoreId = -1;
            for (int o = 0; o < level.size(); o++) {
                if (level.get(o) == -1) continue;
                node1 = Controller.people.get(level.get(o));
                // Draw spouse line
                if (node1.partnerId != -1 && node1.id != ignoreId) {
                    ignoreId = node1.partnerId;
                    node2 = Controller.people.get(node1.partnerId);
                    int startX = (int) node1.nodePane.getLayoutX();
                    int startY = (int) node1.nodePane.getLayoutY();
                    int endX = (int) node2.nodePane.getLayoutX();
                    int endY = (int) node2.nodePane.getLayoutY();
                    Controller.lines.add(new Line(startX + 200, startY + 100, endX, endY + 100));
                }
                // Draw children lines
                if (node1.childrenId.size() != 0) {
                    for (int child : node1.childrenId) {
                        node2 = Controller.people.get(child);
                        int startX = (int) node1.nodePane.getLayoutX();
                        int startY = (int) node1.nodePane.getLayoutY();
                        int endX = (int) node2.nodePane.getLayoutX();
                        int endY = (int) node2.nodePane.getLayoutY();
                        Controller.lines.add(new Line(startX + 100, startY + 200, endX+100, endY));
                    }
                }
            }
        }
        mainPane.getChildren().addAll(Controller.lines);
    }
    private void removeNode(AnchorPane mainPane) {
        // Remove node from parents
        for (int i = 0; i < info.getParents().size(); i++) {
            TreeNode parent = Controller.people.get(info.getParents().get(i));
            for (int o = 0; o < parent.childrenId.size(); o++) {
                if (parent.childrenId.get(o) == this.id) {
                    parent.childrenId.remove(o);
                    break;
                }
            }
            parent.info.removeChild(this.id);
        }
        // Remove node from children
        for (Integer integer : childrenId) {
            TreeNode child = Controller.people.get(integer);
            child.info.removeParent(this.id);
        }
        // Remove node from spouse
        if (this.partnerId != -1) {
            TreeNode spouse = Controller.people.get(this.partnerId);
            spouse.partnerId = -1;
            spouse.info.removeSpouse();
        }
        // Remove node from levels
        ArrayList<Integer> arr = Controller.levels.get(this.level);
        for (int i = 0; i < arr.size(); i++) if (arr.get(i) == this.id) {
            arr.remove(i);
            arr.add(-1);
        }
        // Delete node
        Controller.people.remove(this.id);
        mainPane.getChildren().remove(this.nodePane);
    }
    public void addChildrenSpouseParentsFromFile(ArrayList<Integer> childrenId, int spouseId, ArrayList<Integer> parentId) {
        this.childrenId = childrenId;
        this.partnerId = spouseId;
        this.info.addChildren(childrenId);
        this.info.addSpouse(spouseId);
        for (int parent : parentId) {
            this.info.addParent(parent);
        }
    }

    public void setVisibleNode(boolean value){
        nodePane.setVisible(value);
    }
}
