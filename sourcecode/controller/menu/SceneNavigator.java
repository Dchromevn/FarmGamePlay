package controller.menu;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import controller.game.FarmController;
import model.core.Farm;
import model.core.GameState;
import model.player.Player;
import controller.game.PlayerController;
import model.notification.NotificationManager;
import model.resourceManagement.Store;
import utility.NotificationType;
import service.eventSystem.RandomEventManager;
import java.io.IOException;
import service.save.GameSaveManager;
import controller.game.UIManager;
public class SceneNavigator {
    public static final String MAIN_MENU = "/view/menuview/MainMenu.fxml";
    public static final String GAME_VIEW = "/view/gameview/GameMenu.fxml";
    private static Stage mainStage; //MainMenu window
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
            stage.setResizable(false); 
            setMainStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadGameScene(boolean isNewGame) {
        try {
        	Farm farm;
        	Player player;
        	NotificationManager nm;
        	RandomEventManager randomEvent;
        	if (isNewGame) {
        		farm = new Farm(5,5);
        		player = new Player();
        		nm = new NotificationManager();
        		randomEvent = new RandomEventManager();
        	} else {
        		GameSaveManager saveManager = new GameSaveManager();
        		try {
        			GameState loadState = saveManager.loadGame(GameSaveManager.DEFAULT_SAVE);
        			farm = loadState.getFarm();
        			player = loadState.getPlayer();
        			nm = loadState.getNotificationManager();
        			randomEvent = loadState.getEventManager();
        			nm.addNotification("Welcome back!", NotificationType.SUCCESS, farm.getCurrentDay());
        		} catch(IOException | ClassNotFoundException e ){
        			Alert alert = new Alert(Alert.AlertType.ERROR);
        			alert.setTitle("Load Failed");
        			alert.setHeaderText("Cannot continue game");
                    alert.setContentText("Save file not found or corrupted.");
                    alert.showAndWait();
                    return ;
        		}
        	}
        	Store shop = new Store();
        	PlayerController pc = new PlayerController(player, farm, nm, shop, randomEvent,null);

            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(GAME_VIEW));
            Parent root = loader.load();

            FarmController controller = loader.getController();
            if (controller != null) {
                controller.initialize(player,farm,nm,pc,randomEvent);
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
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to load game screen.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}