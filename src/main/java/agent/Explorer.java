package agent;

import agent.sensation.SmellMatrix;
import agent.sensation.SoundMatrix;
import agent.sensation.VisionMatrix;
import map.Tile;
import map.Vector2d;

import java.util.List;
import java.util.Random;

public class Explorer extends Agent{

    private final String EXPLORATION_MODE;
    private int exploredTilesCount;
    private KnowledgeMatrix knowledgeMatrix;

    /**
     * @param position     the current/initial position
     * @param w            the head facing vector
     * @param theta        the vision angle
     * @param d            the vision distance
     * @param speed        the base/initial speed
     * @param visionMatrix personal vision matrix
     * @param smellMatrix  personal smell matrix
     */
    public Explorer(Tile position, Vector2d w, double theta, double d,
                    double smellDistance,
                    double speed,
                    VisionMatrix visionMatrix,
                    SmellMatrix smellMatrix,
                    KnowledgeMatrix knowledgeMatrix,
                    SoundMatrix soundMatrix,
                    String explorationMode) {
        super(position, w, theta, d, smellDistance, speed, visionMatrix, smellMatrix, knowledgeMatrix, soundMatrix);
            this.EXPLORATION_MODE = explorationMode;
            this.exploredTilesCount = 0;
    }

    @Override
    public void act() {
        for(Tile tile : this.getTilesInFOV()) {
            this.explore(tile);
        }
        switch(EXPLORATION_MODE){
            case "random":
                randomExploration();
                break;
            case "dfs":
                break;
            case "hlu":
                hluExploration();
                break;
        }
    }

    private void hluExploration() {

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

}
