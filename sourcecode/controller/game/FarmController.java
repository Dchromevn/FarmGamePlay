package controller.game;

import controller.menu.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.core.*;
import model.crops.Crop;
import model.notification.NotificationManager;
import model.player.Player;
import service.eventSystem.RandomEventManager;
import utility.Point;

public class FarmController {

    @FXML private AnchorPane rootPane;
    @FXML private StackPane customExitPane;
    @FXML private Pane farmPane;
    @FXML private Button advanceDayButton;
    @FXML private Button saveExitButton;

    // Các nút chức năng trên HUD
    @FXML private Button statusButton, seedsButton, waterButton, fertilizerButton, equipmentButton, storeButton, infoButton;

    // Các thành phần UI hiển thị thông số
    @FXML private Label dayLabel, moneyLabel, waterLabel, fertilizerLabel, medicineLabel,boardLabel;
    @FXML private Button boardButton;

    // Đối tượng lõi của trò chơi
    private Player player;
    private Farm farm;
    private NotificationManager notificationManager;
    private PlayerController playerController;
    private Point selectedCell = null;

    // --- CÁC LỚP QUẢN LÝ PHÂN HỆ (MANAGERS) ---
    private UIManager uiManager;
    private FarmRenderer farmRenderer;
    private ToolManager toolManager;
    private StoreManager storeManager;
    private InventoryManager inventoryManager;
    private CropInspectorManager cropInspectorManager; // Quản lý xem chi tiết cây
    private PlantMenuManager plantMenuManager;       // Quản lý menu trồng cây mới

    public void initialize(Player player, Farm farm, NotificationManager notificationManager, PlayerController playerController) {
        this.player = player;
        this.farm = farm;
        this.notificationManager = notificationManager;
        this.playerController = playerController;


        this.uiManager = new UIManager(rootPane, dayLabel, moneyLabel, waterLabel, fertilizerLabel, medicineLabel,boardLabel);
        this.farmRenderer = new FarmRenderer(farmPane);
        this.toolManager = new ToolManager(rootPane, waterButton, fertilizerButton, uiManager);
        this.storeManager = new StoreManager(rootPane, playerController, player, farmRenderer, this::updateGameUI);
        this.inventoryManager = new InventoryManager(rootPane, player, farmRenderer);

        this.cropInspectorManager = new CropInspectorManager(rootPane, playerController, farmRenderer, this::updateGameUI);
        this.plantMenuManager = new PlantMenuManager(rootPane, playerController, player, farmRenderer, this::updateGameUI);


        loadBackground();
        setupButtonAnimations();
        updateGameUI();
        uiManager.showNotification("Welcome Farmer!", farm.getCurrentDay());
    }

    // --- CẬP NHẬT LẠI TOÀN BỘ GIAO DIỆN ---
    public void updateGameUI() {
        uiManager.updateHUD(farm.getCurrentDay(), player.getInventory().getMoney(),
                player.getInventory().getWater(), player.getInventory().getFertilizer(), player.getInventory().getMedicine());
        farmRenderer.renderGrid(farm, this::handleTileClick, selectedCell);
    }

    // --- XỬ LÝ KHI CLICK VÀO Ô ĐẤT ---
    private void handleTileClick(int col, int row, double screenX, double screenY) {
        FarmCell cell = farm.getCell(col, row);

        // A. NẾU ĐANG TRONG CHẾ ĐỘ SỬ DỤNG CÔNG CỤ NHANH (Tưới nước/Bón phân từ HUD)
        if (toolManager.getCurrentTool() != ToolManager.ToolMode.NONE) {
            if (cell.isEmpty()) { uiManager.showAlert("Invalid", "Empty land!", Alert.AlertType.WARNING); return; }
            if (cell.getCrop().isDead()) { uiManager.showAlert("Invalid", "Crop is dead!", Alert.AlertType.WARNING); return; }

            int amount = toolManager.getToolAmount();
            boolean success = false;
            String msg = "";

            if (toolManager.getCurrentTool() == ToolManager.ToolMode.WATER) {
                success = playerController.waterCrop(new Point(col, row), amount);
                msg = success ? "Used " + amount + " Water" : "Not enough water!";
            } else if (toolManager.getCurrentTool() == ToolManager.ToolMode.FERTILIZE) {
                success = playerController.fertilizeCrop(new Point(col, row), amount);
                msg = success ? "Used " + amount + " Fert" : "Not enough fertilizer!";
            }

            updateGameUI();
            uiManager.showNotification(msg, farm.getCurrentDay());
        }
        // B. CHẾ ĐỘ CHUỘT THƯỜNG -> MỞ CÁC CỬA SỔ TƯƠNG TÁC POPUP
        else {
            selectedCell = new Point(col, row);
            updateGameUI(); // Highlight ô được chọn

            if (!cell.isEmpty()) {
                // Nếu có cây: Mở cửa sổ chi tiết (Health, Water, Harvest...) thay vì ContextMenu
                cropInspectorManager.showInspector(cell, col, row);
            } else {
                // Nếu đất trống: Mở cửa sổ chọn hạt giống để trồng theo phong cách Store
                plantMenuManager.showPlantMenu(col, row);
            }
        }
    }

