package agent.sensation;

import map.Map;
import map.Tile;
import map.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class SmellMatrix extends VisionMatrix {

//    private static final double SMELL_DISTANCE = 7;

    public SmellMatrix(Map map) {
        super(map);
    }

    public List<Tile> getTilesInSmell(Tile position, Vector2d w, double smellDistance) {
        return this.getTilesInFOV(position, w, Math.PI * 2, smellDistance);
    }

    // overwrite the method with characteristic that
    // the smell ability will shrink by one tile if there is a wall obstructing

    /**
     * draws a line from t0 to t1
     *
     * @param t0 tile
     * @param t1 tile
     * @return a list of tiles representing the line
     */
    public List<Tile> line(Vector2d t0, Vector2d t1) {
        List<Tile> tiles = new ArrayList<>();
        int n = diagonalDistance(t0, t1) + 1;
        int iterations = n;

        for (double i = 1; i <= iterations; i++) {
            double t = roundTo6dp(i / n);
            try {
                Tile currentTile = lerpTile(t0, t1, t);
                if (currentTile.getType().equals(Tile.Type.WALL)) {
                    currentTile = lerpTile(t0, t1, t);
                    tiles.add(currentTile);
                    iterations--;
                } else {
                    tiles.add(currentTile);
                }

            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        return tiles;
    }

    private int integerDistance(Vector2d t0, Vector2d t1) {
        return (int) Math.round(t0.getDistanceTo(t1));
    }


}
