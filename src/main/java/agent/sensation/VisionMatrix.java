package agent.sensation;

import agent.explorer.Vertex;
import map.Map;
import map.Tile;
import map.Vector2d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class VisionMatrix {

    private final Map map;

    private final int height;
    private final int width;

    private final int[][] matrix;

    public VisionMatrix(Map map) {
        this.map = map;

        this.height = map.getHeight();
        this.width = map.getWidth();

        this.matrix = new int[height][width];
    }

    /**
     * this method uses the sector class to draw lines to each arc point
     *
     * @param position the agent's position
     * @param w        the head facing vector
     * @param theta    the vision angle
     * @param d        the vision distance
     * @return a list of tiles that are within the field of view of the agent
     */
    public List<Tile> getTilesInFOV(Tile position, Vector2d w, double theta, double d) {
        Sector sector = new Sector(position, theta, d, w);
        Vector2d[] lineSegments = sector.getLineSegments();

        Set<Tile> inFOV = new HashSet<>();
        for (Vector2d lineSegment : lineSegments) {
            List<Tile> line = line(position, lineSegment);
            for (Tile lineTile : line) {
                if (position.getDistanceTo(lineTile) <= d) {
                    if (position.copy().add(w).getAngleTo(lineTile) <= theta) {
                        inFOV.add(lineTile);
                    }
                }
            }
        }
        return new ArrayList<>(inFOV);
    }

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

        for (double i = 1; i <= n; i++) {
            double t = roundTo6dp(i / n);
            try {
                Tile currentTile = lerpTile(t0, t1, t);
                if (currentTile.getType().equals(Tile.Type.WALL)) {
                    currentTile = lerpTile(t0, t1, t);
                    tiles.add(currentTile);
                    break;
                } else {
                    tiles.add(currentTile);
                }

            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        return tiles;
    }

    /**
     * draws a line from t0 to end (does not stop at walls). t1 is the direction
     * @param t0 tile
     * @param dir direction
     * @return a list of tiles representing the line
     */
    public List<Tile> lineIgnoreWall(Vector2d t0, Vector2d dir) {
        //finding the furthest Tile t1 in direction dir
        Vector2d t1;
        //currently "cheating" a bit by taking goal as t1 instead of the furthest tile in direction dir
        t1 = t0.add(dir);
        //finding line
        List<Tile> tiles = new ArrayList<>();
        int n = diagonalDistance(t0, t1)+1;

        for (double i = 1; i <= n; i++) {
            double t = roundTo6dp(i / n);
            try {
                Tile currentTile = lerpTile(t0, t1, t);
                if (!currentTile.getType().equals(Tile.Type.WALL)) {
                    tiles.add(currentTile);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        return tiles;
    }

    protected Tile lerpTile(Vector2d t0, Vector2d t1, double t) {
        BigDecimal t0_x = BigDecimal.valueOf(t0.getX());
        BigDecimal t1_x = BigDecimal.valueOf(t1.getX());
        BigDecimal t0_y = BigDecimal.valueOf(t0.getY());
        BigDecimal t1_y = BigDecimal.valueOf(t1.getY());

        int x = Integer.parseInt(String.valueOf(lerp(t0_x, t1_x, BigDecimal.valueOf(t)).setScale(0, RoundingMode.HALF_UP)));
        int y = Integer.parseInt(String.valueOf(lerp(t0_y, t1_y, BigDecimal.valueOf(t)).setScale(0, RoundingMode.HALF_UP)));

        return map.getTileAt(x, y);
    }

    private BigDecimal lerp(BigDecimal start, BigDecimal end, BigDecimal t) {
        return start.add(t.multiply(end.subtract(start)));
    }

    /**
     * Returns the tile it gets teleported to
     *
     * @param t a teleporter tile
     * @return the end tile
     */
    public Tile teleport(Tile t) throws Exception {
        if (!t.getType().equals(Tile.Type.TELEPORT)) throw new Exception("NOT A TELEPORTER!");
        else {
            return map.getTileAt((int) t.getTeleportGoal().getX(), (int) t.getTeleportGoal().getY());
        }
    }

    protected int diagonalDistance(Vector2d t0, Vector2d t1) {
        double dx = t1.getX() - t0.getX();
        double dy = t1.getY() - t0.getY();

        return (int) Math.max(Math.abs(dx), Math.abs(dy));
    }

    /**
     * @param t the tile
     * @return a list of tiles that are neighbors to t
     */
    public List<Tile> getNeighborsOf(Tile t) {
        ArrayList<Tile> tiles = new ArrayList<>();
        int[] xarr = new int[]{-1, -1, -1, 0, 0, +1, +1, +1};
        int[] yarr = new int[]{-1, 0, +1, -1, +1, -1, 0, +1};
        for (int i = 0; i < 8; i++) {
            int xPos = (int) (t.getX() - xarr[i]);
            int yPos = (int) (t.getY() - yarr[i]);
            if (xPos >= 0 && xPos < width && yPos >= 0 && yPos < height) {
                tiles.add(map.getTileAt(xPos, yPos));
            }
        }
        return tiles;
    }

    /**
     * remember that the agent can only see the type of tiles that they actually explored
     * use this method when checking a type, since it ensures that the agent previously explored this tile.
     *
     * @param t the tile
     * @return the type of the tile if explored, UNEXPLORED otherwise
     */
    public Tile.Type getTypeOf(Tile t) {
        if (isExplored(t)) {
            return t.getType();
        }
        return Tile.Type.UNEXPLORED;
    }


    public void explore(Tile t) {
        matrix[(int) t.getY()][(int) t.getX()] = 1;
    }

    public boolean isExplored(Tile t) {
        return matrix[(int) t.getY()][(int) t.getX()] == 1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map getMap() {
        return map;
    }

    protected double roundTo6dp(double num) {
        return Math.round(num * 1000000.0) / 1000000.0;
    }

    private double roundTo3dp(double num) {
        return Math.round(num * 1000.0) / 1000.0;
    }

    public Tile getTileFromVector(Vector2d v){
        return map.getTileAt((int)v.getX(),(int)v.getY());
    }

    public boolean isFullyExplored(){
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if(matrix[i][j] != 1) return false;
            }
        }
        return true;
    }

    public Vertex[][] toGraph(){
        Vertex[][] graph = new Vertex[height][width];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                graph[j][i] = new Vertex(new Tile(i,j,getTypeOf(map.getTileAt(i,j))));
                //no edges to walls
                if(!graph[j][i].getTile().getType().equals(Tile.Type.WALL)){
                    if(j!=0 && !graph[j-1][i].getTile().getType().equals(Tile.Type.WALL)) graph[j][i].addNeighbor(graph[j-1][i]);
                    if(i!=0 && !graph[j][i-1].getTile().getType().equals(Tile.Type.WALL)) graph[j][i].addNeighbor(graph[j][i-1]);
                }
            }
        }
        return graph;
    }

    public double getExploredRatio() {
        int exploredTilesCount = 0;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                Tile t = map.getTileAt(i,j);
                if (isExplored(t)) {
                    exploredTilesCount++;
                }
            }
        }
        return (double) exploredTilesCount / (height* width);
    }

    public int[][] getMat(){
        int[][] copyMat = Arrays.stream(matrix).map(int[]::clone).toArray(int[][]::new);
        return copyMat;
    }
}
