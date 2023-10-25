/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map.scenario;

/**
 *
 * @author joel
 */
public class TelePortal extends Area {
    protected int yTarget;
    protected int xTarget;
    protected double outOrientation;
    private boolean marked;
    
    public TelePortal(int x1, int y1, int x2, int y2, int targetX, int targetY){
        super(x1,y1,x2,y2);
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = 0.0;
    }

    public TelePortal(int x1, int y1, int x2, int y2, int targetX, int targetY, double orient){
        super(x1,y1,x2,y2);
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = orient;
    }
    
    public int[] getNewLocation(){
        int[] target = new int[] {xTarget, yTarget};
        return target;
    }

    public double getNewOrientation(){
        return outOrientation;
    }

    public int getyTarget(){
        return yTarget;
    }

    public int getxTarget(){
        return xTarget;
    }

    public void mark(){
        this.marked = true;
    }

    public void unmark(){
        this.marked = false;
    }

    public boolean isMarked(){
        return marked;
    }
}


