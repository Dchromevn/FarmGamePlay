package core;

import utility.Point;

public abstract class Entity {
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
    public void setPosition(Point position){
        this.position=position;
    }
    public abstract void update();
}
