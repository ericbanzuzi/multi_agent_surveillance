package controller;

import agent.Agent;
import agent.Guard;
import agent.Intruder;
import agent.KnowledgeMatrix;
import agent.markers.PheromoneMarker;
import agent.markers.TeleportMarker;
import agent.sensation.SmellMatrix;
import agent.sensation.SoundMatrix;
import agent.sensation.VisionMatrix;
import map.Map;
import map.Tile;
import map.Vector2d;
import map.scenario.Area;
import map.scenario.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    private static final boolean DEBUG = false;

    private List<Intruder> intruders;
    private List<Guard> guards;

    private final Scenario scenario;
    private final Map map;

    private boolean finished;

    private TeleportMarker teleportMarker = new TeleportMarker();
    private int teleportMarkerTimeStepPassed;
    private PheromoneMarker pheromoneMarker = new PheromoneMarker();
    public List<Tile> marked = new ArrayList<Tile>();
    public ArrayList<List<Integer>> Yelltiles = new ArrayList<List<Integer>>();
    private SoundMatrix soundMatrix;

    public List<List<Tile>> sounds = new ArrayList<>();
    public List<List<Tile>> newSounds = new ArrayList<>();


    public GameState(Scenario scenario) {
        this.scenario = scenario;
        this.map = scenario.getMap();
        this.finished = false;
        this.intruders = new ArrayList<>();
        this.guards = new ArrayList<>();

        soundMatrix = new SoundMatrix(map, getIntruders(), getGuards());

        if (scenario.getNumIntruders() == 0) {
            initialiseGuards();
        } else {
            initialiseIntruders();
            initialiseGuards();
        }
    }

    public void next() {
        long time = System.currentTimeMillis();
        for (Intruder intruder : intruders) {
            intruder.act();
            pheromoneMarker.markLocation(intruder, intruders, guards);
        }
        if (DEBUG) System.out.println("Intruder time needed: " + (System.currentTimeMillis() - time) + " ms");
        time = System.currentTimeMillis();
        for (Guard guard : guards) {
            guard.act();
            pheromoneMarker.markLocation(guard, intruders, guards);
        }
        if (DEBUG) System.out.println("Guard time needed: " + (System.currentTimeMillis() - time) + " ms");
        marked = pheromoneMarker.markedGUI;
        soundMatrix.decay();
        Yelltiles = soundMatrix.yellTiles;
        pheromoneMarker.decay(intruders, guards);
        // teleport marker placement and decay
        if (isEnteredTeleport() != null) {
            Agent enteredTeleportAgent = isEnteredTeleport();
            teleportMarker.markLocation(enteredTeleportAgent, enteredTeleportAgent.getEnteredTeleport());
            teleportMarkerTimeStepPassed++;
        }
        if (teleportMarkerTimeStepPassed != 0) {
            teleportMarkerTimeStepPassed++;
            if (teleportMarker.decay(teleportMarkerTimeStepPassed)) {
                teleportMarkerTimeStepPassed = 0;
            }
        }
        updateSounds();
    }

    private Agent isEnteredTeleport() {
        for (int i = 0; i < intruders.size(); i++) {
            if (intruders.get(i).isEnteredTeleport()) {
                return intruders.get(i);
            }
        }
        for (int i = 0; i < guards.size(); i++) {
            if (guards.get(i).isEnteredTeleport()) {
                return guards.get(i);
            }
        }
        return null;
    }

    private void initialiseIntruders() {
        Area spawnArea = scenario.getSpawnAreaIntruders();
        double dx = spawnArea.getRightBoundary() - spawnArea.getLeftBoundary();
        double dy = spawnArea.getBottomBoundary() - spawnArea.getTopBoundary();
        int[] dirs = new int[]{-1, 0, 1};
        Random rand = new Random();

        for (int i = 0; i < scenario.getNumIntruders(); i++) {
            int x = (int) (spawnArea.getLeftBoundary() + (Math.random() * dx));
            int y = (int) (spawnArea.getTopBoundary() + (Math.random() * dy));
            while (map.getTileAt(x, y).getType().equals(Tile.Type.WALL)) {
                x = (int) (spawnArea.getLeftBoundary() + (Math.random() * dx));
                y = (int) (spawnArea.getTopBoundary() + (Math.random() * dy));
            }
            // generate random head facing
            int xDir = 0;
            int yDir = 0;
            while (xDir == 0 && yDir == 0) {
                xDir = dirs[rand.nextInt(dirs.length)];
                yDir = dirs[rand.nextInt(dirs.length)];
            }
            Intruder intruder = new Intruder(
                    map.getTileAt(x, y),
                    new Vector2d(xDir, yDir).scaleTo(1),
                    Math.PI / 2, // we might want a variable for this
                    scenario.getDistanceViewing(),
                    scenario.getSmellingDistance(),
                    scenario.getBaseSpeedIntruder(),
                    new VisionMatrix(map),
                    new SmellMatrix(map),
                    new KnowledgeMatrix(map, true, -1),
                    new SoundMatrix(map, intruders, guards)
            );
            intruders.add(intruder);
        }
    }

    private void initialiseGuards() {

        Area spawnArea = scenario.getSpawnAreaGuards();
        double dx = spawnArea.getRightBoundary() - spawnArea.getLeftBoundary();
        double dy = spawnArea.getBottomBoundary() - spawnArea.getTopBoundary();
        int[] dirs = new int[]{-1, 0, 1};
        Random rand = new Random();

        for (int i = 0; i < scenario.getNumGuards(); i++) {
            int x = (int) (spawnArea.getLeftBoundary() + Math.random() * dx);
            int y = (int) (spawnArea.getTopBoundary() + Math.random() * dy);
            // generate random head facing
            int xDir = 0;
            int yDir = 0;
            while (xDir == 0 && yDir == 0) {
                xDir = dirs[rand.nextInt(dirs.length)];
                yDir = dirs[rand.nextInt(dirs.length)];
            }
            Guard guard = new Guard(
                    map.getTileAt(x, y),
                    new Vector2d(xDir, yDir).scaleTo(1),
                    Math.PI / 2, // we might want a variable for this
                    scenario.getDistanceViewing(),
                    scenario.getSmellingDistance(),
                    scenario.getBaseSpeedGuard(),
                    new VisionMatrix(map),
                    new SmellMatrix(map),
                    new KnowledgeMatrix(map, true, i),
                    new SoundMatrix(map, intruders, guards)
            );
            guards.add(guard);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public List<Intruder> getIntruders() {
        return intruders;
    }

    public List<Guard> getGuards() {
        return guards;
    }

    public double getProgressGuards() {
        double progress = 0;
        VisionMatrix mat = new VisionMatrix(map);
        guards.forEach(g -> {
            for (int i = 0; i < map.getWidth(); i++) {
                for (int j = 0; j < map.getHeight(); j++) {
                    Tile t = map.getTileAt(i, j);
                    if (g.getVisionMatrix().isExplored(t)) {
                        mat.explore(t);
                    }
                }
            }
        });
        progress = mat.getExploredRatio();
        //Convert to percent:
        progress *= 100;
        return progress;
    }

    public int getIntruderIndex(Intruder intruder) {
        return intruders.indexOf(intruder);
    }

    public int getGuardIndex(Guard guard) {
        return guards.indexOf(guard);
    }

    public void updateSounds() {
        this.sounds = new ArrayList<>(newSounds);
        newSounds = new ArrayList<>();
    }

    public List<List<Tile>> getSounds() {
        return this.sounds;
    }

    public void produceSound(Agent a) {
        List<Tile> sound = a.getSoundFomMoving();
        newSounds.add(sound);
    }

    public SoundMatrix getSoundMatrix() {
        return soundMatrix;
    }
}
