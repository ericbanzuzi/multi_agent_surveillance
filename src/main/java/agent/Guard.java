package agent;

import Exceptions.NoPathException;
import agent.explorer.AStar;
import agent.explorer.Lee;
import agent.explorer.Vertex;
import agent.explorer.BaMMatrix;
import agent.explorer.BrickAndMortar;
import agent.sensation.SmellMatrix;
import agent.sensation.SoundMatrix;
import agent.sensation.VisionMatrix;
import controller.GameRunner;
import map.Tile;
import map.Vector2d;
import map.scenario.Area;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Guard extends Agent{

    private int exploredTilesCount;
    private int lastDirection;
    private boolean avoidOtherGuard;
    private List<Tile> heardSounds;
    private List<Tile> smelled;
    private Vertex[][] graph = null;
    private LinkedList<Vertex> path = new LinkedList<>();
    private boolean spawned = true;
    private Tile target;
    private int count;
    private Lee lee;

    private BaMMatrix BaM;

    /**
     * @param position     the current/initial position
     * @param w            the head facing vector
     * @param theta        the vision angle
     * @param d            the vision distance
     * @param speed        the base speed
     * @param visionMatrix personal vision matrix
     */
    public Guard(Tile position, Vector2d w, double theta, double d,
                 double smellDistance,
                 double speed,
                 VisionMatrix visionMatrix,
                 SmellMatrix smellMatrix,
                 KnowledgeMatrix knowledgeMatrix,
                 SoundMatrix soundMatrix) {
        super(position, w, theta, d, smellDistance, speed, visionMatrix, smellMatrix, knowledgeMatrix, soundMatrix);
        this.BaM = new BaMMatrix(visionMatrix,this);
        lee = new Lee(visionMatrix, this);
    }

    @Override
    public void act() {
        boolean investigate = somethingToInvestigate();
        for(int i = 0; i < speed; i++){
            for(Tile tile : this.getTilesInFOV()) {
                if(tile.getType().equals(Tile.Type.WALL) && !this.explored(tile)){
                    //if there is a new wall in the vision we need to recalculate the path
                    path.clear();
                    graph = null;
                }
                this.explore(tile);
            }
            if((investigate || !path.isEmpty())){
                investigationExploration();
//                System.out.println("I'M INVESTIGATING!");
            } else {
                //randomExploration();
                hluExploration();
                //BrickAndMortarExploration();
                //lee.step();
            }
        }

        // do something
        produceSound();
        reachedTarget();
        count++;
//        System.out.println("MOVE DONE!");
    }

    public void reachedTarget(){
        if(target != null && this.position.getX() == target.getX() && this.position.getY() == target.getY()){
            target = null;
            path.clear();
        }
    }

    private void randomExploration() {
        Random rand = new Random();
        List<Tile> neighbors = this.visionMatrix.getNeighborsOf(this.position);
        boolean moved = false;
        while (!moved) {
            int randomIndex = rand.nextInt(neighbors.size());
            moved = move(neighbors.get(randomIndex));
        }
    }

    private void hluExploration(){
        Vector2d newPosVector = knowledgeMatrix.decision(position, avoidOtherGuard);
        avoidOtherGuard = false;
        Tile newPos = visionMatrix.getTileFromVector(newPosVector);
        explore(newPos);
        visionMatrix.explore(newPos);
        List<Tile> visionTiles = visionMatrix.getTilesInFOV(newPos, this.getW(), this.getTheta(), this.getD());
        for(Tile t : visionTiles){
            visionMatrix.explore(t);
        }
        move(newPos);
    }

    public void investigationExploration(){
        Random rand = new Random();
        if(!path.isEmpty()){
            AStar(null);
        }
        else if(smelled.size() != 0){ // guards know the smell of guards
            if(target == null){
                int id = rand.nextInt(smelled.size());
                target = smelled.get(id);
                smelled.remove(id);
            }
            AStar(target);
//            System.out.println("SMELL");
        }
        else if(heardSounds.size() != 0) { // an unknown sound
            if(target == null) {
                int id = rand.nextInt(heardSounds.size());
                target = heardSounds.get(id);
                heardSounds.remove(id);
            }
            AStar(target);
//            System.out.println("SOUND");
        }
        else {
            hluExploration();
//            System.out.println("INVESTI - HLU");
        }
    }

    private void AStar(Tile target){
        //path from current position to goal
        if(graph == null){
            graph = visionMatrix.toGraph();
        }

        Tile goal = target;
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
            Tile newPos = path.remove(0).getTile();
            avoidOtherGuard = false;
            explore(newPos);
            visionMatrix.explore(newPos);
            boolean valid = move(newPos);
            if(!valid){
                path.clear();
            }
            else {
                List<Tile> visionTiles = visionMatrix.getTilesInFOV(newPos, this.getW(), this.getTheta(), this.getD());
                for(Tile t : visionTiles){
                    visionMatrix.explore(t);
                    // already explored
                    heardSounds.removeIf(t2 -> t.getX() == t2.getX() && t.getY() == t2.getY());
                    smelled.removeIf(t2 -> t.getX() == t2.getX() && t.getY() == t2.getY());
                }
            }
//            System.out.println(newPos.toString());
        }
        catch(IndexOutOfBoundsException e){
        }
    }

    private void BrickAndMortarExploration(){
        Tile newPos = BrickAndMortar.explore(BaM, position);
        visionMatrix.explore(newPos);
        List<Tile> visionTiles = visionMatrix.getTilesInFOV(newPos, this.getW(), this.getTheta(), this.getD());
        for(Tile t : visionTiles){
            visionMatrix.explore(t);
        }
        move(newPos);
    }

    @Override
    public void explore(Tile t) {
        SoundMatrix soundMatrix = GameRunner.runner.getController().getGameState().getSoundMatrix();
        soundMatrix.guardYell(this.getPosition(), this.getW(), this);
        for(Intruder i:GameRunner.runner.getController().getGameState().getIntruders()){
            if(i.getPosition().equals(t)){
                soundMatrix.intruderYell(i.getPosition(), i.getW(), i);
                caughtIntruder(i);
            }
        }
        for(Guard g: GameRunner.runner.getController().getGameState().getGuards()){
            if(! this.equals(g) && g.getPosition().equals(t)) {
                avoidOtherGuard = true;
                break;
            }
        }
        if(!this.getVisionMatrix().isExplored(t)){
            this.getVisionMatrix().explore(t);
            exploredTilesCount++;
        }
        this.getKnowledgeMatrix().explore(t);
    }

    public double getExploredRatio() {
        return (double) exploredTilesCount / (getVisionMatrix().getHeight()* getVisionMatrix().getWidth());
    }

    public void caughtIntruder(Intruder i){
        i.hasBeenCaught();
        //if all intruders have been caught guards win!!!
        if(Intruder.getAmtCaught() == GameRunner.runner.getScene().getNumIntruders()){
            if(GameRunner.PAUSE_AFTER_END) GameRunner.runner.getController().pause();
            System.out.println("GUARDS WON!!!");
            GameRunner.winner = "Guards";
            GameRunner.FINISHED = true;
            if(GameRunner.PRINT_INFORMATION_AFTER){
                System.out.println("After game information:");
                double progress = GameRunner.runner.getController().getGameState().getProgressGuards();
                System.out.println("Guards explored " + progress + "% of the map");
                System.out.println("Intruders caught: " + Intruder.getAmtCaught());
                AtomicReference<Double> distance = new AtomicReference<Double>();
                distance.set((double)Integer.MAX_VALUE);
                GameRunner.runner.getController().getGameState().getIntruders().forEach(intr ->{
                    Tile pos = intr.getPosition();
                    Area tarArea = GameRunner.runner.getScene().getTargetArea();
                    Vector2d target = new Vector2d(tarArea.getBottomBoundary(),tarArea.getLeftBoundary());
                    double dist = target.getDistanceTo(pos);
                    distance.set(Math.min(distance.get(),dist));
                });
                System.out.println("Closest intruder to target: " + distance.get());
            }
        }
    }

    public List<Tile> getSmellTilesInSmell(){
        List<Tile> smells = GameRunner.runner.getController().getGameState().marked;
        List<Tile> smellArea = getTilesInSmell();
        List<Tile> result = new ArrayList<>();
        for(Tile t : smells){
            if(smellArea.contains(t) && !pheromoneMarkers.contains(t) && !t.getType().equals(Tile.Type.WALL)){
                boolean guardSmell = false;
                for(Guard g : GameRunner.runner.getController().getGameState().getGuards()) {
                    if(g.getPheromoneMarkers().contains(t)) {
                        guardSmell = true;
                    }
                }
                if(!guardSmell) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    public List<Tile> getSoundTilesInHearing(){
        List<Tile> hearing = getTilesInHearing();
        List<List<Tile>> sounds = new ArrayList<>(GameRunner.runner.getController().getGameState().getSounds());
        sounds.remove(this.producedSound); // delete own sounds
        for(Guard g : GameRunner.runner.getController().getGameState().getGuards()){
            sounds.remove(g.getProducedSound());
        }
        List<Tile> result = new ArrayList<>();
        for(List<Tile> soundArea : sounds){
            for(Tile t : soundArea){
                if(hearing.contains(t) && !t.getType().equals(Tile.Type.WALL)){
                    result.add(t);
                }
            }
        }
        return result;
    }

    public boolean somethingToInvestigate(){
        // just spawned (ignore first 10 moves) or there does not exist intruders
        if(spawned || GameRunner.runner.getController().getGameState().getIntruders().size() == 0 || count < 10) {
            spawned = false;
            return false;
        }
        List<Tile> sounds = getSoundTilesInHearing();
        List<Tile> smells = getSmellTilesInSmell();
        List<Tile> fov = getTilesInFOV();
        for(Tile t : fov){
            // remove already "investigated" tiles
            smells.removeIf(t2 -> (t.getX() == t2.getX() && t.getY() == t2.getY()) ||
                    (this.position.getX() == t2.getX() && this.position.getY() == t2.getY()));
            sounds.removeIf(t2 -> (t.getX() == t2.getX() && t.getY() == t2.getY()) ||
                    (this.position.getX() == t2.getX() && this.position.getY() == t2.getY()));
        }
        boolean  result = sounds.size() != 0 || smells.size() != 0;
        if(result){
            this.heardSounds = new ArrayList<>(sounds);
            this.smelled = new ArrayList<>(smells);
        }
        return result;
    }

    public boolean guardInVision(){
        List<Tile> visionTiles = visionMatrix.getTilesInFOV(this.position, this.getW(), this.getTheta(), this.getD());
        for(Tile t : visionTiles){
            for(Guard g: GameRunner.runner.getController().getGameState().getGuards()){
                if(!this.equals(g) && g.getPosition().equals(visionMatrix.getMap().getTileAt((int)t.getX(),(int)t.getY()))) {
                    System.out.println("true");
                    return true;
                }
            }
        }
        return false;
    }

    // checks that a guard is not in the 8 neighbors x4 of the current position tile
    public boolean guardInRange(){
        List<Tile> range = this.smellMatrix.getTilesInSmell(position, this.getW(), 4);
        for(Tile t : range){
            for(Guard g: GameRunner.runner.getController().getGameState().getGuards()){
                if(!this.equals(g) && g.getPosition().equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }


}
