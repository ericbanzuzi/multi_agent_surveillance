package ui;

import controller.GameRunner;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import map.Map;
import map.scenario.Scenario;

import java.io.IOException;


public class MenuControllerWindow {

    protected static final int SCALE = 6;

    private final static String path1 = GameRunner.path1;
    private final static String path2 = GameRunner.path2;
    private final static String path3 = GameRunner.path3;
    // intruder vs guards
    private final static String path4 = GameRunner.path4;
    private final static String path5 = GameRunner.path5;
    private final static String path6 = GameRunner.path6;

    public static GameRunner gameRunner;
    
    @FXML
    private Button button1 = new Button("Exploration");

    @FXML
    private Button button2 = new Button("Guards vs Intruders");

    @FXML
    private RadioButton f1 = new RadioButton("Map 1");

    @FXML
    private RadioButton f2 = new RadioButton("Map 2");

    @FXML
    private RadioButton f3 = new RadioButton("Map 3");

    // exploration
    @FXML
    void button1Handle(MouseEvent event) throws IOException {
//        Scenario scene = GameRunner.runner.getScene();
        String mapDoc;
        if(f1.isSelected()){
            mapDoc = path1;
        } else if(f2.isSelected()){
            mapDoc = path2;
        } else{
            mapDoc = path3;
        }
        gameRunner = new GameRunner(mapDoc);
        Scenario scene = GameRunner.runner.getScene();
        loadMap(scene);
    }

    // intruder vs guards
    @FXML
    void button2Handle(MouseEvent event) throws IOException {
        String mapDoc;
        if(f1.isSelected()){
            mapDoc = path4;
        } else if(f2.isSelected()){
            mapDoc = path5;
        } else{
            mapDoc = path6;
        }
        gameRunner = new GameRunner(mapDoc);
        Scenario scene = GameRunner.runner.getScene();
        loadMap(scene);

    }

    /**
     * Loads the map in to a GUI and displays it
     * @param sc a scenario object with all information
     */
    private void loadMap(Scenario sc) {
        Map map = sc.getMap();
        Stage stage = new Stage();
        stage.setMinWidth(map.getWidth()*SCALE);
        stage.setMinHeight(map.getHeight()*SCALE);
        stage.initModality(Modality.APPLICATION_MODAL);

        MapWindow mapWindow = new MapWindow(sc);
        Scene scene = new Scene(mapWindow);
        stage.setScene(scene);
        stage.setTitle("Multi-agent Surveillance");
        mapWindow.showSimulation(); // starts running simulation on game controller
        stage.show();
    }
    
}
