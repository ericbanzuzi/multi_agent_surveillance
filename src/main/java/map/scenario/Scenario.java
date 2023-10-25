package map.scenario;

import controller.GameController;
import map.Map;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Scenario {

    protected double baseSpeedIntruder;
    protected double sprintSpeedIntruder;
    protected double baseSpeedGuard;
    protected int numberMarkers;

    protected String mapDoc;
    protected int gameMode;
    private Path filePath;
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    protected String name;
    protected String gameFile;
    protected int mapHeight;
    protected int mapWidth;
    protected double scaling;
    protected int numIntruders;

    protected double distanceViewing;
    protected double smellingDistance;

    protected int numGuards;
    protected Area spawnAreaIntruders;
    protected Area spawnAreaGuards;
    protected Area targetArea;
    protected ArrayList<Wall> walls;
    protected ArrayList<TelePortal> teleports;
    protected ArrayList<Shaded> shaded;
    private Map map;
    protected GameController gameController;

    public Scenario(String mapFile) {
        mapDoc = mapFile;

        walls = new ArrayList<>();
        shaded = new ArrayList<>();
        teleports = new ArrayList<>();

        // read scenario
        filePath = Paths.get(mapDoc);
        System.out.println(filePath);
        readMap();
        walls.addAll(createBoundaryWalls());
        this.map = new Map(this);
    }

    public void readMap(){
        try (Scanner scanner =  new Scanner(filePath, ENCODING.name())){
            while (scanner.hasNextLine()){
                parseLine(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void parseLine(String line) {
        //use a second Scanner to parse the content of each line
        try (Scanner scanner = new Scanner(line)) {
            scanner.useDelimiter("=");
            if (scanner.hasNext()) {
                // read id value pair
                String id = scanner.next();
                String value = scanner.next();
                // trim excess spaces
                value = value.trim();
                id = id.trim();
                // in case multiple parameters
                String[] items = value.split(" ");
                // only one value
                value = value.split(" ")[0];

                switch (id) {
                    case "name":
                        name = value;
                        break;
                    case "gameFile":
                        gameFile = value;
                        break;
                    case "gameMode":
                        gameMode = Integer.parseInt(value); // 0 is exploration, 1 evasion pursuit game
                        break;
                    case "scaling":
                        scaling = Double.parseDouble(value);
                        break;
                    case "height":
                        mapHeight = Integer.parseInt(value);
                        break;
                    case "width":
                        mapWidth = Integer.parseInt(value);
                        break;
                    case "numGuards":
                        numGuards = Integer.parseInt(value);
                        break;
                    case "numIntruders":
                        numIntruders = Integer.parseInt(value);
                        break;
                    case "baseSpeedIntruder":
                        baseSpeedIntruder = Double.parseDouble(value);
                        break;
                    case "sprintSpeedIntruder":
                        sprintSpeedIntruder = Double.parseDouble(value);
                        break;
                    case "baseSpeedGuard":
                        baseSpeedGuard = Double.parseDouble(value);
                        break;
                    case "numberMarkers":
                        numberMarkers = Integer.parseInt(value);
                        break;
                    case "distanceViewing":
                        distanceViewing = Double.parseDouble(value);
                        break;
                    case "smellingDistance":
                        smellingDistance = Double.parseDouble(value);
                        break;
                    case "targetArea":
                        targetArea = new Area(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                        break;
                    case "spawnAreaIntruders":
                        spawnAreaIntruders = new Area(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                        break;
                    case "spawnAreaGuards":
                        spawnAreaGuards = new Area(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                        break;
                    case "wall":
                        Wall walltmp = new Wall(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                        walls.add(walltmp);
                        break;
                    case "shaded":
                        Shaded shadedtmp = new Shaded(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                        shaded.add(shadedtmp);
                        break;
                    case "teleport":
                        TelePortal teletmp = new TelePortal(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), Integer.parseInt(items[4]), Integer.parseInt(items[5]), Double.parseDouble(items[6]));
                        teleports.add(teletmp);
                        break;
                    case "texture":
                        // still to do. First the coordinates, then an int with texture type and then a double with orientation
                }
            }
        }
    }

    public double getBaseSpeedIntruder() {
        return baseSpeedIntruder;
    }

    public double getSprintSpeedIntruder() {
        return sprintSpeedIntruder;
    }

    public double getBaseSpeedGuard() {
        return baseSpeedGuard;
    }

    public int getNumberMarkers() {
        return numberMarkers;
    }

    public String getMapDoc() {
        return mapDoc;
    }

    public int getGameMode() {
        return gameMode;
    }

    public String getName() {
        return name;
    }

    public String getGameFile() {
        return gameFile;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public double getScaling() {
        return scaling;
    }

    public int getNumIntruders() {
        return numIntruders;
    }

    public double getDistanceViewing() {
        return distanceViewing;
    }

    public double getSmellingDistance() {
        return smellingDistance;
    }

    public int getNumGuards() {
        return numGuards;
    }

    public Area getSpawnAreaIntruders() {
        return spawnAreaIntruders;
    }

    public Area getSpawnAreaGuards() {
        return spawnAreaGuards;
    }

    public Area getTargetArea() {
        return targetArea;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public ArrayList<TelePortal> getTeleports() {
        return teleports;
    }

    public ArrayList<Shaded> getShaded() {
        return shaded;
    }

    public ArrayList<TelePortal> getTeleportals() {
        return teleports;
    }

    public Map getMap() {
        return map;
    }

    private ArrayList<Wall> createBoundaryWalls() {
        Wall leftWall = new Wall(0, 0, 1, mapHeight);
        Wall rightWall = new Wall(mapWidth-1, 0, mapWidth, mapHeight);
        Wall topWall = new Wall(0, 0, mapWidth, 1);
        Wall bottomWall = new Wall(0, mapHeight-1, mapWidth, mapHeight);
        return new ArrayList<Wall>(Arrays.asList(leftWall, rightWall, topWall, bottomWall));
    }

    public void setController(GameController controller) {
        this.gameController = controller;
    }

    public GameController getGameController() {
        return gameController;
    }
}
