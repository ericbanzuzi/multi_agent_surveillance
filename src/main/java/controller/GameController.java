package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import map.scenario.Scenario;
import ui.MapWindow;

public class GameController implements Runnable{

    private static final boolean DEBUG = false;

    private final Scenario scenario;
    private MapWindow view;
    private Timeline timeline;
    private GameState gameState;
    private boolean paused;

    public GameController(Scenario scenario){
        this.scenario = scenario;
        this.gameState = new GameState(scenario);
        this.paused = false;
        this.timeline = new Timeline(new KeyFrame(Duration.millis(100), event ->{
            long time = System.currentTimeMillis();
            long actTime = System.currentTimeMillis();
            gameState.next();
            if(DEBUG) System.out.println("Act time needed: " + (System.currentTimeMillis() - actTime) + " ms");
            long renderTime = System.currentTimeMillis();
            this.render();
            if(DEBUG) {
                System.out.println("Render time needed: " + (System.currentTimeMillis() - renderTime) + " ms");
                System.out.println("Timeline time needed: " + (System.currentTimeMillis() - time) + " ms");
                System.out.println();
            }
            //todo remove just for map building and photos
            //timeline.pause();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.pause();
    }

    @Override
    public void run() {
        while(!paused) {
            try {
                Thread.sleep(GameRunner.simulationDelay);
            } catch (InterruptedException ignored) {
            }
            gameState.next();
        }
    }

    public void pause() {
        timeline.pause();
        paused = true;
    }

    public void play() {
        timeline.play();
        paused = false;
    }

    public GameState getGameState() {
        return gameState;
    }

    public MapWindow getView() {
        return view;
    }

    public void setView(MapWindow view) {
        this.view = view;
    }

    private void render(){
        this.view.renderMap();
    }
}
