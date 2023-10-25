import map.Map;
import map.Tile;
import map.scenario.Scenario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileTest {
    private static String path = "src/main/assets/testmap.txt";

    Scenario scenario = new Scenario(
            path
    );
    Map map = new Map(scenario);


    @Test
    void testGetTypeOf_default() {
        Tile t = map.getTileAt(15, 15);
        assertEquals(
                Tile.Type.DEFAULT,
                t.getType()
        );
    }

    @Test
    void testGetTypeOf_shaded() {
        Tile t = map.getTileAt(10, 20);
        assertEquals(
                Tile.Type.SHADED,
                t.getType()
        );
    }

    @Test
    void testGetTypeOf_wall() {
        Tile t = map.getTileAt(50, 0);
        assertEquals(
                Tile.Type.WALL,
                t.getType()
        );
    }

    @Test
    void testGetTypeOf_teleport() {
        Tile t = map.getTileAt(20, 70);
        assertEquals(
                Tile.Type.TELEPORT,
                t.getType()
        );
    }

}
