package core;

import utility.*;

public abstract class Crop extends Entity {
    private CropStage currentStage;
    protected CropType cropType;
    protected int waterLevel;
    protected int fertilizerLevel;
    private int health;
    private int daysCurrentStage;
    private boolean isDamaged;
    protected int maxWaterLevel;
    protected int maxFertilizerLevel;
    protected final int MAX_HEALTH =100;
    protected int waterPerDay;
    protected int fertilizerPerDay;
    protected int basePrice;
    protected int []dayPerStage;
    protected int waterNeedThreshold;
    protected int fertilizerNeedThreshold;
    public Crop(String id, Point position, CropType cropType){
        super(id, position);
        this.cropType = cropType;
        this.currentStage=CropStage.SEED;
        this.maxWaterLevel=50;
        this.maxFertilizerLevel=50;
        this.health=MAX_HEALTH;
        this.waterLevel=this.maxWaterLevel/2;
        this.fertilizerLevel=this.maxFertilizerLevel/2;
        this.waterPerDay=10;
        this.fertilizerPerDay=5;
        this.waterNeedThreshold=20;
        this.fertilizerNeedThreshold=10;
        this.daysCurrentStage=0;
        this.isDamaged = false;
        this.basePrice = cropType.getBasePriceCrop();
    }
    public void grow(){
        if(isDead()){
            return;
        }
        consumeResource();
        checkHealthStatus();
        if(!isDead()&& hasEnoughResources()){
            daysCurrentStage++;
            if (canAdvanceStage()) {
                advanceToNextStage();
            }
        }
    }
    public void water(int amount){
        if (isDead()){
            System.out.println("Cannot water dead crop");
            return;
        }
        waterLevel=Math.min(this.maxWaterLevel,this.waterLevel+amount);
        System.out.println(cropType.getCropName()+" is watered. Water level is: "+waterLevel);
    }
    public void fertilize(int amount){
        if (isDead()){
            System.out.println("Cannot fertilize dead crop");
            return;
        }
        fertilizerLevel=Math.min(this.maxFertilizerLevel,this.fertilizerLevel+amount);
        System.out.println(cropType.getCropName()+" is fertilized. Fertilizer level is: "+fertilizerLevel);
    }
    public int recycle(){
        if (!isDead()){
            return 0;
        }
        int fertilizerRecycle= 5;
        System.out.println("you have gained 5 fertilizer");
        return fertilizerRecycle;
    }
    public boolean needWater(){
        return waterLevel< waterNeedThreshold;
    }
    public boolean needFertilizer(){
        return fertilizerLevel<fertilizerNeedThreshold;
    }
    public boolean isHarvestable(){
        return currentStage== CropStage.HARVESTABLE && !isDead();
    }
    public boolean isDead(){
        return health<=0 || currentStage == CropStage.DEAD;
    }
    public CropStage getCurrentStage() {
        return currentStage;
    }
    public int getWaterLevel() {
        return waterLevel;
    }
    public int getFertilizerLevel() {
        return fertilizerLevel;
    }
    public int getHealth() {
        return health;
    }
    public int getDaysInCurrentStage() {
        return daysCurrentStage;
    }
    public CropType getCropType() {
        return cropType;
    }
    protected abstract void consumeResource();
    public abstract int harvest();
    protected boolean hasEnoughResources() {
        return waterLevel > 0;
    }
    public boolean isDamaged() {
        return isDamaged;
    }
    protected void checkHealthStatus() {
        if (waterLevel <= 0) {
            takeDamage(15);
        } else if (waterLevel < 20) {
            takeDamage(5);
        }
        if (fertilizerLevel <= 0) {
            takeDamage(5);
        }
    }
    public void takeDamage(int damageAmount) {
        health = Math.max(0, health - damageAmount);
        isDamaged = true;

        if (isDead()) {
            currentStage = CropStage.DEAD;
            System.out.println(cropType.getCropName() + " has died!");
        }
    }
    protected int calculateHarvestValue(){
        if(!isHarvestable()){
            return 0;
        }
        double value = basePrice;
        if (health<=50&& health>0){
            return (int) Math.round(value);
        }
        return (int) Math.round(value+(health/100.0)*value);
    }
    @Override
    public void update() {
        grow();
    }
    protected boolean canAdvanceStage() {
        if (currentStage == CropStage.HARVESTABLE || currentStage == CropStage.DEAD) {
            return false;
        }

        int currentStageIndex = currentStage.ordinal();
        return daysCurrentStage >= dayPerStage[currentStageIndex];
    }
    protected void advanceToNextStage() {
        switch (currentStage) {
            case SEED:
                currentStage = CropStage.SEEDLING;
                break;
            case SEEDLING:
                currentStage = CropStage.MATURE;
                break;
            case MATURE:
                currentStage = CropStage.HARVESTABLE;
                break;
        }
        daysCurrentStage = 0;
    }
    public int getDaysHarvest() {
     if (this.currentStage == CropStage.HARVESTABLE || this.currentStage == CropStage.DEAD) {
            return 0;
        }
        int currentStageIndex = this.currentStage.ordinal();
        int daysRemaining = this.dayPerStage[currentStageIndex] - this.daysCurrentStage;
        for (int i = currentStageIndex + 1; i < 3; i++) {
            daysRemaining += this.dayPerStage[i];
        }
        return daysRemaining;
    }
    public CropStatus getStatus() {
        return new CropStatus(this);
    }
    public boolean canBeRecycled() {
        if (this.isDead()) {
            return true;
        }
        if (this.health <= 25) {
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return String.format(
                "%s at %s | Stage: %s | Health: %d%% | Water: %d%% | Fertilizer: %d%%",
                cropType.getCropName(),
                position,
                currentStage.getDisplayStage(),
                health,
                waterLevel,
                fertilizerLevel
        );
    }
}
