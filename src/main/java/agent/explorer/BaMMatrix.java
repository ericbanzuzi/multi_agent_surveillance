package agent.explorer;

import Exceptions.NoPathException;
import agent.Agent;
import agent.sensation.VisionMatrix;
import map.Map;
import map.Tile;
import map.Vector2d;

import java.util.LinkedList;
import java.util.List;

public class BaMMatrix {

    private int[][] matrix;
    private Map map;
    private VisionMatrix visMat;
    private Agent agent;
    private Vector2d lastPos;
    private Vertex[][] graph;

    public BaMMatrix(VisionMatrix mat, Agent a){
        agent = a;
        lastPos = a.getPosition();
        visMat = mat;
        this.map = mat.getMap();
        matrix = new int[map.getWidth()][map.getHeight()];
        resetMap();
    }

    public int getStateAt(Tile pos){
        return matrix[(int) pos.getX()][(int) pos.getY()];
    }

    public void setExplored(int x, int y){
        if(matrix[x][y] != 1){
            matrix[x][y] = 0;
        }
    }

    public void setVisited(int x, int y){
        matrix[x][y] = 1;
        graph[x][y].getTile().setType(Tile.Type.WALL);
    }

    public Tile bestUnexploredNeighbor(int x, int y){
        int[] neighborFitness = new int[4];
        // if neighbor is not unexplored set to -1, else set to amount of walls near
        try {
            if(matrix[x-1][y] == -1){
                neighborFitness[0] = amtWalls(x-1,y);
            }
            else {
                neighborFitness[0] = -1;
            }
        }
        catch(Exception e){
            neighborFitness[0] = -1;
        }
        try {
            if(matrix[x][y-1] == -1){
                neighborFitness[1] = amtWalls(x,y-1);
            }
            else {
                neighborFitness[1] = -1;
            }
        }
        catch(Exception e){
            neighborFitness[1] = -1;
        }
        try {
            if(matrix[x+1][y] == -1){
                neighborFitness[2] = amtWalls(x+1,y);
            }
            else {
                neighborFitness[2] = -1;
            }
        }
        catch(Exception e){
            neighborFitness[2] = -1;
        }
        try {
            if(matrix[x][y+1] == -1){
                neighborFitness[3] = amtWalls(x,y+1);
            }
            else {
                neighborFitness[3] = -1;
            }
        }
        catch(Exception e){
            neighborFitness[3] = -1;
        }

        int currentBest = neighborFitness[0];
        int currentNr = 0;

        if(neighborFitness[1] > currentBest){
            currentBest = neighborFitness[1];
            currentNr = 1;
        }
        if(neighborFitness[2] > currentBest){
            currentBest = neighborFitness[2];
            currentNr = 2;
        }
        if(neighborFitness[3] > currentBest){
            currentBest = neighborFitness[3];
            currentNr = 3;
        }
        if(currentBest == -1) return null;

        switch (currentNr){
            case 0:
                return map.getTileAt(x-1,y);
            case 1:
                return map.getTileAt(x,y-1);
            case 2:
                return map.getTileAt(x+1,y);
            case 3:
                return map.getTileAt(x,y+1);
            default:
                System.out.printf("Issue with unexplored neighbor!!!");
        }
        return null;
    }

    public Tile rndExploredNeighbor(int x, int y){
        LinkedList<int[]> exploNeigh = new LinkedList<>();
        try {
            if(matrix[x-1][y] == 0){
                exploNeigh.add(new int[]{x-1,y});
            }
        }
        catch(Exception ignored){
        }
        try {
            if(matrix[x][y-1] == 0){
                exploNeigh.add(new int[]{x,y-1});
            }
        }
        catch(Exception ignored){
        }
        try {
            if(matrix[x+1][y] == 0){
                exploNeigh.add(new int[]{x+1,y});
            }
        }
        catch(Exception ignored){
        }
        try {
            if(matrix[x][y+1] == 0){
                exploNeigh.add(new int[]{x,y+1});
            }
        }
        catch(Exception ignored){
        }

        if(exploNeigh.isEmpty()) return null;
        if(exploNeigh.size() > 1){
            for(int i = 0; i < exploNeigh.size(); i++){
                int[] pos = exploNeigh.get(i);
                if((int) lastPos.getX() == pos[0] && (int) lastPos.getY() == pos[1]){
                    exploNeigh.remove(i);
                }
            }
        }
        int rnd = (int) (Math.random() * exploNeigh.size());

        return map.getTileAt(exploNeigh.get(rnd)[0],exploNeigh.get(rnd)[1]);
    }

    private int amtWalls(int x, int y){
        int i = 0;
        try{
            if(matrix[x-1][y] == 1) i++;
        }
        catch (Exception ignored){
        }
        try{
            if(matrix[x][y-1] == 1) i++;
        }
        catch (Exception ignored){
        }
        try{
            if(matrix[x+1][y] == 1) i++;
        }
        catch (Exception ignored){
        }
        try{
            if(matrix[x][y+1] == 1) i++;
        }
        catch (Exception ignored){
        }
        return i;
    }

