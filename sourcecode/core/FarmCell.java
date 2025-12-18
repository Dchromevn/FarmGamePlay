package core;

import utility.Point;
import exceptions.CellOccupiedException;
public class FarmCell {
    private Point position;
    private Crop crop;
    public FarmCell(Point position){
        this.position=position;
        this.crop=null;
    }
    public void plantCrop(Crop crop){
        if(crop==null){
        	throw new IllegalArgumentException("Crop cannot be null");
        }
        if(!isEmpty()){
        	throw new CellOccupiedException("Cell "+ position +" is already occupied.");

        }
        this.crop=crop;
        
    }
    public Crop getCrop() {
        return crop;
    }
//require for a crop here
    public Crop requireCrop() {
    	if (crop == null) {
    		throw new IllegalStateException("No crop at "+ position);
    	}
    	return crop;
    }
    public Crop removeCrop() {
        if (crop == null) {
        	throw new IllegalStateException("Cannot remove crop from empty cell.");
        }
        Crop removed = this.crop;
        this.crop = null;

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
