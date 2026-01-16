package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import controller.menu.SceneNavigator;
import javafx.application.Platform;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
    	try {
        SceneNavigator.setMainStage(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.MAIN_MENU));
        Parent root = loader.load();
        primaryStage.setTitle("Smart Farm");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    	} catch (IOException e) {
    		Alert alert = new Alert(Alert.AlertType.ERROR);
    		alert.setTitle("Startup Error");
    		alert.setHeaderText("Cannot start application");
    		alert.setContentText("Failed to load game. Please reinstall or contact support");
    		alert.showAndWait();
    		e.printStackTrace();
    		Platform.exit();
    	}
    }
    public static void main(String[] args) {
        launch(args);
    }
}