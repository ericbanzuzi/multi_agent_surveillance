import agent.Agent;
import agent.KnowledgeMatrix;
import agent.Intruder;
import agent.sensation.SmellMatrix;
import agent.sensation.SoundMatrix;
import agent.sensation.VisionMatrix;
import map.Map;
import map.Tile;
import map.Vector2d;
import map.scenario.Scenario;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AgentTest {
    private static String path = "src/main/assets/testmap.txt";

    Scenario scenario = new Scenario(
            path
    );
    Map map = new Map(scenario);

    @Test
    /*
    https://www.desmos.com/calculator/a459bwmvhm
     */
    void testGetTilesInFOV() {
        Intruder agent = new Intruder(
                map.getTileAt(47,18),
                new Vector2d(1,0),
                Math.PI/2,
                10,
                7,
                5,
                new VisionMatrix(map),
                new SmellMatrix(map),
                new KnowledgeMatrix(map, false, -1),
                new SoundMatrix(map)
        );

        List<Tile> actual = agent.getTilesInFOV();

        for(Tile tile : actual) {
            System.out.println(tile.getX() + "," + tile.getY());
        }

    }

    @Test
    void testGetTilesInSmell(){
        Intruder agent = new Intruder(
                map.getTileAt(47,18),
                new Vector2d(1,0),
                Math.PI/2,
                10,
                7,
                5,
                new VisionMatrix(map),
                new SmellMatrix(map),
                new KnowledgeMatrix(map,false, -1),
                new SoundMatrix(map)
        );

        List<Tile> actual = agent.getTilesInSmell();

        for(Tile tile : actual) {
            System.out.println(tile.getX() + "," + tile.getY());
        }
    }
}
