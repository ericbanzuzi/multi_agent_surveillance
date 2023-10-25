package agent.markers;

import agent.Agent;
import agent.Guard;
import agent.Intruder;
import controller.GameState;
import map.Tile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PheromoneMarker {

    private int[][] tiles = new int[30][2];
    private int decayTime;
    public List<Tile> markedGUI = new ArrayList<Tile>();
    private int size;


    public void markLocation(Agent agent, List<Intruder> intruders, List<Guard> guards ) {
        size = intruders.size() + guards.size();
        decayTime = size * 20;
        int x = (int) agent.getPosition().getX();
        int y = (int) agent.getPosition().getY();
        for (int j = 0; j < intruders.size(); j++) {
            if (intruders.get(j) != agent) {
                addMarker(intruders.get(j), 1, x, y);
            }
        }
        for (int i = 0; i < guards.size(); i++) {
            if (guards.get(i) != agent) {
                addMarker(guards.get(i), 2, x, y);
            }
        }

        Tile tile = agent.getTileAt(x, y);
        agent.addPheromoneMarker(tile);
        markedGUI.add(tile);
    }

    private void addMarker(Agent toMark, int type, int x, int y){
        Tile tile = toMark.getVisionMatrix().getMap().getTileAt(x , y);
        if (type == 1){
            tile.setMarker(Tile.Marker.PHEROMONEINTRUDER);
        } else {
            tile.setMarker(Tile.Marker.PHEROMONEGUARD);
        }
    }

    public void decay(List<Intruder> intruders, List<Guard> guards ){
        if(markedGUI.size() >= decayTime){
            for(int i = 0; i < size; i++){
                int x = (int) markedGUI.get(i).getX();
                int y = (int) markedGUI.get(i).getY();
                for (int j = 0; j < intruders.size(); j++) {
                    intruders.get(j).getTileAt(x,y).setMarker(Tile.Marker.DEFAULT);
                }
                for (int l = 0; l < guards.size(); l++) {
                    guards.get(l).getTileAt(x,y).setMarker(Tile.Marker.DEFAULT);
                }
                markedGUI.remove(i);
            }

        }
        //agent.getVisionMatrix().getMap().setMarked(markedGUI);
    }

}
