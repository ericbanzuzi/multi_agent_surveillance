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
public class Area {
    protected int leftBoundary;
    protected int rightBoundary;
    protected int topBoundary;
    protected int bottomBoundary;
    
    public Area(){
        leftBoundary=0;
        rightBoundary=1;
        topBoundary=0;
        bottomBoundary=1;
    }
    
    public Area(int x1, int y1, int x2, int y2){
        leftBoundary=Math.min(x1,x2);
        rightBoundary=Math.max(x1,x2);
        topBoundary=Math.min(y1,y2);
        bottomBoundary=Math.max(y1,y2);
    }
    
    /*
        Check whether a point is in the target area
    */
    public boolean isHit(double x,double y){
        return (y<=bottomBoundary)&(y>=topBoundary)&(x>=leftBoundary)&(x<=rightBoundary);
    }

    /*
        Check whether something with a radius is in the target area
        STILL TO BE IMPLEMENTED
    */
    public boolean isHit(double x,double y,double radius){
        return (x-radius >= leftBoundary) && (x+radius <= rightBoundary) &&
                (y+radius >= topBoundary) && (y-radius <= bottomBoundary);
    }
    
    /*
        Check whether some area is adjacent to another area
    */
    public boolean isAdjacent(Area area){
        // case when the areas are connected on their sides
        if(leftBoundary == area.rightBoundary || rightBoundary == area.leftBoundary){
            return overlapsVertically(area);
            // case when the areas are connected on from top and bottoms
        } else if(topBoundary == area.bottomBoundary || bottomBoundary == area.topBoundary){
            return overlapsHorizontally(area);
        }
        return false;
    }

    public boolean connectedToTop(Area area) {
        if (topBoundary == area.bottomBoundary) {
            return overlapsHorizontally(area);
        }
        return false;
    }

    public boolean connectedToBottom(Area area) {
        if (area.topBoundary == bottomBoundary) {
            return overlapsHorizontally(area);
            }
        return false;
    }

    public boolean connectedToLeft(Area area){
        if(area.rightBoundary == leftBoundary){
            return overlapsVertically(area);
        }
        return false;
    }

    public boolean connectedToRight(Area area){
        if(rightBoundary == area.leftBoundary){
            return overlapsVertically(area);
        }
        return false;
    }

    /**
     * Checks if two areas overlap in their horizontal dimensions
     * @param area area to compare to this area
     * @return true if there is overlap, false otherwise
     */
    public boolean overlapsHorizontally(Area area){
        if ((area.leftBoundary >= leftBoundary) && (area.leftBoundary < rightBoundary) ||
                (area.rightBoundary > leftBoundary) && (area.rightBoundary <= rightBoundary)) {
            return true;
        }
        return (leftBoundary >= area.leftBoundary) && (leftBoundary < area.rightBoundary) ||
                (rightBoundary > area.leftBoundary) && (rightBoundary <= area.rightBoundary);
    }

    /**
     * Checks if two areas overlap in their vertical dimensions
     * @param area area to compare to this area
     * @return true if there is overlap, false otherwise
     */
    public boolean overlapsVertically(Area area){
        if(((area.bottomBoundary <= bottomBoundary)&&(area.bottomBoundary > topBoundary))||
                ((area.topBoundary < bottomBoundary)&&(area.topBoundary >= topBoundary))){
            return true;
        }
        // connects through the whole side
        return ((bottomBoundary <= area.bottomBoundary) && (bottomBoundary > area.topBoundary)) ||
                ((topBoundary < area.bottomBoundary) && (topBoundary >= area.topBoundary));
    }

    /*
        Check whether some point is in the bottom of area
    */
    public boolean inBottom(int x, int equalY){
        if(equalY == bottomBoundary && ((x>=leftBoundary)&&(x<=rightBoundary))){
            return true;
        }
        return false;
    }

    /*
        Check whether some point is in the top of area
    */
    public boolean inTop(int x, int equalY){
        if(equalY == topBoundary && ((x>=leftBoundary)&&(x<=rightBoundary))){
            return true;
        }
        return false;
    }

    /*
        Check whether some point is in the right of area
    */
    public boolean inRight(int equalX, int y){
        if(equalX == rightBoundary && ((y>=bottomBoundary)&&(y<=topBoundary))){
            return true;
        }
        return false;
    }

    /*
        Check whether some point is in the left of area
    */
    public boolean inLeft(int equalX, int y){
        if(equalX == leftBoundary && ((y>=bottomBoundary)&&(y<=topBoundary))){
            return true;
        }
        return false;
    }

    public int getLeftBoundary() {
        return leftBoundary;
    }

    public int getRightBoundary() {
        return rightBoundary;
    }

    public int getTopBoundary() {
        return topBoundary;
    }

    public int getBottomBoundary() {
        return bottomBoundary;
    }

    public int[] getCenter() {
        int x = (rightBoundary + leftBoundary)/2;
        int y = (bottomBoundary + topBoundary)/2;
        return new int[]{x,y};
    }
}
