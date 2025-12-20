package asset;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import core.Farm;
import player.Player;
import controller.PlayerController;
import notification.NotificationManager;

public class GameAppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Farm farm = new Farm(5, 5);
            Player player = new Player();
            NotificationManager nm = new NotificationManager();
            PlayerController pc = new PlayerController(player, farm, null, null);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/asset/MainMenu.fxml"));
            Parent root = loader.load();

            FarmController controller = loader.getController();
            controller.initialize(player, farm, nm, pc);

            primaryStage.setTitle("Smart Farm - Isometric Test");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}