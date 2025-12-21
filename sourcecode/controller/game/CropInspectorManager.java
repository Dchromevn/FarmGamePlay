package controller.game;

import model.crops.Crop;
import model.core.FarmCell;
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
import utility.Point;

public class CropInspectorManager {
    private AnchorPane rootPane;
    private PlayerController playerController;
    private FarmRenderer renderer;
    private Runnable onActionCallback;

    public CropInspectorManager(AnchorPane rootPane, PlayerController pc, FarmRenderer fr, Runnable onAction) {
        this.rootPane = rootPane;
        this.playerController = pc;
        this.renderer = fr;
        this.onActionCallback = onAction;
    }

    public void showInspector(FarmCell cell, int x, int y) {
        if (cell.isEmpty() || rootPane == null) return;
        Crop crop = cell.getCrop();

        // 1. Lớp nền tối (Overlay)
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        // 2. Khung nội dung chính (Style giống StoreManager)
        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxSize(420, 500);
        container.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 0, 0); " +
                        "-fx-padding: 25;"
        );

        // --- XỬ LÝ HÌNH ẢNH THEO GIAI ĐOẠN ---
        String typeName = crop.getCropType().toString();
        typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1).toLowerCase();

        String suffix = "Mature";
        if (crop.isDead()) {
            suffix = "Dead";
        } else if (crop.isHarvestable()) {
            suffix = "Harvestable";
        } else {
            String stage = crop.getCurrentStage().toString();
            if (stage.equals("SEED")) suffix = "Seed";
            else if (stage.equals("SEEDLING")) suffix = "Seedling";
        }

        // Load ảnh dựa trên loại cây và giai đoạn thực tế
        ImageView cropIcon = new ImageView(renderer.loadImage(typeName + "_" + suffix));
        cropIcon.setFitWidth(100);
        cropIcon.setFitHeight(100);
        cropIcon.setPreserveRatio(true);

        Label titleLabel = new Label(typeName.toUpperCase());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#8B4513"));

        // --- BẢNG THÔNG SỐ (STATS) ---
        VBox statsBox = new VBox(8);
        statsBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 15; -fx-background-radius: 10;");

        statsBox.getChildren().addAll(
                createStatRow("Health:", crop.getHealth() + "%", Color.DARKRED),
                createStatRow("Water:", crop.getWaterLevel() + "/" + crop.getCropType().getMaxWaterLevel(), Color.BLUE),
                createStatRow("Fertilizer:", crop.getFertilizerLevel() + "/" + crop.getCropType().getMaxFertilizerLevel(), Color.BROWN),
                createStatRow("Days to Harvest:", crop.getDaysHarvest() + " days", Color.DARKGREEN)
        );

        // --- HÀNH ĐỘNG (ACTIONS) ---
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);

        if (!crop.isDead()) {
            // Cây còn sống: Hiện tưới nước và bón phân
            actionButtons.getChildren().add(createStyledButton("Water (-10)", "#5bc0de", () -> {
                playerController.waterCrop(new Point(x, y), 10);
                refresh(overlay, cell, x, y);
            }));

            actionButtons.getChildren().add(createStyledButton("Fertilize (-5)", "#f0ad4e", () -> {
                playerController.fertilizeCrop(new Point(x, y), 5);
                refresh(overlay, cell, x, y);
            }));
            actionButtons.getChildren().add(createStyledButton("Medicine","#9370DB",() ->{
            	playerController.cureCrop(new Point(x,y));
                refresh(overlay,cell,x,y);
            }));

            // Cây đã chín: Hiện thêm nút Harvest
            if (crop.isHarvestable()) {
                actionButtons.getChildren().add(createStyledButton("HARVEST", "#5cb85c", () -> {
                    playerController.harvestCrop(new Point(x, y));
                    close(overlay);
                }));
            }
        } else {
            // Cây đã chết: CHỈ hiện nút Recycle
            actionButtons.getChildren().add(createStyledButton("Recycle", "#d9534f", () -> {
                playerController.recycleCrop(new Point(x, y));
                close(overlay);
            }));
        }

        // Nút Close
        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #777; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        closeBtn.setPrefWidth(100);
        closeBtn.setOnAction(e -> close(overlay));

        container.getChildren().addAll(cropIcon, titleLabel, statsBox, actionButtons, closeBtn);
        overlay.getChildren().add(container);
        rootPane.getChildren().add(overlay);

        // Hiệu ứng hiện ra
        ScaleTransition st = new ScaleTransition(Duration.millis(200), container);
        st.setFromX(0.7); st.setFromY(0.7); st.setToX(1.0); st.setToY(1.0); st.play();
    }

    private HBox createStatRow(String label, String value, Color color) {
        HBox row = new HBox(10);
        Label lblLabel = new Label(label);
        lblLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label lblValue = new Label(value);
        lblValue.setTextFill(color);
        lblValue.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        row.getChildren().addAll(lblLabel, lblValue);
        return row;
    }

    private Button createStyledButton(String text, String bgColor, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        btn.setOnAction(e -> {
            action.run();
            if (onActionCallback != null) onActionCallback.run();
        });
        return btn;
    }

    private void refresh(StackPane overlay, FarmCell cell, int x, int y) {
        rootPane.getChildren().remove(overlay);
        showInspector(cell, x, y);
    }

    private void close(StackPane overlay) {
        rootPane.getChildren().remove(overlay);
        if (onActionCallback != null) onActionCallback.run();
    }
}