    // --- XỬ LÝ CÁC NÚT BẤM TRÊN HUD ---
    @FXML private void handleStoreButton() { storeManager.showStore(); }
    @FXML private void handleBoardClick() { uiManager.showLogBoard(); }

    @FXML
    private void handleWaterButton() {
        if(toolManager.getCurrentTool() == ToolManager.ToolMode.WATER)
            toolManager.resetTool(farm.getCurrentDay());
        else
            toolManager.activateTool(ToolManager.ToolMode.WATER, farm.getCurrentDay());
        rootPane.requestFocus();
    }

    @FXML
    private void handleFertilizerButton() {
        if(toolManager.getCurrentTool() == ToolManager.ToolMode.FERTILIZE)
            toolManager.resetTool(farm.getCurrentDay());
        else
            toolManager.activateTool(ToolManager.ToolMode.FERTILIZE, farm.getCurrentDay());
        rootPane.requestFocus();
    }

    // --- XỬ LÝ KHI KẾT THÚC NGÀY (ADVANCE DAY) ---
    @FXML
    private void handleAdvanceDay() {
        if (toolManager.getCurrentTool() != ToolManager.ToolMode.NONE)
            toolManager.resetTool(farm.getCurrentDay());
        try {
            RandomEventManager eventManager = new RandomEventManager();
            String eventResult = farm.advanceDay(eventManager);

            updateGameUI();

            if (eventResult != null && !eventResult.isEmpty()) {
                uiManager.showAlert(" DAILY EVENT", eventResult, Alert.AlertType.INFORMATION);
                uiManager.showNotification("Event: " + eventResult, farm.getCurrentDay());
            } else {
                uiManager.showNotification("New day started!", farm.getCurrentDay());
            }

            // Thông báo nếu có cây bị chết trong đêm
            long deadCount = farm.getAllCrops().stream().filter(Crop::isDead).count();
            if (deadCount > 0) {
                uiManager.showAlert("⚠️ WARNING", "There are " + deadCount + " dead crops!", Alert.AlertType.WARNING);
                uiManager.showNotification("Warning: " + deadCount + " crops died!", farm.getCurrentDay());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ (HELPERS) ---
    private void loadBackground() {
        try {
            Image bgImage = farmRenderer.loadImage("Background");
            if (bgImage != null) {
                BackgroundImage bgi = new BackgroundImage(
                        bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
                );
                rootPane.setBackground(new Background(bgi));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupButtonAnimations() {
        Button[] btns = {statusButton, seedsButton, waterButton, fertilizerButton,
                equipmentButton, storeButton, infoButton, advanceDayButton, boardButton};
        for(Button b : btns) {
            if(b != null) {
                b.setOnMouseEntered(e -> { b.setScaleX(1.1); b.setScaleY(1.1); });
                b.setOnMouseExited(e -> { b.setScaleX(1.0); b.setScaleY(1.0); });
            }
        }
    }

    @FXML private void handleStatusButton() { inventoryManager.showStatus(); }
    @FXML private void handleSeedsButton() { uiManager.showAlert("Info", "Buy seeds in store", Alert.AlertType.INFORMATION); }
    @FXML private void handleEquipmentButton() { handleStatusButton(); }
    @FXML private void handleInfoButton() { uiManager.showAlert("Guide", "Plant -> Water -> Harvest", Alert.AlertType.INFORMATION); }

    @FXML public void handleSaveAndExit() {
        if (playerController != null) {
            playerController.saveGameCommand("smartfarm_save");
        }
        SceneNavigator.loadMainMenu();
        ((Stage) rootPane.getScene().getWindow()).close();
    }

    @FXML
    public void confirmExitOnClose(javafx.stage.WindowEvent event) {
        event.consume();
        if (customExitPane != null) {
            customExitPane.setVisible(true);
        }
    }

    @FXML
    public void closeExitPane() {
        if (customExitPane != null) {
            customExitPane.setVisible(false);
        }
    }
}