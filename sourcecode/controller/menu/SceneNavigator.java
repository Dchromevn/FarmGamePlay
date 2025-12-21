package controller.menu;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.game.FarmController;
import model.core.Farm;
import model.player.Player;
import controller.game.PlayerController;
import model.notification.NotificationManager;
import model.resourceManagement.ResourceManager;
import utility.NotificationType;
import service.eventSystem.RandomEventManager;
import java.io.IOException;

public class SceneNavigator {
    public static final String MAIN_MENU = "/view/menuview/MainMenu.fxml";
    public static final String GAME_VIEW = "/view/gameview/GameMenu.fxml";

    private static Stage mainStage;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void loadMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(MAIN_MENU));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Smart Farm");
            stage.setScene(new Scene(root));
            
            // Cập nhật lại Stage chính của ứng dụng
            setMainStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGameScene(boolean isNewGame) {
        try {
            Farm farm = new Farm(5, 5);
            Player player = new Player();
            NotificationManager nm = new NotificationManager();
            ResourceManager shop = new ResourceManager();
            RandomEventManager randomEvent = new RandomEventManager(); // Đổi tên để tránh trùng lặp
            PlayerController pc = new PlayerController(player, farm, nm, shop, randomEvent);

            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(GAME_VIEW));
            Parent root = loader.load();

            FarmController controller = loader.getController();
            if (controller != null) {
                if (!isNewGame) {
                    pc.loadGameCommand("smartfarm_save");
                    farm = pc.getFarm();
                    player = pc.getPlayer();
                    nm = pc.getNotificationManager();

                    nm.addNotification("Welcome back!", NotificationType.SUCCESS, farm.getCurrentDay());
                
                }
                controller.initialize(player,farm,nm,pc);
                controller.updateGameUI(); 
            }

            Stage gameStage = new Stage();
            gameStage.setTitle("Smart Farm - Game Play");
            gameStage.setScene(new Scene(root));
            gameStage.setResizable(false);
            gameStage.setOnCloseRequest(windowEvent -> {
                if (controller != null) {
                    controller.confirmExitOnClose(windowEvent);
                }
            });

            gameStage.show();

            if (mainStage != null) {
                mainStage.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading Game Scene: " + e.getMessage());
        }
    }
}