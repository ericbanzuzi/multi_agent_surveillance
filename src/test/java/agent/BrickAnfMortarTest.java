package agent;

import agent.explorer.BaMMatrix;
import agent.explorer.BrickAndMortar;
import agent.sensation.SmellMatrix;
import agent.sensation.VisionMatrix;
import controller.GameRunner;
import map.Map;
import map.Tile;
import map.Vector2d;
import map.scenario.Scenario;
import org.junit.jupiter.api.Test;

public class BrickAnfMortarTest {

    @Test
    void name(){
        GameRunner game = new GameRunner("src/main/assets/BaMTestMap.txt");
        game.getController().pause();
        Guard g = game.getController().getGameState().getGuards().get(0);
        g.setW(new Vector2d(1, 0));
        BaMMatrix mat = new BaMMatrix(g.getVisionMatrix(), g);

        //create "walls" around agent to see reaction
        mat.setVisited(4,6);
        mat.setVisited(4,4);
        mat.setVisited(3,5);
        mat.setVisited(5,6);
        mat.setVisited(5,4);
        mat.setVisited(6,6);
        mat.setVisited(6,4);
        //action
        Tile newPos = BrickAndMortar.explore(mat,g.getPosition());
        assert(!newPos.equals(g.getPosition()));
    }
}
