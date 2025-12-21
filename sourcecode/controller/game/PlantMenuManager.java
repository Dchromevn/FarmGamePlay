package controller.game;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import model.player.Player;
import utility.CropType;
import utility.Point;

public class PlantMenuManager {
    private AnchorPane rootPane;
    private PlayerController playerController;
    private Player player;
    private FarmRenderer renderer;
    private Runnable onPlantCallback;

    public PlantMenuManager(AnchorPane rootPane, PlayerController pc, Player p, FarmRenderer fr, Runnable onPlant) {
        this.rootPane = rootPane;
        this.playerController = pc;
        this.player = p;
        this.renderer = fr;
        this.onPlantCallback = onPlant;
    }

    public void showPlantMenu(int x, int y) {
        if (rootPane == null) return;

        // 1. LỚP NỀN TỐI (Overlay)
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        // 2. KHUNG CHỌN HẠT GIỐNG (Main Box)
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxSize(500, 450);
        container.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 0, 0); " +
                        "-fx-padding: 25;"
        );

        // Header
        Label title = new Label("SELECT SEED TO PLANT");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#8B4513"));

        // Grid hạt giống
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        int col = 0, row = 0;
        for (CropType type : CropType.values()) {
            VBox seedCard = createSeedCard(type, x, y, overlay);
            grid.add(seedCard, col, row);
            col++;
            if (col > 2) { col = 0; row++; }
        }

        // Nút Đóng
        Button closeBtn = new Button("Cancel");
        closeBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        closeBtn.setPrefWidth(120);
        closeBtn.setOnAction(e -> rootPane.getChildren().remove(overlay));

        container.getChildren().addAll(title, grid, closeBtn);
        overlay.getChildren().add(container);
        rootPane.getChildren().add(overlay);

        // Hiệu ứng Scale
        ScaleTransition st = new ScaleTransition(Duration.millis(200), container);
        st.setFromX(0.7); st.setFromY(0.7); st.setToX(1.0); st.setToY(1.0);
        st.play();
    }

    private VBox createSeedCard(CropType type, int x, int y, StackPane overlay) {
        int count = player.getInventory().getSeedCount(type);

        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(130, 160);
        card.setStyle("-fx-background-color: #F5DEB3; -fx-background-radius: 10; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 10;");

        ImageView img = new ImageView(renderer.loadImage(type.getCropName() + "_Bag"));
        img.setFitWidth(50); img.setFitHeight(50);

        Label nameLabel = new Label(type.getCropName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label countLabel = new Label("Left: " + count);
        countLabel.setTextFill(count > 0 ? Color.DARKGREEN : Color.RED);

        Button plantBtn = new Button("PLANT");
        plantBtn.setDisable(count <= 0);
        plantBtn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white; -fx-font-size: 11; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");

        plantBtn.setOnAction(e -> {
            boolean success = playerController.plantCrop(type, new Point(x, y));
            if (success) {
                rootPane.getChildren().remove(overlay);
                if (onPlantCallback != null) onPlantCallback.run();
            }
        });

        card.getChildren().addAll(img, nameLabel, countLabel, plantBtn);

        // Hiệu ứng hover
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-background-color: #FFE4B5;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle() + "-fx-background-color: #F5DEB3;"));

        return card;
    }
}