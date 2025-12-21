package controller.menu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;

import java.io.File;

public class MainMenuController {
	@FXML private Button btnStart;
    @FXML private Button btnContinue;
    @FXML private StackPane exitPane;
    @FXML private StackPane helpPane; // Khai báo thêm helpPane
    @FXML private ImageView background;

    @FXML
    public void initialize() {
        File saveFile = new File("sourcecode/view/menuview/saves/smartfarm_save.dat");
        if (btnContinue != null) {
            btnContinue.setDisable(!saveFile.exists());
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
        SceneNavigator.loadGameScene(false); 
    }

    // Thay đổi hàm này để hiện popup thay vì Alert
    @FXML
    public void handleHelp(ActionEvent event) {
        if (helpPane != null) {
            helpPane.setVisible(true);
        }
    }

    // Thêm hàm đóng popup Help
    @FXML
    public void closeHelp(ActionEvent event) {
        if (helpPane != null) {
            helpPane.setVisible(false);
        }
    }

    @FXML
    public void handleExitRequest(ActionEvent event) {
        if (exitPane != null) exitPane.setVisible(true);
    }

    @FXML
    public void confirmExit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void cancelExit(ActionEvent event) {
        if (exitPane != null) exitPane.setVisible(false);
    }
}