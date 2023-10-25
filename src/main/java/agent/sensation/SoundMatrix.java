package agent.sensation;

import agent.Agent;
import agent.Guard;
import agent.Intruder;
import map.Map;
import map.Tile;
import map.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class SoundMatrix extends VisionMatrix {

    public List<Tile> guardSoundMatrix = new ArrayList<>();
    public List<Tile> intruderSoundMatrix = new ArrayList<>();
    private Tile orgTile;
    private int soundDistance = 20;
    public List<Intruder> intrudersHear = new ArrayList<>();
    public List<Guard> guardsHear = new ArrayList<>();
    private final Map map;
    public List<Tile> markedNew = new ArrayList<>();
    private List<Intruder> intruders;
    private List<Guard> guards;
    public ArrayList<List<Tile>> marked = new ArrayList<List<Tile>>();
    public ArrayList<List<Integer>> yellTiles = new ArrayList<List<Integer>>();
    public List<Integer> decayTime = new ArrayList<Integer>();
    private int time = 0;
    public SoundMatrix(Map map, List<Intruder> intruders, List<Guard> guards) {
        super(map);
        this.map = map;
        this.guards = guards;
        this.intruders = intruders;
    }

    //Create listen area
    public List<Tile> getTilesInSound(Tile position, Vector2d w, double hearDistance) {
        return this.getTilesInFOV(position, w, Math.PI * 2, hearDistance);
    }

    //Create sound
    public void guardYell(Tile position, Vector2d w, Guard guard) {
        if(checkIntruders(guard) && time == 5) {
            orgTile = position;
            distance(guard);
            createMarkers();
        } else {
            time++;
        }
    }

    public void intruderYell(Tile position, Vector2d w, Agent agent) {
        orgTile = position;
        distance(agent);
        createMarkers();
    }

    private boolean checkIntruders(Guard guard){
        List<Tile> temp = getTilesInFOV(guard.getPosition(),guard.getW(),guard.getTheta(),guard.getD());
        for(int i = 0; i < intruders.size(); i++){
            int x1 = (int) intruders.get(i).getPosition().getX();
            int y1 = (int) intruders.get(i).getPosition().getY();
            for (int j = 0; j < temp.size(); j++) {
                int x2 = (int) temp.get(j).getX();
                int y2 = (int) temp.get(j).getY();
                if(x1 == x2 && y1 == y2){
                    return true;
                }
            }
        }
        return false;
    }

    private void createMarkers() {
        markedNew.clear();
        for (int i = 0; i < intrudersHear.size(); i++) {
            List<Tile> temp = line(orgTile, intrudersHear.get(i).getPosition());
            for (int j = 0; j < temp.size(); j++) {
                markedNew.add(temp.get(j));
                int x = (int) temp.get(j).getX();
                int y = (int) temp.get(j).getY();
                intrudersHear.get(i).getTileAt(x, y).setMarker(Tile.Marker.YELLINTRUDER);
            }
        }
        for (int i = 0; i < guardsHear.size(); i++) {
            List<Tile> temp = line(orgTile, guardsHear.get(i).getPosition());
            for (int j = 0; j < temp.size(); j++) {
                markedNew.add(temp.get(j));
                int x = (int) temp.get(j).getX();
                int y = (int) temp.get(j).getY();
                guardsHear.get(i).getTileAt(x, y).setMarker(Tile.Marker.YELLGUARD);
            }
        }
        marked.add(markedNew);
        decayTime.add(0);
        List<Integer> gui = new ArrayList<>();
        gui.add((int) orgTile.getX());
        gui.add((int) orgTile.getY());
        gui.add(0);
        yellTiles.add(gui);
    }

    public void distance(Agent agent){
        intrudersHear.clear();
        guardsHear.clear();
        Tile tile1 = agent.getPosition();
        for (int i = 0; i < intruders.size(); i++) {
            if(diagonalDistance(tile1, intruders.get(i).getPosition()) <= soundDistance){
                intrudersHear.add(intruders.get(i));
            }
        }

        for (int i = 0; i < guards.size(); i++) {
            if(diagonalDistance(tile1, guards.get(i).getPosition()) <= soundDistance){
                guardsHear.add(guards.get(i));
            }
        }
    }

    public void decay(){
        for (int i = 0; i < decayTime.size(); i++) {
            if(decayTime.get(i) == 20){
                marked.remove(i);
                decayTime.remove(i);
            } else {
                decayTime.set(i, decayTime.get(i) + 1);
            }
        }
    }

}
