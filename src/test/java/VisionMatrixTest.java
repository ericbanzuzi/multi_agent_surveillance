import agent.sensation.VisionMatrix;
import map.Map;
import map.scenario.Scenario;
import map.Tile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VisionMatrixTest {
    private static String path = "src/main/assets/testmap.txt";

    Scenario scenario = new Scenario(
            path
    );
    Map map = new Map(scenario);
    VisionMatrix visionMatrix = new VisionMatrix(map);


    @Test
    void testGetTypeOf_unexplored() {
        Tile t = map.getTileAt(15, 15);
        assertEquals(
                Tile.Type.UNEXPLORED,
                visionMatrix.getTypeOf(t)
        );
    }

    @Test
    void testGetTypeOf_explored() {
        Tile t = map.getTileAt(15, 15);
        visionMatrix.explore(t);
        assertEquals(
                Tile.Type.DEFAULT,
                visionMatrix.getTypeOf(t)
        );
    }

    @Test
    void testLine() {
        Tile t0 = map.getTileAt(47,18);
        Tile t1 = map.getTileAt(43,14);

        List<Tile> actual = visionMatrix.line(t0, t1);

        for(Tile tile : actual) {
            System.out.println(tile.getX() + "," + tile.getY());
        }
    }

    @Test
    void testGetNeighbors_centeredTile() {
        Tile t = map.getTileAt(1,1);

        List<Tile> expected = new ArrayList<>();
        expected.add(map.getTileAt(0,0));
        expected.add(map.getTileAt(0,1));
        expected.add(map.getTileAt(0,2));
        expected.add(map.getTileAt(1,0));
        expected.add(map.getTileAt(1,2));
        expected.add(map.getTileAt(2,0));
        expected.add(map.getTileAt(2,1));
        expected.add(map.getTileAt(2,2));

        List<Tile> actual = visionMatrix.getNeighborsOf(t);

        assertTrue(
                actual.size() == expected.size() && actual.containsAll(expected)
        );
    }

    @Test
    void testGetNeighbors_boundaryTile() {
        Tile t = map.getTileAt(0,0);

        List<Tile> expected = new ArrayList<>();
        expected.add(map.getTileAt(1,0));
        expected.add(map.getTileAt(0,1));
        expected.add(map.getTileAt(1,1));

        List<Tile> actual = visionMatrix.getNeighborsOf(t);

        assertTrue(
                actual.size() == expected.size() && actual.containsAll(expected)
        );
    }

}
