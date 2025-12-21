package controller.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.core.Farm;
import model.player.Player;
import model.notification.NotificationManager;
import model.resourceManagement.ResourceManager; // 1. Import thêm cái này
import service.eventSystem.RandomEventManager;
public class GameAppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Khởi tạo các thành phần cốt lõi
            Farm farm = new Farm(5, 5);
            Player player = new Player();
            NotificationManager nm = new NotificationManager();

            // 2. SỬA LỖI TẠI ĐÂY: Khởi tạo ResourceManager
            ResourceManager shop = new ResourceManager();
            RandomEventManager event= new RandomEventManager();

            // 3. Truyền 'shop' vào PlayerController thay vì để null
            PlayerController pc = new PlayerController(player, farm, nm, shop,event);

            // Load giao diện
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gameview/GameMenu.fxml"));
            Parent root = loader.load();

            // Cấu hình Controller
            FarmController controller = loader.getController();
            controller.initialize(player, farm, nm, pc);

            // Hiển thị cửa sổ
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