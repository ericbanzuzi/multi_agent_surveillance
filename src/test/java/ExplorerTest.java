import agent.Explorer;
import agent.KnowledgeMatrix;
import agent.sensation.SmellMatrix;
import agent.sensation.SoundMatrix;
import agent.sensation.VisionMatrix;
import map.Map;
import map.Vector2d;
import map.scenario.Scenario;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

public class ExplorerTest {

    DecimalFormat df = new DecimalFormat("#.####");

    private static String path = "src/main/assets/testmap.txt";

    Scenario scenario = new Scenario(
            path
    );
    Map map = new Map(scenario);

    @Test
    void testAct() {
        Explorer explorer = new Explorer(
                map.getTileAt(1, 1),
                new Vector2d(1, 0),
                Math.PI / 2,
                5,
                7,
                5,
                new VisionMatrix(map),
                new SmellMatrix(map),
                new KnowledgeMatrix(map, false, -1),
                new SoundMatrix(map),
                "random"
        );

        int t = 0;
        while (explorer.getExploredRatio() <= 0.6) {
            explorer.act();
            t++;
            System.out.println("t=" + t + " : " + explorer.getPosition() + "," + explorer.getW() + ", " + df.format(explorer.getExploredRatio()));
        }

    }

}
