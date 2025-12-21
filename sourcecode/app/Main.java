package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.menu.SceneNavigator;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneNavigator.setMainStage(primaryStage);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.MAIN_MENU));
        Parent root = loader.load();
        primaryStage.setTitle("Smart Farm");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}