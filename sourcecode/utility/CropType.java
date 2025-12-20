package utility;

public enum CropType {
    WHEAT("Wheat",5, 10,50,30,15,10, new int[]{1, 1, 1}),
    CORN("Corn",8,20,80,50,20,15, new int[]{1,2,2}),
    CARROT("Carrot",15,32,100,6,25,15, new int[]{2,2,2}),
    TOMATO("Tomato",20,45,120,80,30,20, new int[]{2,3,2}),
    PUMPKIN("Pumpkin",30,80,150,120,40,30, new int[]{3,3,3});
    private final String cropName;
    private final int seedPrice;
    private final int basePriceCrop;
    private final int maxWaterLevel;
    private final int maxFertilizerLevel;
    private final int waterNeedThreshold;
    private final int fertilizerNeedThreshold;
    private final int []dayPerStage;

    CropType(String cropName,int seedPrice,int basePriceCrop,int maxWaterLevel,
             int maxFertilizerLevel,int waterNeedThreshold,int fertilizerNeedThreshold,int []dayPerStage

    ){
        this.cropName=cropName;
        this.seedPrice=seedPrice;
        this.basePriceCrop=basePriceCrop;
        this.maxWaterLevel=maxWaterLevel;
        this.maxFertilizerLevel=maxFertilizerLevel;
        this.waterNeedThreshold=waterNeedThreshold;
        this.fertilizerNeedThreshold=fertilizerNeedThreshold;
        this.dayPerStage=dayPerStage;
    }
    public String getCropName(){
        return cropName;
    }
    public int getSeedPrice(){
        return seedPrice;
    }
    public int getBasePriceCrop(){
        return basePriceCrop;
    }
    public int getMaxWaterLevel(){return maxWaterLevel;}
    public int getMaxFertilizerLevel(){return maxFertilizerLevel;}
    public int getWaterNeedThreshold(){return waterNeedThreshold;}
    public int getFertilizerNeedThreshold(){return fertilizerNeedThreshold;}
    public int[] getDayPerStage() { return dayPerStage; }
}
