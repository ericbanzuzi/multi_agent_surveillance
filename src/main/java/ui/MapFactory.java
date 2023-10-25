package ui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import map.Map;
import map.scenario.Shaded;
import map.scenario.TelePortal;
import map.scenario.Wall;

import java.util.ArrayList;
import java.util.List;


public class MapFactory {
    public static Paint color1 = Color.rgb(0, 0, 0);
    public static Paint color2 = Color.rgb(153, 153, 153); // wall
    public static Paint color3 = Color.KHAKI; // teleport
    public static Paint color4 = Color.LIGHTGREY; // shaded
    public static Paint color5 = Color.rgb(0 ,255,51); // target
    private final int RECTANGLE_SCALE = MenuControllerWindow.SCALE;
    private final Paint LIGHT_RED = Color.rgb(255,102,102);

    /**
     * Creates a list of all the objects in the map as Shapes so they could be drawn in the GUI
     * @param map a map object that contains all the information for the map
     * @return a list of shapes that match the different areas in the map
     */

    protected List<Shape> createDrawableMap(Map map) {

        List<Shape> drawableShapes = new ArrayList<>();

        for(Wall wall : map.getWalls()){
            int width = wall.getRightBoundary()-wall.getLeftBoundary();
            int height = wall.getBottomBoundary() - wall.getTopBoundary();
            Rectangle rectangle = new Rectangle(wall.getLeftBoundary()*this.RECTANGLE_SCALE, wall.getTopBoundary()*this.RECTANGLE_SCALE,
                    width*this.RECTANGLE_SCALE, height*this.RECTANGLE_SCALE);
            rectangle.setFill(color1);
            drawableShapes.add(rectangle);
        }

        for(TelePortal portal : map.getTeleports()){
            int width = portal.getRightBoundary()-portal.getLeftBoundary();
            int height = portal.getBottomBoundary()-portal.getTopBoundary();
            Rectangle rectangle = new Rectangle(portal.getLeftBoundary()*this.RECTANGLE_SCALE, portal.getTopBoundary()*this.RECTANGLE_SCALE,
                    width*this.RECTANGLE_SCALE, height*this.RECTANGLE_SCALE);
            rectangle.setFill(color3);
            drawableShapes.add(rectangle);
        }

        for(Shaded shaded : map.getShaded()){
            int width = shaded.getRightBoundary()-shaded.getLeftBoundary();
            int height = shaded.getBottomBoundary()-shaded.getTopBoundary();
            Rectangle rectangle = new Rectangle(shaded.getLeftBoundary()*this.RECTANGLE_SCALE, shaded.getTopBoundary()*this.RECTANGLE_SCALE,
                    width*this.RECTANGLE_SCALE, height*this.RECTANGLE_SCALE);
            rectangle.setFill(color4);
            rectangle.setOpacity(0.5);
            drawableShapes.add(rectangle);

            int i = shaded.getTopBoundary();
            while(i < shaded.getBottomBoundary()){
                Line line = new Line(shaded.getLeftBoundary()*this.RECTANGLE_SCALE, i*this.RECTANGLE_SCALE,
                        shaded.getRightBoundary()*this.RECTANGLE_SCALE, i*this.RECTANGLE_SCALE);
                line.setStroke(Color.WHITE);
                line.setStrokeWidth(2);
                drawableShapes.add(line);
                i++;
            }

        }

        if(map.getTargetArea() != null) {
            int width = map.getTargetArea().getRightBoundary() - map.getTargetArea().getLeftBoundary();
            int height = map.getTargetArea().getBottomBoundary() - map.getTargetArea().getTopBoundary();
            Rectangle rectangle = new Rectangle(map.getTargetArea().getLeftBoundary() * this.RECTANGLE_SCALE, map.getTargetArea().getTopBoundary() * this.RECTANGLE_SCALE,
                    width * this.RECTANGLE_SCALE, height * this.RECTANGLE_SCALE);
            rectangle.setFill(color5);
            drawableShapes.add(rectangle);
        }

        return drawableShapes;
    }

    public List<Shape> getTeleportMarkers(Map map){
        List<double[]> boundaries = map.getBoundariesOfTeleMarkers();
        List<Shape> drawableShapes = new ArrayList<>();
        for(double [] bs : boundaries){
            Line top = new Line(bs[0]*this.RECTANGLE_SCALE, bs[2]*this.RECTANGLE_SCALE,
                    bs[1]*this.RECTANGLE_SCALE, bs[2]*this.RECTANGLE_SCALE);
            top.setStroke(LIGHT_RED);
            top.setStrokeWidth(3);
            drawableShapes.add(top);
            Line bottom = new Line(bs[0]*this.RECTANGLE_SCALE, bs[3]*this.RECTANGLE_SCALE,
                    bs[1]*this.RECTANGLE_SCALE, bs[3]*this.RECTANGLE_SCALE);
            bottom.setStroke(LIGHT_RED);
            bottom.setStrokeWidth(3);
            drawableShapes.add(bottom);
            Line left = new Line(bs[0]*this.RECTANGLE_SCALE, bs[2]*this.RECTANGLE_SCALE,
                    bs[0]*this.RECTANGLE_SCALE, bs[3]*this.RECTANGLE_SCALE);
            left.setStroke(LIGHT_RED);
            left.setStrokeWidth(3);
            drawableShapes.add(left);
            Line right = new Line(bs[1]*this.RECTANGLE_SCALE, bs[2]*this.RECTANGLE_SCALE,
                    bs[1]*this.RECTANGLE_SCALE, bs[3]*this.RECTANGLE_SCALE);
            right.setStroke(LIGHT_RED);
            right.setStrokeWidth(3);
            drawableShapes.add(right);
        }
        return drawableShapes;
    }

    public int getRECTANGLE_SCALE(){
        return RECTANGLE_SCALE;
    }
}
