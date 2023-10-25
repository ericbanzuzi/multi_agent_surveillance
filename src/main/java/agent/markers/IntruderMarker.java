package agent.markers;

import agent.Agent;
import agent.Intruder;
import controller.GameState;
import map.Tile;

import java.util.List;

public abstract class IntruderMarker extends AgentMarker{

    public IntruderMarker(GameState gameState, Intruder intruder){
        super(gameState,intruder);
    }

    public void markLocation() {
        int i = gameState.getIntruderIndex((Intruder) this.agent);
        List<Intruder> intruders = gameState.getIntruders();
        for (int j = 0; j < intruders.size(); j++) {
            if (j != i) {
                markLocation(intruders.get(j));
            }
        }
    }

    protected void markLocation(Intruder intruder) {

    }
}
