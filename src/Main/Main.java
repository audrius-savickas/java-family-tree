package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));
        primaryStage.setTitle("Family Tree");
        primaryStage.setScene(new Scene(root, 1500, 800));



        primaryStage.show();
        stage = primaryStage;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
