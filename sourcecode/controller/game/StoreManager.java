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
import model.resourceManagement.*;
public class StoreManager {
    private AnchorPane rootPane;
    private PlayerController playerController;
    private Player player;
    private FarmRenderer renderer;
    private Runnable onPurchaseCallback;

    public StoreManager(AnchorPane rootPane, PlayerController pc, Player p, FarmRenderer fr, Runnable onPurchase) {
        this.rootPane = rootPane;
        this.playerController = pc;
        this.player = p;
        this.renderer = fr;
        this.onPurchaseCallback = onPurchase;
    }

    public void showStore() {
        if (rootPane == null) return;

        // 1. LỚP NỀN TỐI (Overlay) - Theo mẫu: rgba 0.7
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        // 2. KHUNG STORE (Main Box) - Theo mẫu: Trắng, Bo tròn 20, Đổ bóng
        VBox shop = new VBox(15);
        shop.setAlignment(Pos.TOP_CENTER);
        shop.setMaxSize(550, 500);
        shop.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 0, 0); " +
                        "-fx-padding: 20;"
        );

        // --- HEADER ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER);

        // Icon Store
        ImageView storeIcon = new ImageView(renderer.loadImage("Store"));
        storeIcon.setFitWidth(50); storeIcon.setFitHeight(50);

        // Tiêu đề: Đổi màu chữ sang Nâu (#8B4513) cho nổi trên nền trắng
        Label title = new Label(" STORE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#8B4513"));

        // Hộp hiển thị tiền
        HBox moneyBox = new HBox(5);
        moneyBox.setAlignment(Pos.CENTER);
        moneyBox.setStyle("-fx-background-color: #eee; -fx-padding: 5 10; -fx-background-radius: 10;"); // Nền xám nhạt cho tiền

        ImageView moneyIcon = new ImageView(renderer.loadImage("Money"));
        moneyIcon.setFitWidth(20); moneyIcon.setFitHeight(20);

        Label storeMoneyLabel = new Label("$" + player.getInventory().getMoney());
        storeMoneyLabel.setTextFill(Color.DARKGREEN); // Tiền màu xanh lá đậm
        storeMoneyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        moneyBox.getChildren().addAll(moneyIcon, storeMoneyLabel);
        header.getChildren().addAll(storeIcon, title, moneyBox);

        // --- GRID SẢN PHẨM ---
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        int col = 0, row = 0;

        // 1. Seeds
        for (utility.CropType type : utility.CropType.values()) {
            String typeName = type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1).toLowerCase();
            VBox item = createShopItem(typeName, type.getSeedPrice(), typeName + "_Bag", () -> {
                playerController.buyItem(new SeedItem(type), 1);
                storeMoneyLabel.setText("$" + player.getInventory().getMoney());
                if(onPurchaseCallback != null) onPurchaseCallback.run();
            });
            grid.add(item, col, row); col++; if (col > 2) { col = 0; row++; }
        }

        // 2. Other Items (Phân bón, Nước, Thuốc)
        grid.add(createShopItem("Fertilizer(25 units)", 3, "Fertilizer_Bag_Sell", () -> {
            playerController.buyItem(new FertilizerItem(),1); storeMoneyLabel.setText("$" + player.getInventory().getMoney()); if(onPurchaseCallback != null) onPurchaseCallback.run();
        }), col, row); col++; if (col > 2) { col = 0; row++; }

        grid.add(createShopItem("Water(25 units)", 2, "Water", () -> {
            playerController.buyItem(new WaterItem(),1); storeMoneyLabel.setText("$" + player.getInventory().getMoney()); if(onPurchaseCallback != null) onPurchaseCallback.run();
        }), col, row); col++; if (col > 2) { col = 0; row++; }

        grid.add(createShopItem("Medicine", 50, "Medicine", () -> {
            playerController.buyItem(new MedicineItem(),1); storeMoneyLabel.setText("$" + player.getInventory().getMoney()); if(onPurchaseCallback != null) onPurchaseCallback.run();
        }), col, row);

        // --- NÚT CLOSE ---
        // Style theo mẫu: Xanh dương, chữ trắng, bo tròn 10
        Button closeBtn = new Button("Close");
        closeBtn.setPrefWidth(120);
        closeBtn.setStyle(
                "-fx-background-color: #d9534f; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> rootPane.getChildren().remove(overlay));

        shop.getChildren().addAll(header, grid, closeBtn);
        overlay.getChildren().add(shop);

        // Hiệu ứng hiện ra (Scale)
        ScaleTransition st = new ScaleTransition(Duration.millis(200), shop);
        st.setFromX(0.5); st.setFromY(0.5); st.setToX(1.0); st.setToY(1.0); st.play();

        rootPane.getChildren().add(overlay);
    }

    private VBox createShopItem(String name, int price, String imageName, Runnable onBuy) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(130, 180); // Tăng kích thước thẻ lên xíu cho vừa nút to
        card.setStyle("-fx-background-color: #F5DEB3; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        // 1. Ảnh và Tên
        ImageView img = new ImageView(renderer.loadImage(imageName));
        img.setFitWidth(50);
        img.setFitHeight(50);
        img.setPreserveRatio(true);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label priceLabel = new Label("$" + price);
        priceLabel.setTextFill(Color.DARKRED);

        // 2. TẠO NÚT MUA VỚI STYLE MỚI CỦA BẠN
        Button buyBtn = new Button("BUY");

        // Set kích thước cố định cho nhỏ gọn (Rộng 70, Cao 25)
        buyBtn.setPrefSize(70, 25);

        buyBtn.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.5); " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 1.5px; " +  // Giảm viền xuống 1.5px cho đỡ thô
                        "-fx-background-radius: 30; " +
                        "-fx-border-radius: 30; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 11px; " +      // Giảm chữ xuống 11px
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"
        );
        // Xử lý sự kiện click
        buyBtn.setOnAction(e -> {
            onBuy.run();
            // Hiệu ứng nhún nút khi bấm
            ScaleTransition st = new ScaleTransition(Duration.millis(100), buyBtn);
            st.setFromX(1.0); st.setFromY(1.0);
            st.setToX(0.9); st.setToY(0.9);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        });

        card.getChildren().addAll(img, nameLabel, priceLabel, buyBtn);
        return card;
    }
}