package explorer;

import map.Vector2d;

import java.util.List;

public class Item {
    public Vector2d center;
    public Vector2d[] corners;
    public Vector2d[] sides = new Vector2d[4];

    public double maxX;
    public double maxY;
    public double minX;
    public double minY;

    public Item(double x1, double y1, double x2, double y2){
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);

        this.center = new Vector2d(
                (maxX+minX)/2,
                (maxY+minY)/2
        );

        this.corners = new Vector2d[]{
                new Vector2d(minX, maxY),
                new Vector2d(maxX, maxY),
                new Vector2d(maxX, minY),
                new Vector2d(minX, minY)
        };
    }

    /*
    public Item(Vector2d center, Vector2d[] corners) {
        this.center = center;
        this.corners = corners;

        maxX = Double.NEGATIVE_INFINITY;
        maxY = Double.NEGATIVE_INFINITY;
        minX = Double.POSITIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        for(Vector2d corner : corners){
            maxX = Math.max(maxX, corner.getX());
            maxY = Math.max(maxY, corner.getY());
            minX = Math.min(minX, corner.getX());
            minY = Math.min(minY, corner.getY());

        }
    }*/

    public Vector2d[] getSides() {
        for (int i = 0; i < corners.length; i++) {
            // for the last pair, we loop the second corner parameter
            // back to the first one in the corners list
            if (i == corners.length - 1) {
                sides[i] = getSideOfTwoCorners(corners[i], corners[0]);
            } else {
                sides[i] = getSideOfTwoCorners(corners[i], corners[i + 1]);
            }
        }
        return sides;
    }

    public Vector2d getSideOfTwoCorners(Vector2d corner1, Vector2d corner2) {
        return corner2.minus(corner1);
    }


}
