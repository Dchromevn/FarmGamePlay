package controller.game;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class UIManager {
    private Label dayLabel, moneyLabel, waterLabel, fertilizerLabel, medicineLabel,boardLabel;
    private AnchorPane rootPane;
    private List<String> notificationHistory = new ArrayList<>();

    public UIManager(AnchorPane rootPane, Label day, Label money, Label water, Label fer,Label med, Label board) {
        this.rootPane = rootPane;
        this.dayLabel = day;
        this.moneyLabel = money;
        this.waterLabel = water;
        this.fertilizerLabel = fer;
        this.boardLabel = board;
        this.medicineLabel = med;
    }

    public void updateHUD(int day, double money, int water, int fertilizer, int medicine) {
        if (dayLabel != null) dayLabel.setText("Day " + day);
        if (moneyLabel != null) moneyLabel.setText("$" + (int)money);
        if (waterLabel != null) waterLabel.setText("Water: " + water);
        if (fertilizerLabel != null) fertilizerLabel.setText("Fer: " + fertilizer);
        if (medicineLabel != null) medicineLabel.setText("Med: " + fertilizer);

    }

    public void showNotification(String message, int currentDay) {
        if (boardLabel != null) {
            boardLabel.setText(message);
            FadeTransition fade = new FadeTransition(Duration.millis(200), boardLabel);
            fade.setFromValue(0.0); fade.setToValue(1.0); fade.play();
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> boardLabel.setText(""));
            pause.play();
        }
        String logEntry = "[Day " + currentDay + "] " + message;
        notificationHistory.add(0, logEntry);
    }

    public void showLogBoard() {
        if (rootPane == null) return;

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        VBox boardBox = new VBox(15);
        boardBox.setAlignment(Pos.TOP_CENTER);
        boardBox.setMaxSize(600, 700);
        boardBox.setPadding(new Insets(25));
        boardBox.setStyle("-fx-background-color: #FDF5E6; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 0); -fx-border-color: #8B4513; -fx-border-width: 3; -fx-border-radius: 15;");

        // HEADER: Font Arial
        Label title = new Label("NOTIFICATION");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#5D4037"));
        title.setUnderline(true);

        ListView<String> listView = new ListView<>();
        if (notificationHistory.isEmpty()) listView.getItems().add("No activities yet...");
        else listView.getItems().addAll(notificationHistory);

        listView.setPrefHeight(550);
        listView.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 10;");

        listView.setCellFactory(param -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null); setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setTextFill(Color.BLACK);
                    // ITEM: Font Arial
                    setFont(Font.font("Arial", FontWeight.BOLD, 18));
                    setStyle("-fx-background-color: transparent;");
                }
            }
        });

        Button closeBtn = new Button("Close");
        closeBtn.setPrefWidth(200);
        // BUTTON: Font Arial
        closeBtn.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 10; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> rootPane.getChildren().remove(overlay));

        boardBox.getChildren().addAll(title, listView, closeBtn);
        overlay.getChildren().add(boardBox);
        rootPane.getChildren().add(overlay);

        ScaleTransition st = new ScaleTransition(Duration.millis(200), boardBox);
        st.setFromX(0.8); st.setFromY(0.8); st.setToX(1.0); st.setToY(1.0); st.play();
    }

    public void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}