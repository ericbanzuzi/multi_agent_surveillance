package ui;

import agent.Guard;
import agent.markers.PheromoneMarker;
import controller.GameController;
import controller.GameRunner;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import map.Map;
import map.Tile;
import map.scenario.Scenario;

import java.util.ArrayList;
import java.util.List;


public class MapWindow extends AnchorPane {

    private final int MAP_HEIGHT;
    private final int MAP_WIDTH;
    private final int SCALE = MenuControllerWindow.SCALE;
    private final List<DrawableAgent> intruders;
    private final List<DrawableAgent> guards;
    private final List<Shape> drawableShapes;
    private AnchorPane mainPane;
    private GameController gameController;
    private final MapFactory mapFactory;
    private final Map map;
    private  PheromoneMarker pheromoneMarker;

    /**
     * Constructs a MapView and adds all shapes to be rendered to an AnchorPane
     * @param sc a scenario object with all information
     */
    public MapWindow(Scenario sc) {
        this.map = sc.getMap();

        MAP_WIDTH = this.map.getWidth()*this.SCALE;
        MAP_HEIGHT = this.map.getHeight()*this.SCALE;

        this.prefHeight(MAP_WIDTH);
        this.prefWidth(MAP_HEIGHT);

        this.mainPane = new AnchorPane();
        this.mainPane.setStyle("-fx-background-color: white;");

        //Create areas in the map
        this.mapFactory = new MapFactory();
        this.drawableShapes = mapFactory.createDrawableMap(map);
        for (Shape rectangle : this.drawableShapes) {
            this.mainPane.getChildren().add(rectangle);
        }

        // Create intruders and guards in the map
//        gameController = GameRunner.runner.getController();
//        gameController = new GameController(sc);
        this.gameController = sc.getGameController();
        this.gameController.setView(this);
        AgentFactory agentFactory = new AgentFactory();
        this.guards = agentFactory.createDrawableGuards(gameController.getGameState().getGuards());
        this.intruders = agentFactory.createDrawableIntruders(gameController.getGameState().getIntruders());

        for (DrawableAgent intruder : this.intruders) {
            this.mainPane.getChildren().add(intruder);
            this.mainPane.getChildren().add(intruder.getVision());
        }

        for (DrawableAgent guard : this.guards) {
            this.mainPane.getChildren().add(guard);
            this.mainPane.getChildren().add(guard.getVision());
        }

        this.getChildren().add(mainPane);
        renderMap();
    }

    public void renderMap(){
        for(DrawableAgent guard : guards){
            guard.updatePosition();
        }

        for(DrawableAgent intruder : intruders){
            intruder.updatePosition();
        }

        this.getChildren().remove(this.mainPane);
        this.mainPane.getChildren().clear();
        this.mainPane = new AnchorPane();
        for (Shape rectangle : this.drawableShapes) {
            this.mainPane.getChildren().add(rectangle);
        }
        // set marker if someone placed it
        if(this.map.teleportMarked()){
            List<Shape> markers = this.mapFactory.getTeleportMarkers(this.map);
            for(Shape marker: markers){
                this.mainPane.getChildren().add(marker);
            }
        }
        for (DrawableAgent intruder : this.intruders) {
            this.mainPane.getChildren().add(intruder);
            this.mainPane.getChildren().add(intruder.getVision());
        }

        for (DrawableAgent guard : this.guards) {
            this.mainPane.getChildren().add(guard);
            this.mainPane.getChildren().add(guard.getVision());
        }

       try {
           for(Tile tile : gameController.getGameState().marked){
               int x = (int) tile.getX();
               int y = (int) tile.getY();
               int scale = this.mapFactory.getRECTANGLE_SCALE();
               Shape shape = new Rectangle(x * scale, y * scale, 3, 3);
               shape.setFill(Color.GREEN);
               mainPane.getChildren().add(shape);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }

        try {
            ArrayList<List<Integer>> yelltiles = gameController.getGameState().Yelltiles;
            for (int i = 0; i < yelltiles.size(); i++) {
                if(yelltiles.get(i).get(2) == 10){
                   yelltiles.remove(i);
                } else {
                    int x = yelltiles.get(i).get(0);
                    int y = yelltiles.get(i).get(1);
                    if(yelltiles.get(i).get(2) <= 3){
                        drawArc(x,y,6);
                    } else if(yelltiles.get(i).get(2) <= 6){
                        drawArc(x,y,13);
                    } else{
                        drawArc(x,y,20);
                    }
                    yelltiles.get(i).set(2, yelltiles.get(i).get(2) + 1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        this.getChildren().add(mainPane);
    }

    private void drawArc(int x, int y, int rad){
        int scale = this.mapFactory.getRECTANGLE_SCALE();
        Arc shape = new Arc(x * scale, y * scale,rad * scale, rad * scale, 0, 360);
        shape.setStroke(Color.BLUE);
        shape.setStrokeWidth(2);
        shape.setFill(Color.TRANSPARENT);
        mainPane.getChildren().add(shape);
    }

    public void showSimulation(){
        this.gameController.play();
    }

}
