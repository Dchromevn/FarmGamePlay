package model.core;
import java.io.Serializable;

import utility.Point;

public abstract class Entity implements Serializable {
    protected String id;
    protected Point position;
    public Entity(String id,Point position){
        this.id=id;
        this.position=position;
    }
    public String getID(){
        return id;
    }
    public Point getPosition(){
        return position;
    }
    public abstract void update();
}
