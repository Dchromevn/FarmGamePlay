package asset;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import controller.PlayerController;
import core.*;
import notification.NotificationManager;
import player.Player;
import utility.CropType;
import utility.Point;
import resourceManagement.Store;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller chính cho giao diện Farm với hiển thị Isometric 3D
 * Tự động load ảnh thông minh (hỗ trợ .png, .jpg, .jpeg)
 */
public class FarmController {

    @FXML private AnchorPane rootPane;
    @FXML private Pane farmPane;
    @FXML private Button advanceDayButton;

    // Right panel buttons
    @FXML private Button statusButton;
    @FXML private Button seedsButton;
    @FXML private Button waterButton;
    @FXML private Button equipmentButton;
    @FXML private Button storeButton;
    @FXML private Button infoButton;

    // Game components
    private Player player;
    private Farm farm;
    private NotificationManager notificationManager;
    private PlayerController playerController;
    private Store store;
    private Point selectedCell = null;

    // --- CẤU HÌNH GRID ---
    private static final double TILE_WIDTH = 66;
    private static final double TILE_HEIGHT = 33;
    private static final double IMAGE_SIZE = 180;
    private static final double BASE_OFFSET_X = 500;
    private static final double BASE_OFFSET_Y = 250;
    private static final int GRID_SIZE = 5;

    // Image cache để tối ưu performance
    private final Map<String, Image> imageCache = new HashMap<>();

    /**
     * Khởi tạo controller với dữ liệu game
     */
    public void initialize(Player player, Farm farm,
                           NotificationManager notificationManager,
                           PlayerController playerController) {
        this.player = player;
        this.farm = farm;
        this.notificationManager = notificationManager;
        this.playerController = playerController;
        this.store = new Store(playerController);

        // Load background
        loadBackground();

        // Tạo farm grid
        createIsometricGrid();

        // Setup animations
        setupButtonAnimations();
    }

    /**
     * Load background image cho farmPane
     */
    private void loadBackground() {
        try {
            Image bg = loadImage("Background");
            if (bg != null) {
                BackgroundImage bgi = new BackgroundImage(
                        bg,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(
                                BackgroundSize.AUTO,
                                BackgroundSize.AUTO,
                                false, false, true, true
                        )
                );
                rootPane.setBackground(new Background(bgi));
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi load background: " + e.getMessage());
        }
    }

    /**
     * Load ảnh thông minh - tự động tìm .png, .jpg, .jpeg
     * Có cache để tăng performance
     */
    private Image loadImage(String fileName) {
        // Xử lý tên file - bỏ đuôi nếu có
        String baseName = fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf("."))
                : fileName;

        // Check cache
        if (imageCache.containsKey(baseName)) {
            return imageCache.get(baseName);
        }

        // Thử các đuôi file
        String[] extensions = {".png", ".jpg", ".jpeg", ".PNG", ".JPG", ".JPEG"};
        Image img = null;

        for (String ext : extensions) {
            // Thử path trong asset/images/
            String path = "/asset/images/" + baseName + ext;
            try {
                java.io.InputStream stream = getClass().getResourceAsStream(path);

                // Nếu không có, thử images/ ở root
                if (stream == null) {
                    path = "/images/" + baseName + ext;
                    stream = getClass().getResourceAsStream(path);
                }

                if (stream != null) {
                    img = new Image(stream);
                    if (!img.isError()) {
                        break; // Tìm thấy ảnh hợp lệ
                    }
                }
            } catch (Exception e) {
                // Continue to next extension
            }
        }

        // Fallback nếu không tìm thấy
        if (img == null || img.isError()) {
            System.err.println("⚠️ Không tìm thấy: " + baseName);

            // Fallback sang Land nếu không phải đang load Land
            if (!baseName.equals("Land")) {
                return loadImage("Land");
            }

            // Tạo ảnh placeholder
            return createPlaceholderImage();
        }

        imageCache.put(baseName, img);
        return img;
    }

    /**
     * Tạo placeholder image nếu không load được ảnh gì
     */
    private Image createPlaceholderImage() {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(100, 100);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, 100, 100);
        gc.setFill(Color.WHITE);
        gc.fillText("?", 45, 55);

