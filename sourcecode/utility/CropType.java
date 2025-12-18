package utility;

public enum CropType {
    WHEAT("Wheat",5, 10),
    CORN("Corn",8,20),
    CARROT("Carrot",15,32),
    TOMATO("Tomato",20,45),
    PUMPKIN("Pumpkin",30,80);
    private final String cropName;
    private final int seedPrice;
    private final int basePriceCrop;
    CropType(String cropName,int seedPrice,int basePriceCrop){
        this.cropName=cropName;
        this.seedPrice=seedPrice;
        this.basePriceCrop=basePriceCrop;
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
}
