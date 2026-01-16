package utility;
import java.io.Serializable;

public class Point implements Serializable {
    private int x;
    private int y;
    public  Point(int x,int y){
        this.x=x;
        this.y=y;
    }
    public int getX(){
        return x;
    }
    public int getY() {
        return y;
    }
    @Override
    public boolean equals(Object obj){
        if (this ==obj) return true;
        if (obj == null || getClass()!= obj.getClass()) return false;
        Point point =(Point) obj;
        return x==point.x && y ==point.y;
    }
    @Override
    public String toString(){
        return "("+x+", "+y+")";
    }
}
