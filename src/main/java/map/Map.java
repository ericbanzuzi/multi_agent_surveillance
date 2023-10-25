package map;

import agent.Agent;
import agent.Intruder;
import agent.explorer.Vertex;
import map.scenario.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Map {

    private final int height;
    private final int width;
    private List<Wall> walls;
    private ArrayList<TelePortal> teleports;
    private ArrayList<Shaded> shaded;
    private Area targetArea;
    private boolean teleportMarked;

    private final Tile[][] board;

    public Map(Scenario scenario){
        this.height = scenario.getMapHeight();
        this.width = scenario.getMapWidth();
        this.walls = scenario.getWalls();
        this.teleports = scenario.getTeleports();
        this.shaded = scenario.getShaded();
        this.targetArea = scenario.getTargetArea();

        this.board = new Tile[height][width];

        /*
        by default all tiles will have type = Tile.Type.DEFAULT
         */
        for (int i = 0; i < scenario.getMapHeight(); i++) {
            for (int j = 0; j < scenario.getMapWidth(); j++) {
                board[i][j] = new Tile(j,i,Tile.Type.DEFAULT);
            }
        }

        /*
        following loops create the tiles given the information in the scenario.
         */
        scenario.getShaded().forEach(shaded -> {
            for(int i = shaded.getLeftBoundary(); i < shaded.getRightBoundary(); i++){
                for(int j = shaded.getTopBoundary(); j < shaded.getBottomBoundary(); j++){
                    board[j][i].setType(Tile.Type.SHADED);
                }
            }
        });
        scenario.getWalls().forEach(walls -> {
            for(int i = walls.getLeftBoundary(); i < walls.getRightBoundary(); i++){
                for(int j = walls.getTopBoundary(); j < walls.getBottomBoundary(); j++){
                    board[j][i].setType(Tile.Type.WALL);
                }
            }
        });
        scenario.getTeleportals().forEach(tele -> {
            Vector2d goal = new Vector2d(tele.getxTarget(), tele.getyTarget());
            for(int i = tele.getLeftBoundary(); i < tele.getRightBoundary(); i++){
                for(int j = tele.getTopBoundary(); j < tele.getBottomBoundary(); j++){
                    board[j][i].setType(Tile.Type.TELEPORT);
                    board[j][i].setTeleportGoal(goal);
                }
            }
        });
        Area target = scenario.getTargetArea();
        if(target != null) {
            for (int i = target.getLeftBoundary(); i < target.getRightBoundary(); i++) {
                for (int j = target.getTopBoundary(); j < target.getBottomBoundary(); j++) {
                    board[j][i].setType(Tile.Type.TARGET);
                }
            }
        }
    }

    public Tile getTileAt(int x, int y) {
        // TODO: boundary checks
        return board[y][x];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public ArrayList<TelePortal> getTeleports() {
        return teleports;
    }

    public ArrayList<Shaded> getShaded() {
        return shaded;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Area getTargetArea() {
        return targetArea;
    }

    public Vertex[][] toGraph(){
        Vertex[][] graph = new Vertex[height][width];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                graph[j][i] = new Vertex(getTileAt(i,j));
                //no edges to walls
                if(!graph[j][i].getTile().getType().equals(Tile.Type.WALL)){
                    if(j!=0 && !graph[j-1][i].getTile().getType().equals(Tile.Type.WALL)) graph[j][i].addNeighbor(graph[j-1][i]);
                    if(i!=0 && !graph[j][i-1].getTile().getType().equals(Tile.Type.WALL)) graph[j][i].addNeighbor(graph[j][i-1]);
                }
            }
        }
        return graph;
    }

    public List<Tile> getOtherTilesOfTeleport(Tile tile, Agent agent) {
        double x = tile.getX();
        double y = tile.getY();
        List<Tile> tiles = new LinkedList<>();

        for (int i = 0; i < teleports.size(); i++) {
            if (teleports.get(i).isHit(x, y)) {
                TelePortal tele = teleports.get(i);
                int left = tele.getLeftBoundary();
                int top = tele.getTopBoundary();
                int width = Math.abs(tele.getRightBoundary() - left);
                int height = Math.abs(tele.getBottomBoundary() - top);
                for (int j = 0; j < width; j++) {
                    for (int k = 0; k < height; k++) {
                        tiles.add(agent.getTileAt(left, top));
                        left++;
                    }
                    left = left - width;
                    top--;
                }
                tele.mark();
            }
        }

        return tiles;
    }

    public List<double[]> getBoundariesOfTeleMarkers(){
        List<double[]> boundaries = new ArrayList<>();
        for(TelePortal tele: teleports){
            if(tele.isMarked()){
                double x1 = tele.getLeftBoundary() - 0.5;
                double x2 = tele.getRightBoundary() + 0.5;
                double y1 = tele.getTopBoundary() - 0.5;
                double y2 = tele.getBottomBoundary() + 0.5;
                boundaries.add(new double[]{x1,x2,y1,y2});
            }
        }
        return boundaries;
    }

    public void setTeleportMarker(boolean marked) {
        this.teleportMarked = marked;
    }

    public boolean teleportMarked() {
        return teleportMarked;
    }
}
