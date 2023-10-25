package agent;

import Exceptions.NoPathException;
import agent.explorer.AStar;
import agent.explorer.Vertex;
import agent.sensation.SmellMatrix;
import agent.sensation.SoundMatrix;
import agent.sensation.VisionMatrix;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import controller.GameRunner;
import map.Tile;
import map.Vector2d;
import map.scenario.Area;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.util.List;
import java.util.Random;

public class Intruder extends Agent {
    //Final variables
    private static final boolean DEBUG = false;

    private static int amtCaught = 0;

    private int exploredTilesCount;
    private Vector2d goalDirection;
    private Vector2d initialPos;
    private List<Tile> line;
    private LinkedList<Vertex> path = new LinkedList<>();
    private Vertex[][] graph = null;
    private boolean atGoal = false;
    private boolean caught = false;

    /**
     * @param position     the current/initial position
     * @param w            the head facing vector
     * @param theta        the vision angle
     * @param d            the vision distance
     * @param speed        the base speed
     * @param visionMatrix personal vision matrix
     */
    public Intruder(Tile position, Vector2d w, double theta, double d,
                    double smellDistance,
                    double speed,
                    VisionMatrix visionMatrix,
                    SmellMatrix smellMatrix,
                    KnowledgeMatrix knowledgeMatrix,
                    SoundMatrix soundMatrix) {
        super(position, w, theta, d, smellDistance, speed, visionMatrix, smellMatrix, knowledgeMatrix, soundMatrix);
        initialPos = position;
        //Direction from start position
        Area targetArea = GameRunner.runner.getScene().getTargetArea();
        if(targetArea == null){
            System.err.println("No target area!!!");
        }
        //middle of target area
        int x = ((Math.max(targetArea.getRightBoundary(),targetArea.getLeftBoundary())-Math.min(targetArea.getRightBoundary(),targetArea.getLeftBoundary())) / 2) + Math.min(targetArea.getRightBoundary(),targetArea.getLeftBoundary());
        int y = ((Math.max(targetArea.getTopBoundary(),targetArea.getBottomBoundary())-Math.min(targetArea.getTopBoundary(),targetArea.getBottomBoundary())) / 2) + Math.min(targetArea.getTopBoundary(),targetArea.getBottomBoundary());

        Vector2d target = new Vector2d(x,y);
        Vector2d agentTarget = target.minus(position);
        goalDirection = agentTarget;
        //goalDirection = agentTarget.getNorm();
        //Line of tiles
        line = visionMatrix.lineIgnoreWall(position,goalDirection);
    }

    @Override
    public void act() {
        // Do not move if caught
        if(!caught && !atGoal) {
            explore(position);
            if(visionMatrix.getMap().getTileAt((int)position.getX(),(int)position.getY()).getType().equals(Tile.Type.TARGET)){
                reachedGoal();
                return;
            }
            // do something
            long total = System.currentTimeMillis();
            for (int i = 0; i < speed; i++) {
                long time = System.currentTimeMillis();
                for (Tile tile : this.getTilesInFOV()) {
                    if(tile.getType().equals(Tile.Type.WALL) && !this.explored(tile)){
                        //if there is a new wall in the vision we need to recalculate the path
                        path.clear();
                        graph = null;
                    }
                    this.explore(tile);
                }
                if(DEBUG) System.out.println("FOV time needed: " + (System.currentTimeMillis() - time) + " ms");
                time = System.currentTimeMillis();
                if (!atGoal) {
                    AStar();
                }
                if(DEBUG) System.out.println("Astar time needed: " + (System.currentTimeMillis() - time) + " ms");
            }
            if(DEBUG){
                System.out.println("Total time needed: " + (System.currentTimeMillis() - total) + " ms");
                System.out.println();
            }
            produceSound();
        }
    }

    //When an intruder reaches the goal the game is won!
    private void reachedGoal() {
        atGoal = true;
        if(GameRunner.PAUSE_AFTER_END) GameRunner.runner.getController().pause();
        System.out.println("INTRUDERS WON!!!");
        GameRunner.winner = "Intruder";
        GameRunner.FINISHED = true;
        if(GameRunner.PRINT_INFORMATION_AFTER){
            System.out.println("After game information:");
            double progress = GameRunner.runner.getController().getGameState().getProgressGuards();
            System.out.println("Guards explored " + progress + "% of the map");
            System.out.println("Intruders caught: " + amtCaught);
        }
    }

    private void AStar(){
        //path from current position to goal
        if(graph == null){
            graph = visionMatrix.toGraph();
        }
        //if there is no next element stop moving
        if(line.isEmpty()){
            return;
        }
        // If we are on the line we go to next spot on the line
        if(position.getX() == line.get(0).getX() && position.getY() == line.get(0).getY()){
            if(visionMatrix.getMap().getTileAt((int)position.getX(),(int)position.getY()).getType().equals(Tile.Type.TARGET)){
                //Win!!! Agent is on the target
                reachedGoal();
            }
            else{
                line.remove(0);
                if(line.isEmpty()) return;
            }
        }
        Tile goal = line.get(0);
        //no need to recalculate path if there are still valid instructions left.
        if(path.isEmpty()){
            try {
                path = AStar.path(graph[(int)position.getY()][(int)position.getX()],graph[(int)goal.getY()][(int)goal.getX()]);
                path.remove(0);
            } catch (NoPathException e) {
                e.printStackTrace();
            }
        }

        //move by first element of the path (not the one we are on)
        try{
            super.move(path.remove(0).getTile());
        }
        catch(IndexOutOfBoundsException e){
        }
    }

    public boolean isAtGoal() {
        return atGoal;
    }

    public boolean isCaught() {
        return caught;
    }

    public void hasBeenCaught() {
        if(!caught){
            caught = true;
            amtCaught++;
            //remove vision cone as sign for being caught
            super.setD(0);
        }
    }

    @Override
    public void explore(Tile t) {
        if(!this.getVisionMatrix().isExplored(t)){
            this.getVisionMatrix().explore(t);
            exploredTilesCount++;
        }
    }

    public double getExploredRatio() {
        return (double) exploredTilesCount / (getVisionMatrix().getHeight()* getVisionMatrix().getWidth());
    }


    public static int getAmtCaught() {
        return amtCaught;
    }

    public static void resetAmtCaught() {
        Intruder.amtCaught = 0;
    }
}