    /**
     * Checks whether setting this tile to visited will block the path todo wrong and not working correctly
     * @param agentPos next agent position
     * @param x current x position
     * @param y current y position
     * @return true if not blocked, false otherwise
     */
    public boolean checkNotBlocking(Tile agentPos, int x, int y){
        //simulate x and y being a wall
        graph[x][y].getTile().setType(Tile.Type.WALL);
        try{
            try {
                if(matrix[x][y+1] != 1){
                    LinkedList<Vertex> p = AStar.path(graph[(int)agentPos.getX()][(int)agentPos.getY()],graph[x][y+1]);
                }
            } catch (IndexOutOfBoundsException ignored){
            }
            try {
                if(matrix[x+1][y] != 1){
                    LinkedList<Vertex> p = AStar.path(graph[(int)agentPos.getX()][(int)agentPos.getY()],graph[x+1][y]);
                }
            } catch (IndexOutOfBoundsException ignored){
            }
            try {
                if(matrix[x-1][y] != 1){
                    LinkedList<Vertex> p = AStar.path(graph[(int)agentPos.getX()][(int)agentPos.getY()],graph[x-1][y]);
                }
            } catch (IndexOutOfBoundsException ignored){
            }
            try {
                if(matrix[x][y-1] != 1){
                    LinkedList<Vertex> p = AStar.path(graph[(int)agentPos.getX()][(int)agentPos.getY()],graph[x][y-1]);
                }
            } catch (IndexOutOfBoundsException ignored){
            }
        }
        catch(NoPathException e){
            graph[x][y].getTile().setType(Tile.Type.WALL);
            return false;
        }
        graph[x][y].getTile().setType(Tile.Type.WALL);
        return true;
    }

    public boolean checkNotBlockingCurrent(int x, int y){
        Vertex[][] graph = toGraph();
        //simulate x and y being a wall
        graph[x][y].getTile().setType(Tile.Type.WALL);
        try{
            try {
                if(matrix[x][y+1] != 1){
                    if(matrix[x][y-1] != 1){
                        AStar.path(graph[x][y+1],graph[x][y-1]);
                    }
                    if(matrix[x+1][y] != 1){
                        AStar.path(graph[x][y+1],graph[x+1][y]);
                    }
                    if(matrix[x-1][y] != 1){
                        AStar.path(graph[x][y+1],graph[x-1][y]);
                    }
                }
            } catch (IndexOutOfBoundsException ignored){
            }
            try {
                if(matrix[x][y-1] != 1){
                    if(matrix[x+1][y] != 1){
                        AStar.path(graph[x][y-1],graph[x+1][y]);
                    }
                    if(matrix[x-1][y] != 1){
                        AStar.path(graph[x][y-1],graph[x-1][y]);
                    }
                }
            } catch (IndexOutOfBoundsException ignored){
            }
            try {
                if(matrix[x+1][y] != 1){
                    if(matrix[x-1][y] != 1){
                        AStar.path(graph[x+1][y],graph[x-1][y]);
                    }
                }
            } catch (IndexOutOfBoundsException ignored){
            }
        }
        catch(NoPathException e){
            return false;
        }
        return true;
    }

    public void resetMap(){
        graph = toGraph();
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                if(map.getTileAt(i,j).getType().equals(Tile.Type.WALL)){
                    //consider visited if wall (dont go there again)
                    matrix[i][j] = 1;
                }
                else{
                    //consider unexplored
                    matrix[i][j] = -1;
                }
            }
        }
    }

    public Vertex[][] toGraph(){
        Vertex[][] graph = new Vertex[map.getWidth()][map.getHeight()];
        for(int i = 0; i < map.getWidth(); i++){
            for(int j = 0; j < map.getHeight(); j++){
                Tile.Type type = switch (matrix[i][j]){
                    case -1, 0 -> Tile.Type.DEFAULT;
                    case 1 -> Tile.Type.WALL;
                    default -> throw new IllegalStateException("Unexpected value during graph creation: " + matrix[i][j]);
                };
                graph[i][j] = new Vertex(new Tile(i,j,type));
                //no edges to walls
                if(!graph[i][j].getTile().getType().equals(Tile.Type.WALL)){
                    if(i!=0 && !graph[i-1][j].getTile().getType().equals(Tile.Type.WALL)) graph[i][j].addNeighbor(graph[i-1][j]);
                    if(j!=0 && !graph[i][j-1].getTile().getType().equals(Tile.Type.WALL)) graph[i][j].addNeighbor(graph[i][j-1]);
                }
            }
        }
        return graph;
    }

    public void exploreVision(Tile pos){

        List<Tile> visionTiles = visMat.getTilesInFOV(pos, agent.getW(), agent.getTheta(), agent.getD());
        for(Tile t : visionTiles){
            // only check if not marked as visited already
            if(matrix[(int) t.getX()][(int) t.getY()] != 1){
                if(checkNotBlocking(pos, (int) t.getX(), (int) t.getY())){
                    setVisited((int) t.getX(), (int) t.getY());
                }
                else{
                    setExplored((int) t.getX(), (int) t.getY());
                }
            }
        }
    }

    public void setLastPos(Vector2d lastPos) {
        this.lastPos = lastPos;
    }
}
