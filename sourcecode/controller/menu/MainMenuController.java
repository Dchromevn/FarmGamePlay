package controller.menu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import service.save.GameSaveManager;
public class MainMenuController {
	@FXML private Button btnStart;
    @FXML private Button btnContinue;
    @FXML private StackPane exitPane;
    @FXML private StackPane helpPane; 
    @FXML private ImageView background;
    private GameSaveManager saveManager;
    @FXML
    public void initialize() {
    	saveManager = new GameSaveManager();
        if (btnContinue != null) {
            btnContinue.setDisable(!saveManager.saveExists(GameSaveManager.DEFAULT_SAVE));
        }
        if (btnStart != null) {
        	btnStart.setDisable(false);
        }
        if (background != null) {
            background.setPreserveRatio(false);
        }
    }
    @FXML
    public void handleStartGame(ActionEvent event) {
        SceneNavigator.loadGameScene(true);
    }

    @FXML
    public void handleContinueGame(ActionEvent event) {
    	if(!saveManager.saveExists(GameSaveManager.DEFAULT_SAVE)) {
    		Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Continue Failed");
            alert.setContentText("Save file no longer exists!");
            alert.showAndWait();
            btnContinue.setDisable(true);
            return;
    	}
        SceneNavigator.loadGameScene(false); 
    }
    @FXML
    public void handleHelp(ActionEvent event) {
        if (helpPane != null) {
            helpPane.setVisible(true);
        }
    }
    @FXML
    public void closeHelp(ActionEvent event) {
        if (helpPane != null) {
            helpPane.setVisible(false);
        }
    }
    @FXML
    public void handleExitRequest(ActionEvent event) {
        if (exitPane != null) {
        	exitPane.setVisible(true);
        }
    }
    @FXML
    public void confirmExit(ActionEvent event) {
        Platform.exit();
    }
    @FXML
    public void cancelExit(ActionEvent event) {
        if (exitPane != null) {
        	exitPane.setVisible(false);
        }
    }
}