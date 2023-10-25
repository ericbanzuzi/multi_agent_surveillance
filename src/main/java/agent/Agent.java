package agent;

import agent.sensation.SmellMatrix;
import agent.sensation.SoundMatrix;
import agent.sensation.VisionMatrix;
import controller.GameRunner;
import map.Tile;
import map.Vector2d;

import java.util.ArrayList;
import java.util.List;

public abstract class Agent {

    protected Tile position;
    private Vector2d w;
    private double theta;
    private double d;
    public double NonShadedD;
    protected double smellDistance;
    protected double speed;
    protected final VisionMatrix visionMatrix;
    protected final SmellMatrix smellMatrix;
    protected final SoundMatrix soundMatrix;
    private boolean shaded = false;
    protected boolean running = false;
    protected final int WALK_SOUND = 5; // sound from walking
    protected final int RUN_SOUND = 6;
    protected final int HEAR_DISTANCE = 5; // distance of hearing
    protected List<Tile> producedSound;
    protected List<Tile> pheromoneMarkers = new ArrayList<>();
    private boolean isEnteredTeleport;
    private Tile enteredTeleport;

    public KnowledgeMatrix getKnowledgeMatrix() {
        return knowledgeMatrix;
    }

    protected final KnowledgeMatrix knowledgeMatrix;

    /**
     * @param position        the current/initial position
     * @param w               the head facing vector
     * @param theta           the vision angle
     * @param d               the vision distance
     * @param speed           the base/initial speed
     * @param visionMatrix    personal vision matrix
     * @param smellMatrix     personal smell matrix
     * @param knowledgeMatrix personal knowledge matrix
     * @param
     */
    public Agent
    (Tile position, Vector2d w, double theta, double d,
     double smellDistance,
     double speed,
     VisionMatrix visionMatrix,
     SmellMatrix smellMatrix,
     KnowledgeMatrix knowledgeMatrix,
     SoundMatrix soundMatrix) {
        this.position = position;
        this.w = w;
        this.theta = theta;
        this.d = d;
        NonShadedD = d;
        this.smellDistance = smellDistance;
        this.speed = speed;
        this.visionMatrix = visionMatrix;
        this.smellMatrix = smellMatrix;
        this.knowledgeMatrix = knowledgeMatrix;
        this.soundMatrix = soundMatrix;
    }

    public abstract void act();

    public void explore(Tile t) {
        if (!visionMatrix.isExplored(t)) {
            visionMatrix.explore(t);
        }
    }

    public boolean explored(Tile t) {
        return visionMatrix.isExplored(t);
    }

    /**
     * @return a list of tiles that are in the agent's field of view
     */
    public List<Tile> getTilesInFOV() {
        if (visionMatrix.getMap().getTileAt((int) position.getX(), (int) position.getY()).getType().equals(Tile.Type.SHADED) && !shaded) {
            d *= 0.75;
            shaded = true;
        } else if (!visionMatrix.getMap().getTileAt((int) position.getX(), (int) position.getY()).getType().equals(Tile.Type.SHADED) && shaded) {
            d = NonShadedD;
            shaded = false;
        }
        return visionMatrix.getTilesInFOV(this.position, this.w, this.theta, this.d);
    }

    public List<Tile> getTilesInSmell() {
        return smellMatrix.getTilesInSmell(this.position, this.w, this.smellDistance);
    }

    public Tile getEnteredTeleport() {
        return enteredTeleport;
    }

    public boolean move(Tile newPos) {
        isEnteredTeleport = false;
        if (!this.visionMatrix.getTypeOf(newPos).equals(Tile.Type.WALL) ||
                this.visionMatrix.getMap().getTileAt((int) newPos.getX(), (int) newPos.getY()).getType().equals(Tile.Type.WALL)) {
            if (this.visionMatrix.getTypeOf(newPos).equals(Tile.Type.TELEPORT)) {
                try {
                    Tile newTile = visionMatrix.teleport(newPos);
                    // set the marker and enter the teleport
                    this.setPosition(newPos);
                    isEnteredTeleport = true;
                    enteredTeleport = this.getPosition();
//                    new TeleportMarker().markLocation( this);

                    this.setPosition(newTile);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                this.setW((newPos.minus(this.position)).scaleTo(1));
                this.setPosition(newPos);
                return true;
            }
        }
        return false;
    }

    public Tile getPosition() {
        return position;
    }

    public void setPosition(Tile position) {
        this.position = position;
    }

    public Tile getTileAt(int x, int y) {
        return visionMatrix.getMap().getTileAt(x, y);
    }

    public Vector2d getW() {
        return w;
    }

    public void setW(Vector2d w) {
        this.w = w;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public VisionMatrix getVisionMatrix() {
        return visionMatrix;
    }

    public boolean isEnteredTeleport() {
        return isEnteredTeleport;
    }

    public List<Tile> getTilesInHearing() {
        return this.soundMatrix.getTilesInSound(this.position, this.w, HEAR_DISTANCE);
    }

    public boolean isInHearing(Tile t) {
        List<Tile> hearing = getTilesInHearing();
        return hearing.contains(t);
    }

    public List<Tile> getSoundTilesInHearing() {
        List<Tile> hearing = getTilesInHearing();
        List<List<Tile>> sounds = new ArrayList<>(GameRunner.runner.getController().getGameState().getSounds());
        sounds.remove(this.producedSound); // delete own sounds
        List<Tile> result = new ArrayList<>();
        for (List<Tile> soundArea : sounds) {
            for (Tile t : soundArea) {
                if (hearing.contains(t) && !t.getType().equals(Tile.Type.WALL)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    public List<Tile> getSmellTilesInSmell() {
        List<Tile> smells = GameRunner.runner.getController().getGameState().marked;
        List<Tile> smellArea = getTilesInSmell();
        List<Tile> result = new ArrayList<>();
        for (Tile t : smells) {
            if (smellArea.contains(t) && !pheromoneMarkers.contains(t) && !t.getType().equals(Tile.Type.WALL)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Tile> getSoundFomMoving() {
        if (running) {
            this.producedSound = this.soundMatrix.getTilesInSound(this.position, this.w, RUN_SOUND);
            return producedSound;
        }
        this.producedSound = this.soundMatrix.getTilesInSound(this.position, this.w, WALK_SOUND);
        return this.producedSound;
    }

    public void produceSound() {
        GameRunner.runner.getController().getGameState().produceSound(this);
    }

    public void addPheromoneMarker(Tile t) {
        this.pheromoneMarkers.add(t);
    }

    public void removePheromoneMarker(Tile t) {
        this.pheromoneMarkers.remove(t);
    }

    public List<Tile> getPheromoneMarkers() {
        return pheromoneMarkers;
    }

    public List<Tile> getProducedSound() {
        return producedSound;
    }
}
