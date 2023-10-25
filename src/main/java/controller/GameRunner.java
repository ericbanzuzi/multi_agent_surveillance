package controller;
import agent.Intruder;
import agent.explorer.AStar;
import map.scenario.Scenario;


public class GameRunner {

    public static boolean PRINT_INFORMATION_AFTER = true;
    public static boolean PAUSE_AFTER_END = true;
    public static int simulationDelay = 100;

    public static boolean FINISHED = false;
    public static String winner = "";

    private String mapDoc;
    protected Scenario scenario;

    private GameController controller;

    public static GameRunner runner;
    // exploration
    public static String path1="src/main/assets/experimentsExplore.txt";
    public static String path2="src/main/assets/testmap.txt";
    public static String path3="src/main/assets/examinermap_small.txt";
    // intruder vs guards
    public static String path4="src/main/assets/examinermap_phase1.txt";
    public static String path5="src/main/assets/experimentsVS.txt";
    public static String path6="src/main/assets/experimentsVS.txt";

    public GameRunner(String scn){
        runner = this;
        mapDoc=scn;
        scenario = new Scenario(mapDoc);
        controller = new GameController(scenario);
        scenario.setController(controller);
        reset();
    }

    public GameRunner(Scenario s){
        runner = this;
        mapDoc= s.getMapDoc();
        scenario = s;
        controller = new GameController(scenario);
        s.setController(controller);
        scenario.setController(controller);
        reset();
    }

    public String getMapDoc(){
        return mapDoc;
    }

    public GameController getController(){
        return controller;
    }

    public Scenario getScene() {
        return scenario;
    }

    public void reset(){
        FINISHED = false;
        winner = "";
        Intruder.resetAmtCaught();
        AStar.resetNr();

    }
}