        javafx.scene.image.WritableImage img = new javafx.scene.image.WritableImage(100, 100);
        canvas.snapshot(null, img);
        return img;
    }

    /**
     * Tạo farm grid isometric
     */
    private void createIsometricGrid() {
        farmPane.getChildren().clear();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                FarmCell cell = farm.getCell(col, row);
                StackPane tile = createIsometricTile(row, col, cell);

                // Calculate isometric position
                double x = (col - row) * (TILE_WIDTH / 2) + BASE_OFFSET_X;
                double y = (col + row) * (TILE_HEIGHT / 2) + BASE_OFFSET_Y;

                tile.setLayoutX(x);
                tile.setLayoutY(y);

                farmPane.getChildren().add(tile);
            }
        }
    }

    /**
     * Tạo một tile isometric
     */
    private StackPane createIsometricTile(int row, int col, FarmCell cell) {
        StackPane tile = new StackPane();
        tile.setPrefSize(TILE_WIDTH, TILE_HEIGHT * 2);
        tile.setPickOnBounds(false);

        // Hitbox (hình thoi) để detect click
        Polygon hitbox = new Polygon(
                TILE_WIDTH / 2.0, 0.0,
                TILE_WIDTH, TILE_HEIGHT / 2.0,
                TILE_WIDTH / 2.0, TILE_HEIGHT,
                0.0, TILE_HEIGHT / 2.0
        );
        hitbox.setFill(Color.TRANSPARENT);
        hitbox.setStroke(Color.TRANSPARENT);

        // Visual image
        ImageView visual = new ImageView();
        visual.setFitWidth(IMAGE_SIZE);
        visual.setFitHeight(IMAGE_SIZE);
        visual.setPreserveRatio(true);
        visual.setMouseTransparent(true);
        visual.setTranslateY(-75);

        // Load ảnh phù hợp
        if (cell.isEmpty()) {
            visual.setImage(loadImage("Land"));
        } else {
            String cropImageName = getCropImageName(cell.getCrop());
            Image cropImg = loadImage(cropImageName);
            visual.setImage(cropImg);
        }

        tile.getChildren().addAll(hitbox, visual);

        // Thêm overlay (health bar, water warning)
        if (!cell.isEmpty()) {
            addCropOverlays(tile, cell.getCrop());
        }

        // Event handlers
        final int finalRow = row;
        final int finalCol = col;

        ColorAdjust brighten = new ColorAdjust();
        brighten.setBrightness(0.3);

        hitbox.setOnMouseEntered(e -> visual.setEffect(brighten));
        hitbox.setOnMouseExited(e -> visual.setEffect(null));
        hitbox.setOnMouseClicked(e -> {
            selectedCell = new Point(finalCol, finalRow);
            highlightSelectedTile();
            showCellMenu(finalCol, finalRow);
        });

        tile.setUserData(new Point(col, row));
        return tile;
    }

    /**
     * Thêm overlay cho crop (health bar, warnings)
     */
    private void addCropOverlays(StackPane tile, Crop crop) {
        if (crop.isDead()) return;

        VBox overlay = new VBox(2);
        overlay.setAlignment(Pos.CENTER);
        overlay.setMouseTransparent(true);
        overlay.setTranslateY(-110);

        // Water warning icon
        if (crop.getWaterLevel() < 30) {
            ImageView waterIcon = new ImageView(loadImage("Water_Pod"));
            waterIcon.setFitWidth(25);
            waterIcon.setFitHeight(25);

            // Pulsing animation
            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), waterIcon);
            fade.setFromValue(1.0);
            fade.setToValue(0.2);
            fade.setCycleCount(Animation.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();

            overlay.getChildren().add(waterIcon);
        }

        // Health bar
        HBox healthBox = new HBox(2);
        healthBox.setAlignment(Pos.CENTER);
        healthBox.setStyle(
                "-fx-background-color: rgba(0,0,0,0.5); " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 2;"
        );

        ImageView healthIcon = new ImageView(loadImage("Health"));
        healthIcon.setFitWidth(10);
        healthIcon.setFitHeight(10);

        ProgressBar healthBar = new ProgressBar(crop.getHealth() / 100.0);
        healthBar.setPrefWidth(35);
        healthBar.setPrefHeight(6);

        String barColor = crop.isHarvestable() ? "#FFD700" :
                crop.getHealth() < 30 ? "#FF0000" :
                        crop.getHealth() < 60 ? "#FFA500" : "#32CD32";

        healthBar.setStyle(
                "-fx-accent: " + barColor + "; " +
                        "-fx-control-inner-background: #444; " +
                        "-fx-text-box-border: transparent;"
        );

        healthBox.getChildren().addAll(healthIcon, healthBar);
        overlay.getChildren().add(healthBox);

        tile.getChildren().add(overlay);
    }

    /**
     * Lấy tên file ảnh cho crop (không có đuôi file)
     */
    private String getCropImageName(Crop crop) {
        String typeStr = crop.getCropType().toString();
        String typeName = typeStr.substring(0, 1).toUpperCase() +
                typeStr.substring(1).toLowerCase();

        String suffix = "Mature";

        if (crop.isDead()) {
            suffix = "Dead";
        } else if (crop.isHarvestable()) {
            // Special case cho Pumpkin
            if (typeName.equals("Pumpkin")) {
                return "Pumpkin-Harvestable";
            }
            suffix = "Harvestable";
        } else {
            String stage = crop.getCurrentStage().toString();
            if (stage.equals("SEED")) {
                suffix = "Seed";
            } else if (stage.equals("SEEDLING")) {
                suffix = "Seedling";
            }
        }

        return typeName + "_" + suffix;
    }

    // ==================== BUTTON HANDLERS ====================

    /**
     * Advance Day button
     */
    @FXML
    private void handleAdvanceDay() {
        // Button animation
        ScaleTransition st = new ScaleTransition(Duration.millis(100), advanceDayButton);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();

        try {
            eventSystem.RandomEventManager eventManager = new eventSystem.RandomEventManager();
            String eventResult = farm.advanceDay(eventManager);

            if (eventResult != null && !eventResult.isEmpty()) {
                showAlert("🌅 Sự kiện", eventResult, Alert.AlertType.INFORMATION);
            }

            refreshFarm();

            // Check dead crops
            long deadCount = farm.getAllCrops().stream()
                    .filter(Crop::isDead)
                    .count();

            if (deadCount > 0) {
                showAlert("⚠️ Cảnh báo",
                        "Có " + deadCount + " cây chết!\nHãy xới bỏ để lấy phân bón.",
                        Alert.AlertType.WARNING);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Lỗi", "Có lỗi khi chuyển ngày: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Store button - Mở cửa hàng
     */
    @FXML
    private void handleStoreButton() {
        System.out.println("🏪 Store button clicked!"); // DEBUG

        if (rootPane == null) {
            System.err.println("❌ rootPane is null!");
            return;
        }

        // Create overlay
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        // Shop container
        VBox shop = new VBox(15);
        shop.setAlignment(Pos.TOP_CENTER);
        shop.setStyle(
                "-fx-background-color: #8B4513; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #D2691E; " +
                        "-fx-border-width: 4; " +
                        "-fx-padding: 20;"
        );
        shop.setMaxSize(550, 480);

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER);

        ImageView storeIcon = new ImageView(loadImage("Store"));
        storeIcon.setFitWidth(50);
        storeIcon.setFitHeight(50);

        Label title = new Label("🏪 CỬA HÀNG");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        // Money display
        HBox moneyBox = new HBox(5);
        moneyBox.setAlignment(Pos.CENTER);
        moneyBox.setStyle(
                "-fx-background-color: rgba(0,0,0,0.3); " +
                        "-fx-padding: 5; " +
                        "-fx-background-radius: 10;"
        );

        ImageView moneyIcon = new ImageView(loadImage("Money"));
        moneyIcon.setFitWidth(20);
        moneyIcon.setFitHeight(20);

        Label moneyLabel = new Label("$" + player.getInventory().getMoney());
        moneyLabel.setTextFill(Color.GOLD);
        moneyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        moneyBox.getChildren().addAll(moneyIcon, moneyLabel);
        header.getChildren().addAll(storeIcon, title, moneyBox);

        // Items grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        int col = 0, row = 0;

        // Seeds
        for (CropType type : CropType.values()) {
            String typeName = type.toString().substring(0, 1).toUpperCase() +
                    type.toString().substring(1).toLowerCase();

            VBox item = createShopItem(
                    typeName,
                    type.getSeedPrice(),
                    typeName + "_Seed",
                    () -> {
                        playerController.buySeed(type, 1);
                        moneyLabel.setText("$" + player.getInventory().getMoney());
                    }
            );

            grid.add(item, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        // Fertilizer
        grid.add(createShopItem(
                "Phân bón",
                3,
                "Fertilizer bag",
                () -> {
                    playerController.buyFertilizer(1);
                    moneyLabel.setText("$" + player.getInventory().getMoney());
                }
        ), col, row);

        col++;

        // Water
        grid.add(createShopItem(
                "Nước",
                2,
                "Water_Pod",
                () -> {
                    playerController.buyWater(1);
                    moneyLabel.setText("$" + player.getInventory().getMoney());
                }
        ), col, row);

        // Close button
        Button closeBtn = new Button("❌ Đóng");
        closeBtn.setStyle(
                "-fx-background-color: #CD5C5C; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> rootPane.getChildren().remove(overlay));

        shop.getChildren().addAll(header, grid, closeBtn);
        overlay.getChildren().add(shop);
        rootPane.getChildren().add(overlay);
    }

    /**
     * Tạo shop item card
     */
    private VBox createShopItem(String name, int price, String imageName, Runnable onBuy) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(110, 140);
        card.setStyle(
                "-fx-background-color: #F5DEB3; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        );

        ImageView img = new ImageView(loadImage(imageName));
        img.setFitWidth(50);
        img.setFitHeight(50);
        img.setPreserveRatio(true);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label priceLabel = new Label("$" + price);
        priceLabel.setTextFill(Color.DARKRED);

        // Buy button
        StackPane buyBtn = new StackPane();
        ImageView btnBg = new ImageView(loadImage("Button"));
        btnBg.setFitWidth(70);
        btnBg.setFitHeight(25);

        Label btnText = new Label("MUA");
        btnText.setTextFill(Color.WHITE);
        btnText.setFont(Font.font("System", FontWeight.BOLD, 11));

        buyBtn.getChildren().addAll(btnBg, btnText);
        buyBtn.setStyle("-fx-cursor: hand;");

        buyBtn.setOnMouseClicked(e -> {
            onBuy.run();

            // Button animation
            ScaleTransition st = new ScaleTransition(Duration.millis(100), buyBtn);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(0.9);
            st.setToY(0.9);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        });

        card.getChildren().addAll(img, nameLabel, priceLabel, buyBtn);
        return card;
    }

    /**
     * Status button
     */
    @FXML
    private void handleStatusButton() {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 TRẠNG THÁI FARM\n");
        sb.append("═══════════════════\n\n");
        sb.append("📅 Ngày: ").append(farm.getCurrentDay()).append("\n");
        sb.append("🌱 Cây trồng: ").append(farm.getCropCount()).append("/25\n");
        sb.append("💔 Cây bị hư: ").append(farm.getDamagedCrops().size()).append("\n");
        sb.append("📦 Ô trống: ").append(farm.getEmptyCellCount()).append("\n\n");
        sb.append("💰 Tiền: $").append(player.getInventory().getMoney()).append("\n");
        sb.append("💧 Nước: ").append(player.getInventory().getWater()).append("\n");
        sb.append("🌿 Phân: ").append(player.getInventory().getFertilizer()).append("\n");
        sb.append("💊 Thuốc: ").append(player.getInventory().getMedicine());

        showAlert("Trạng thái", sb.toString(), Alert.AlertType.INFORMATION);
    }

    @FXML private void handleSeedsButton() {
        StringBuilder sb = new StringBuilder();
        sb.append("🌾 KHO HẠT GIỐNG\n═══════════════════\n\n");

        boolean hasSeeds = false;
        for (CropType type : CropType.values()) {
            int count = player.getInventory().getSeedCount(type);
            if (count > 0) {
                sb.append("• ").append(type.getCropName())
                        .append(": ").append(count).append("\n");
                hasSeeds = true;
            }
        }

        if (!hasSeeds) {
            sb.append("❌ Chưa có hạt giống!");
        }

        showAlert("Hạt giống", sb.toString(), Alert.AlertType.INFORMATION);
    }

    @FXML private void handleWaterButton() {
        showAlert("💧 Tưới nước",
                "Click vào cây để tưới!\n\nNước hiện có: " +
                        player.getInventory().getWater(),
                Alert.AlertType.INFORMATION);
    }

    @FXML private void handleEquipmentButton() {
        StringBuilder sb = new StringBuilder();
        sb.append("🎒 TRANG BỊ\n═══════════════════\n\n");
        sb.append("💧 Nước: ").append(player.getInventory().getWater()).append("\n");
        sb.append("🌿 Phân: ").append(player.getInventory().getFertilizer()).append("\n");
        sb.append("💊 Thuốc: ").append(player.getInventory().getMedicine()).append("\n");
        sb.append("💰 Tiền: $").append(player.getInventory().getMoney());

        showAlert("Trang bị", sb.toString(), Alert.AlertType.INFORMATION);
    }

    @FXML private void handleInfoButton() {
        String help = """
                🎮 HƯỚNG DẪN
                ═══════════════════
                
                🖱️ Click vào ô đất để:
                • Trồng cây (ô trống)
                • Tưới, bón phân (đang trồng)
                • Thu hoạch (chín)
                
                🌱 Các giai đoạn:
                Hạt → Mầm → Trưởng thành → Thu hoạch
                
                💡 Mẹo:
                • Giữ sức khỏe cây > 60%
                • Tưới đủ nước thường xuyên
                • Thu hoạch đúng lúc để có tiền
                
                Chúc may mắn! 🚜
                """;

        showAlert("Hướng dẫn", help, Alert.AlertType.INFORMATION);
    }

    // ==================== HELPERS ====================

    /**
     * Highlight tile được chọn
     */
    private void highlightSelectedTile() {
        createIsometricGrid();

        if (selectedCell != null) {
            farmPane.getChildren().stream()
                    .filter(node -> node instanceof StackPane)
                    .filter(node -> {
                        Point p = (Point) node.getUserData();
                        return p != null && p.equals(selectedCell);
                    })
                    .findFirst()
                    .ifPresent(node -> {
                        StackPane tile = (StackPane) node;
                        if (tile.getChildren().size() > 1) {
                            tile.getChildren().get(1).setEffect(
                                    new DropShadow(20, Color.YELLOW)
                            );
                        }
                    });
        }
    }

    /**
     * Hiển thị context menu cho cell
     */
    private void showCellMenu(int x, int y) {
        FarmCell cell = farm.getCell(x, y);
        ContextMenu menu = new ContextMenu();

        if (cell.isEmpty()) {
            // Menu trồng cây
            Menu plantMenu = new Menu("🌱 Trồng cây");
            for (CropType type : CropType.values()) {
                MenuItem item = new MenuItem(type.getCropName() +
                        " ($" + type.getSeedPrice() + ")");
                item.setOnAction(e -> {
                    if (playerController.plantCrop(type, new Point(x, y))) {
                        refreshFarm();
                    }
                });
                plantMenu.getItems().add(item);
            }
            menu.getItems().add(plantMenu);
        } else {
            Crop crop = cell.getCrop();

            // Info
            MenuItem info = new MenuItem(
                    "📊 " + crop.getCropType().getCropName() +
                            " - HP: " + crop.getHealth() + "%"
            );
            info.setDisable(true);
            menu.getItems().add(info);
            menu.getItems().add(new SeparatorMenuItem());

            // Actions
            if (!crop.isDead()) {
                MenuItem water = new MenuItem("💧 Tưới (10 đơn vị)");
                water.setOnAction(e -> {
                    if (playerController.waterCrop(new Point(x, y), 10)) {
                        refreshFarm();
                    }
                });
                menu.getItems().add(water);

                MenuItem fertilize = new MenuItem("🌿 Bón phân (5 đơn vị)");
                fertilize.setOnAction(e -> {
                    if (playerController.fertilizeCrop(new Point(x, y), 5)) {
                        refreshFarm();
                    }
                });
                menu.getItems().add(fertilize);
            }

            if (crop.isHarvestable()) {
                MenuItem harvest = new MenuItem("🌾 Thu hoạch");
                harvest.setOnAction(e -> {
                    if (playerController.harvestCrop(new Point(x, y))) {
                        refreshFarm();
                    }
                });
                menu.getItems().add(harvest);
            }

            MenuItem recycle = new MenuItem("⛏️ Xới bỏ");
            recycle.setOnAction(e -> {
                playerController.recycleCrop(new Point(x, y));
                refreshFarm();
            });
            menu.getItems().add(recycle);
        }

        menu.show(farmPane.getScene().getWindow());
    }

    private void refreshFarm() {
        createIsometricGrid();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Setup hover animations cho buttons
     */
    private void setupButtonAnimations() {
        Button[] buttons = {
                statusButton, seedsButton, waterButton,
                equipmentButton, storeButton, infoButton,
                advanceDayButton
        };

        for (Button btn : buttons) {
            if (btn != null) {
                btn.setOnMouseEntered(e -> {
                    btn.setScaleX(1.1);
                    btn.setScaleY(1.1);
                });

                btn.setOnMouseExited(e -> {
                    btn.setScaleX(1.0);
                    btn.setScaleY(1.0);
                });
            }
        }
    }
}