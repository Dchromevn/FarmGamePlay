package controller.game;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class ToolManager {
    public enum ToolMode { NONE, WATER, FERTILIZE }

    private ToolMode currentTool = ToolMode.NONE;
    private int toolAmount = 0;

    private AnchorPane rootPane;
    private Button waterBtn;
    private Button fertilizerBtn;
    private UIManager uiManager;

    public ToolManager(AnchorPane rootPane, Button wBtn, Button fBtn, UIManager ui) {
        this.rootPane = rootPane;
        this.waterBtn = wBtn;
        this.fertilizerBtn = fBtn;
        this.uiManager = ui;
    }

    // --- SỬA: Thêm tham số currentDay ---
    public void activateTool(ToolMode mode, int currentDay) {
        showCustomInputDialog(mode, currentDay);
    }

    // --- SỬA: Thêm tham số currentDay để truyền vào thông báo ---
    private void showCustomInputDialog(ToolMode mode, int currentDay) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        VBox dialogBox = new VBox(20);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setMaxSize(400, 300);
        dialogBox.setPadding(new Insets(30));
        dialogBox.setStyle("-fx-background-color: #FDF5E6; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 0); -fx-border-color: #8B4513; -fx-border-width: 3; -fx-border-radius: 20;");

        Label title = new Label(mode == ToolMode.WATER ? "WATERING" : "FERTILIZER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#8B4513"));

        Label message = new Label("Enter amount to use:");
        message.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        message.setTextFill(Color.BLACK);

        TextField inputField = new TextField();
        inputField.setText(mode == ToolMode.WATER ? "10" : "5");
        inputField.setMaxWidth(150); inputField.setAlignment(Pos.CENTER);
        inputField.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        inputField.setStyle("-fx-background-color: white; -fx-border-color: #8B4513; -fx-border-width: 2; -fx-text-fill: #8B4513;");

        HBox buttonBox = new HBox(20); buttonBox.setAlignment(Pos.CENTER);
        Button btnOk = createStyledButton("CONFIRM", "#4CAF50");
        Button btnCancel = createStyledButton("CANCEL", "#CD5C5C");

        btnOk.setOnAction(e -> {
            try {
                int amount = Integer.parseInt(inputField.getText());
                if (amount <= 0) throw new NumberFormatException();
                this.currentTool = mode;
                this.toolAmount = amount;
                rootPane.setCursor(Cursor.CROSSHAIR);



                updateToolVisuals();
                rootPane.getChildren().remove(overlay);
                rootPane.requestFocus();
            } catch (NumberFormatException ex) {
                uiManager.showAlert("Error", "Please enter a valid number!", Alert.AlertType.ERROR);
            }
        });

        btnCancel.setOnAction(e -> { rootPane.getChildren().remove(overlay); rootPane.requestFocus(); });
        buttonBox.getChildren().addAll(btnCancel, btnOk);
        dialogBox.getChildren().addAll(title, message, inputField, buttonBox);
        overlay.getChildren().add(dialogBox);
        rootPane.getChildren().add(overlay);
        ScaleTransition st = new ScaleTransition(Duration.millis(200), dialogBox); st.setFromX(0.8); st.setFromY(0.8); st.setToX(1.0); st.setToY(1.0); st.play();
        inputField.requestFocus();
    }

    private Button createStyledButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;");
        btn.setPrefWidth(100);
        return btn;
    }

    // --- SỬA: Thêm tham số currentDay ---
    public void resetTool(int currentDay) {
        this.currentTool = ToolMode.NONE;
        this.toolAmount = 0;
        rootPane.setCursor(Cursor.DEFAULT);




        updateToolVisuals();
    }

    private void updateToolVisuals() {
        waterBtn.setEffect(null); waterBtn.setScaleX(1.0); waterBtn.setScaleY(1.0);
        fertilizerBtn.setEffect(null); fertilizerBtn.setScaleX(1.0); fertilizerBtn.setScaleY(1.0);
        DropShadow glow = new DropShadow(); glow.setColor(Color.YELLOW); glow.setRadius(20); glow.setSpread(0.5);
        if (currentTool == ToolMode.WATER) { waterBtn.setEffect(glow); waterBtn.setScaleX(1.2); waterBtn.setScaleY(1.2); }
        else if (currentTool == ToolMode.FERTILIZE) { fertilizerBtn.setEffect(glow); fertilizerBtn.setScaleX(1.2); fertilizerBtn.setScaleY(1.2); }
    }

    public ToolMode getCurrentTool() { return currentTool; }
    public int getToolAmount() { return toolAmount; }
}