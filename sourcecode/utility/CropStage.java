package utility;
public enum CropStage {
    SEED("Seed"),
    SEEDLING("Seedling"),
    MATURE("Mature"),
    HARVESTABLE("Harvestable"),
    DEAD("Dead");
    private final String displayStage;
    CropStage(String displayStage){
        this.displayStage=displayStage;
    }
    public String getDisplayStage(){
        return displayStage;
    }
}
