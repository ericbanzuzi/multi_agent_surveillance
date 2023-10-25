package agent.markers;

import agent.Agent;
import agent.Intruder;
import controller.GameState;
import map.Tile;

public abstract class AgentMarker implements Marker{
    protected GameState gameState;
    protected Agent agent;
    protected Tile markedCoordinate;

    public AgentMarker(GameState gameState, Agent agent){
        this.gameState = gameState;
        this.agent = agent;
        this.markedCoordinate = this.agent.getPosition();
    }

    // erasing depends on how you perform the marker
    // it is just to clear the actions
    // we have done for that coordinate
}
