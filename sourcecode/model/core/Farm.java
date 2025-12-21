package model.core;
import java.io.Serializable;

import model.crops.Crop;
import utility.Point;
import service.eventSystem.RandomEventManager;
import java.util.ArrayList;
import java.util.List;
import service.eventSystem.GameEvent;
import model.exceptions.*;
public class Farm implements Serializable  {
    private FarmCell[][] grid;
    private int width;
    private int height;
    private int currentDay;
    public Farm(int width, int height) {
        this.width = width;
        this.height = height;
        this.currentDay = 1;
        createGrid();
        System.out.println("You have created a farm");
    }
    private void createGrid(){
        grid = new FarmCell[height][width];
        for (int i=0; i < height; i++){
            for (int j=0; j < width; j++){
                grid[i][j] = new FarmCell(new Point(j, i));
            }
        }
    }
    public FarmCell getCell(int x, int y){
        if(!isValidPosition(x,y)){
        	throw new InvalidPositionException("Invalid position: "+x+ ", "+y);
        }
        return grid[y][x];
    }
    public FarmCell getCell(Point position){
        return getCell(position.getX(),position.getY());
    }
    
    public Crop getCrop(int x, int y) {
        FarmCell cell = getCell(x, y);
        if (cell != null && !cell.isEmpty()) {
            return cell.getCrop();
        }
        return null;
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public int getCropCount() {
        return getAllCrops().size();
    }
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    public List<Crop> getAllCrops(){
        List<Crop> crop= new ArrayList<>();
        for (int y=0;y<height;y++){
            for (int x=0;x< width;x++){
                FarmCell cell=grid[y][x];
                if (!cell.isEmpty()){
                    crop.add(cell.getCrop());
                }
            }
        }
        return crop;
    }

    public List<FarmCell> getEmptyCells(){
        List<FarmCell> emptyCells =new ArrayList<>();
        for (int y=0; y<height;y++){
            for (int x=0;x<width;x++){
                FarmCell cell= grid[y][x];
                if (cell.isEmpty()){
                    emptyCells.add(cell);
                }
            }
        }
        return emptyCells;
    }
    public String advanceDay(RandomEventManager eventManager) {
        System.out.println("Day " + currentDay + " ended.");
        currentDay++;
        System.out.println("\nDay " + currentDay + " begins...");
        GameEvent event = eventManager.triggerRandomEvent();
        String eventResult = null;
        if(event!= null){
            eventResult=event.triggerEvent(this);
        }
        updateAllCrops();
        return eventResult;
    }
    public void updateAllCrops() {
        List<Crop> crops = getAllCrops();

        if (crops.isEmpty()) {
            System.out.println(" No crops to update.");
            return;
        }
        System.out.println("Updating " + crops.size() + " crops...");

        for (Crop crop : crops) {
            crop.grow();
        }
    }public void printFarm() {
        System.out.println("FARM (Day " + currentDay + ")");
        System.out.println("═".repeat(width * 4 + 1));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                FarmCell cell = grid[y][x];

                if (cell.isEmpty()) {
                    System.out.print("│  ");
                } else {
                    Crop crop = cell.getCrop();
                    System.out.print("│" + crop.getCurrentStage()+ " ");
                }
            }
            System.out.println("│");
        }

        System.out.println("═".repeat(width * 4 + 1));
        System.out.println("Crops: " + getCropCount() + "/" + (width * height));
    }

    @Override
    public String toString() {
        return String.format(
                "Farm[%dx%d] | Day: %d | Crops: %d/%d",
                width, height, currentDay, getCropCount(), width * height
        );
    }
}