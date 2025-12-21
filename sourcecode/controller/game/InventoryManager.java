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

public class InventoryManager {
    private AnchorPane rootPane;
    private Player player;
    private FarmRenderer renderer;

    public InventoryManager(AnchorPane rootPane, Player player, FarmRenderer renderer) {
        this.rootPane = rootPane;
        this.player = player;
        this.renderer = renderer;
    }

    public void showStatus() {
        if (rootPane == null) return;

        // 1. Overlay background
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        // 2. Main Container
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxSize(550, 500);
        container.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 0, 0); " +
                        "-fx-padding: 25;"
        );

        // --- HEADER ---
        Label title = new Label("INVENTORY");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#8B4513"));

        // --- GRID OF ITEMS ---
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        int col = 0, row = 0;

        // 1. Medicine Card
        int medCount = player.getInventory().getMedicine();
        grid.add(createStatusCard("Medicine", medCount, "Medicine"), col, row);
        col++;

        // 2. Seed Cards
        for (CropType type : CropType.values()) {
            String typeName = type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1).toLowerCase();
            int count = player.getInventory().getSeedCount(type);

            // Note: Use typeName + "_Bag" to match your Store's image naming convention
            grid.add(createStatusCard(typeName + " Seeds", count, typeName + "_Bag"), col, row);

            col++;
            if (col > 2) { col = 0; row++; }
        }

        // --- CLOSE BUTTON ---
        Button closeBtn = new Button("Close");
        closeBtn.setPrefWidth(120);
        closeBtn.setStyle(
                "-fx-background-color: #d9534f; " + // Red color to differentiate from Store
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> rootPane.getChildren().remove(overlay));

        container.getChildren().addAll(title, grid, closeBtn);
        overlay.getChildren().add(container);

        // Transition effect
        ScaleTransition st = new ScaleTransition(Duration.millis(200), container);
        st.setFromX(0.5); st.setFromY(0.5); st.setToX(1.0); st.setToY(1.0); st.play();

        rootPane.getChildren().add(overlay);
    }

    private VBox createStatusCard(String name, int count, String imageName) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(140, 160);
        card.setStyle("-fx-background-color: #FDF5E6; -fx-background-radius: 12; -fx-border-color: #DEB887; -fx-border-width: 2;");

        ImageView img = new ImageView(renderer.loadImage(imageName));
        img.setFitWidth(60);
        img.setFitHeight(60);
        img.setPreserveRatio(true);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label countLabel = new Label("Amount: " + count);
        countLabel.setFont(Font.font("Arial", FontWeight.BLACK, 16));
        countLabel.setTextFill(count > 0 ? Color.DARKGREEN : Color.RED);

        card.getChildren().addAll(img, nameLabel, countLabel);
        return card;
    }
}