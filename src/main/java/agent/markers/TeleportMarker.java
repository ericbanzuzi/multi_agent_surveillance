package agent.markers;

import agent.Agent;
import map.Map;
import map.Tile;

import java.util.List;

public class TeleportMarker {
    private final int DECAY_TIME_STEP = 50;
    List<Tile> tiles;
    Map map;

    public void markLocation(Agent agent, Tile enteredTeleport) {
        tiles = agent.getVisionMatrix().getMap().getOtherTilesOfTeleport(enteredTeleport, agent);
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setMarker(Tile.Marker.TELEPORT);
        }
        map = agent.getVisionMatrix().getMap();
        map.setTeleportMarker(true);
    }

    public boolean decay(int decayTimeStep) {
        if (decayTimeStep > DECAY_TIME_STEP) {
            for (int i = 0; i < tiles.size(); i++) {
                tiles.get(i).setMarker(Tile.Marker.DEFAULT);
            }
            map.setTeleportMarker(false);
            return true;
        }
        return false;
    }

}
