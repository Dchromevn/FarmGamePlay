package resourceManagement;

import controller.PlayerController;
import player.Player;
import utility.CropType;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class Store {
    private PlayerController controller;

    public Store(PlayerController controller) {
        this.controller = controller;
    }

    /**
     * Hàm hiển thị cửa sổ cửa hàng (GUI)
     * Thay thế cho hàm openStore dùng Scanner cũ
     */
    public void showStoreDialog() {
        // 1. Tạo hộp thoại (Dialog)
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("🏪 Farm Shop");
        dialog.setHeaderText("Welcome to the Store!");

        // Lấy thông tin tiền từ controller (giả định controller có hàm getPlayer)
        // Nếu controller không có hàm getPlayer(), bạn cần truyền Player vào hàm này
        // Ở đây ta tạm thời lấy tiền thông qua việc mua thử hoặc hiển thị tĩnh
        // Để đơn giản, ta sẽ cập nhật tiền sau mỗi lần mua

        // 2. Tạo nội dung cửa sổ
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        // --- MỤC: SEEDS (HẠT GIỐNG) ---
        Label lblSeeds = new Label("🌱 SEEDS");
        lblSeeds.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(lblSeeds);

        for (CropType type : CropType.values()) {
            HBox row = createItemRow(
                    type.toString() + " Seed",
                    "$" + type.getSeedPrice(),
                    () -> controller.buySeed(type, 1)
            );
            content.getChildren().add(row);
        }

        content.getChildren().add(new Separator());

        // --- MỤC: RESOURCES (VẬT TƯ) ---
        Label lblRes = new Label("💧 SUPPLIES");
        lblRes.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(lblRes);

        // Nước
        content.getChildren().add(createItemRow(
                "Water (25 units)",
                "$2",
                () -> controller.buyWater(1) // Mua 1 gói (25 đơn vị)
        ));

        // Phân bón
        content.getChildren().add(createItemRow(
                "Fertilizer (25 units)",
                "$3",
                () -> controller.buyFertilizer(1)
        ));

        // Thuốc
        content.getChildren().add(createItemRow(
                "Medicine",
                "$50",
                () -> controller.buyMedicine(1)
        ));

        // 3. Đưa nội dung vào Dialog
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // 4. Hiển thị
        dialog.showAndWait();
    }

    // Hàm hỗ trợ tạo 1 dòng sản phẩm: [Tên ...... Giá [Button Buy]]
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
        buyBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");

        buyBtn.setOnAction(e -> {
            buyAction.run();
            // Hiệu ứng nháy nút để biết đã mua
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