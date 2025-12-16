package core;

import utility.Point;

public class FarmCell {
    private Point position;
    private Crop crop;
    public FarmCell(Point position){
        this.position=position;
        this.crop=null;
    }
    public boolean plantCrop(Crop crop){
        if(crop==null){
            System.out.println("Cannot plant in null crop");
            return false;
        }
        if(!isEmpty()){
            System.out.println("This cell has already been planted");
            return false;
        }
        this.crop=crop;
        System.out.println("You have planted "+ crop.getCropType().getCropName()+" at "+ position);
        return true;
    }
    public Crop getCrop() {
        return crop;
    }
    public Crop removeCrop() {
        if (crop == null) {
            return null;
        }
        Crop removed = this.crop;
        this.crop = null;

        System.out.println("Removed " + removed.getCropType().getCropName() +
                " from " + position);
        return removed;
    }
    public boolean isEmpty(){
        return crop==null;
    }
    public Point getPosition() {
        return position;
    }
    @Override
    public String toString() {
        if (crop != null) {
            return String.format("Cell[%s] →  %s",
                    position,
                    crop.getCropType().getCropName()
            );
        } else {
            return String.format("Cell[%s] → Empty", position);
        }
    }
}
