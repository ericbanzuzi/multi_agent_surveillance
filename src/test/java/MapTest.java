import controller.GameController;
import controller.GameState;
import map.Map;
import map.Tile;
import map.scenario.Scenario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapTest {

    private static String path = "src/main/assets/testmap.txt";
    Scenario scenario = new Scenario(
            path
    );
    Map map = new Map(scenario);
    GameController controller = new GameController(scenario);
    GameState gameState = controller.getGameState();


    public static void main(String[] args) {
        Scenario scenario = new Scenario(
                path
        );
        Map map = new Map(scenario);
        GameController controller = new GameController(scenario);
        GameState gameState = controller.getGameState();
        Tile t = map.getTileAt(22, 72);
        System.out.println(map.getOtherTilesOfTeleport(t,gameState.getIntruders().get(0)));
    }
//    @Test
//    void testGetTilesOfTeleport() {
//        Tile t = map.getTileAt(22, 72);
//        System.out.println(map.getOtherTilesOfTeleport(t,gameState.getIntruders().get(0)));
//    }
}
