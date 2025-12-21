package controller.game;

import model.crops.Crop;
import model.core.Farm;
import model.core.FarmCell;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import utility.Point;

import java.util.HashMap;
import java.util.Map;

public class FarmRenderer {
    private Pane farmPane;
    private final Map<String, Image> imageCache = new HashMap<>();

    // --- Interface để xử lý click chuột (Nhận thêm tọa độ màn hình) ---
    public interface TileClickHandler {
        void onClick(int col, int row, double screenX, double screenY);
    }

    // Cấu hình Grid

    private static final double TILE_WIDTH = 90;
    private static final double TILE_HEIGHT = 45;
    private static final double IMAGE_SIZE = 150;
    private static final double BASE_OFFSET_X = 550;
    private static final double BASE_OFFSET_Y = 170;

    private static final int GRID_SIZE = 5;

    public FarmRenderer(Pane farmPane) {
        this.farmPane = farmPane;
    }

    // Cập nhật tham số thành TileClickHandler
    public void renderGrid(Farm farm, TileClickHandler onTileClick, Point selectedCell) {
        farmPane.getChildren().clear();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                FarmCell cell = farm.getCell(col, row);
                StackPane tile = createIsometricTile(row, col, cell, onTileClick);

                double x = (col - row) * (TILE_WIDTH / 2) + BASE_OFFSET_X;
                double y = (col + row) * (TILE_HEIGHT / 2) + BASE_OFFSET_Y;

                tile.setLayoutX(x);
                tile.setLayoutY(y);

                if (selectedCell != null && selectedCell.getX() == col && selectedCell.getY() == row) {
                    if (tile.getChildren().size() > 1)
                        tile.getChildren().get(1).setEffect(new DropShadow(20, Color.YELLOW));
                }

                farmPane.getChildren().add(tile);
            }
        }
    }

    private StackPane createIsometricTile(int row, int col, FarmCell cell, TileClickHandler onTileClick) {
        StackPane tile = new StackPane();
        tile.setPrefSize(TILE_WIDTH, TILE_HEIGHT * 2);
        tile.setPickOnBounds(false);

        Polygon hitbox = new Polygon(TILE_WIDTH / 2.0, 0.0, TILE_WIDTH, TILE_HEIGHT / 2.0, TILE_WIDTH / 2.0, TILE_HEIGHT, 0.0, TILE_HEIGHT / 2.0);
        hitbox.setFill(Color.TRANSPARENT);
        hitbox.setStroke(Color.TRANSPARENT);

        ImageView visual = new ImageView();
        visual.setFitWidth(IMAGE_SIZE);
        visual.setFitHeight(IMAGE_SIZE);
        visual.setPreserveRatio(true);
        visual.setMouseTransparent(true);

        if (cell.isEmpty()) visual.setImage(loadImage("Land"));
        else visual.setImage(loadImage(getCropImageName(cell.getCrop())));

        tile.getChildren().addAll(hitbox, visual);
        if (!cell.isEmpty()) addCropOverlays(tile, cell.getCrop());

        ColorAdjust br = new ColorAdjust(); br.setBrightness(0.3);
        hitbox.setOnMouseEntered(e -> visual.setEffect(br));
        hitbox.setOnMouseExited(e -> visual.setEffect(null));

        // --- QUAN TRỌNG: Truyền tọa độ màn hình khi click ---
        hitbox.setOnMouseClicked(e -> onTileClick.onClick(col, row, e.getScreenX(), e.getScreenY()));

        return tile;
    }

    private void addCropOverlays(StackPane tile, Crop crop) {
        if (crop.isDead()) return;
        VBox overlay = new VBox(2);
        overlay.setAlignment(Pos.CENTER);
        overlay.setMouseTransparent(true);
        overlay.setTranslateY(-110);
        if (crop.getWaterLevel() < 30) {
            ImageView waterIcon = new ImageView(loadImage("Water_Pod"));
            waterIcon.setFitWidth(25); waterIcon.setFitHeight(25);
            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), waterIcon);
            fade.setFromValue(1.0); fade.setToValue(0.2); fade.setCycleCount(Animation.INDEFINITE); fade.setAutoReverse(true); fade.play();
            overlay.getChildren().add(waterIcon);
        }
        tile.getChildren().add(overlay);
    }

    private String getCropImageName(Crop crop) {
        String typeStr = crop.getCropType().toString();
        String typeName = typeStr.substring(0, 1).toUpperCase() + typeStr.substring(1).toLowerCase();
        String suffix = "Mature";
        if (crop.isDead()) suffix = "Dead";
        else if (crop.isHarvestable()) {
            suffix = "Harvestable";
        } else {
            String stage = crop.getCurrentStage().toString();
            if (stage.equals("SEED")) suffix = "Seed";
            else if (stage.equals("SEEDLING")) suffix = "Seedling";
        }
        return typeName + "_" + suffix;
    }

    public Image loadImage(String fileName) {
        String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
        if (imageCache.containsKey(baseName)) return imageCache.get(baseName);
        String[] extensions = {".png", ".jpg", ".jpeg", ".PNG", ".JPG", ".JPEG"};
        Image img = null;
        for (String ext : extensions) {
            String path = "/view/images/" + baseName + ext;
            try {
                java.io.InputStream stream = getClass().getResourceAsStream(path);
                if (stream == null) {
                    path = "/images/" + baseName + ext;
                    stream = getClass().getResourceAsStream(path);
                }
                if (stream != null) {
                    img = new Image(stream);
                    if (!img.isError()) break;
                }
            } catch (Exception e) {}
        }
        if (img == null || img.isError()) {
            if (!baseName.equals("Land")) return loadImage("Land");
            return null;
        }
        imageCache.put(baseName, img);
        return img;
    }
}