package model.resourceManagement;

import controller.game.PlayerController;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;

public class Store {
    private PlayerController controller;

    public Store(PlayerController controller) {
        this.controller = controller;
    }
    private HBox createItemRow(String name, String price, Runnable buyAction) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 5; -fx-background-color: #f4f4f4; -fx-background-radius: 5;");

        Label nameLabel = new Label(name);
        nameLabel.setPrefWidth(150);

        Label priceLabel = new Label(price);
        priceLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        priceLabel.setPrefWidth(80);

        Button buyBtn = new Button("Buy");
        buyBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: black; -fx-cursor: hand;");

        buyBtn.setOnAction(e -> {
            buyAction.run();
            buyBtn.setText("OK!");
            buyBtn.setDisable(true);
            new javafx.animation.PauseTransition(javafx.util.Duration.millis(500)).setOnFinished(ev -> {
                buyBtn.setText("Buy");
                buyBtn.setDisable(false);
            });
        });

        row.getChildren().addAll(nameLabel, priceLabel, buyBtn);
        return row;
    }
}