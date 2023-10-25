package agent.explorer;

import Exceptions.NoPathException;
import controller.GameRunner;
import agent.Agent;
import map.Tile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicReference;

public class AStar {
    public static int nr = 0;
    public static final boolean DEBUG = false;

    //calculates the estimated cost
    private static double heuristic(Vertex vertex, Vertex target){
        if(vertex.getTile().getType().equals(Tile.Type.WALL)) return Integer.MAX_VALUE;
        if(!target.getTile().getType().equals(Tile.Type.TELEPORT) && vertex.getTile().getType().equals(Tile.Type.TELEPORT)){
            if (!vertex.getTile().getMarker().equals(Tile.Marker.TELEPORT)) {
                if(vertex.getTile().getTeleportGoal() == null){
                    int dx = (int)Math.round(vertex.getTile().getX() - target.getTile().getX());
                    int dy = (int)Math.round(vertex.getTile().getY() - target.getTile().getY());
                    return Math.abs(dx)+Math.abs(dy);
                }
                int dx = (int) Math.round(vertex.getTile().getTeleportGoal().getX() - target.getTile().getX());
                int dy = (int) Math.round(vertex.getTile().getTeleportGoal().getY() - target.getTile().getY());
                return Math.abs(dx) + Math.abs(dy);
            } else {
                return Tile.Marker.TELEPORT.getWeight();
            }}
        if(vertex.getTile().getMarker().equals(Tile.Marker.YELLINTRUDER)) return Tile.Marker.YELLINTRUDER.getWeight();
        if(vertex.getTile().getMarker().equals(Tile.Marker.YELLGUARD)) return Tile.Marker.YELLGUARD.getWeight();
        if(vertex.getTile().getMarker().equals(Tile.Marker.PHEROMONEINTRUDER)) return  Tile.Marker.PHEROMONEINTRUDER.getWeight();
        if(vertex.getTile().getMarker().equals(Tile.Marker.PHEROMONEGUARD)) return  Tile.Marker.PHEROMONEGUARD.getWeight();

        int dx = (int)Math.round(vertex.getTile().getX() - target.getTile().getX());
        int dy = (int)Math.round(vertex.getTile().getY() - target.getTile().getY());
        return Math.abs(dx)+Math.abs(dy);
    }

    /**
     * Finds the path from the start Vertex to the goal
     * @param start is the start Vertex
     * @param goal is the goal Vertex
     * @return the path in form of an arraylist
     * @throws NoPathException if there is no path from start to goal
     */
    public static LinkedList<Vertex> path(Vertex start, Vertex goal) throws NoPathException {
        nr++;
        long time = System.currentTimeMillis();
        //change run nr
        start.setParent(null);
        start.setRunNr(nr);
        //if start = end
        if(start.equals(goal)) {
            LinkedList<Vertex> p = new LinkedList<>();
            p.add(start);
            return p;
        }
        PriorityQueue<Vertex> openList = new PriorityQueue<>();
        boolean[][] closedList = new boolean[GameRunner.runner.getScene().getMapWidth()][GameRunner.runner.getScene().getMapHeight()];

        openList.add(start);

        Vertex first = null;
        int i = 0;
        while(!openList.isEmpty()){
            i++;
            first = openList.poll();
            ArrayList<Vertex> neighbors = first.getNeighbors();

            AtomicReference<Vertex> atomicFirst = new AtomicReference<>();
            atomicFirst.set(first);
            neighbors.forEach(n -> {
                if(!closedList[(int)n.getTile().getX()][(int) n.getTile().getY()]){
                    if(n.getRunNr() != nr){
                        n.setRunNr(nr);
                        n.setEstimatedCost(heuristic(n, goal));
                        n.setCostSoFar(atomicFirst.get().getCostSoFar() + 1);
                        n.setParent(atomicFirst.get());
                        if(!n.getTile().getType().equals(Tile.Type.WALL)){
                            openList.add(n);
                        }
                    }
                    else{
                        n.setCostSoFar(Math.min(n.getCostSoFar(),(atomicFirst.get().getCostSoFar()+1)));
                        if(n.getEstimatedCost() == atomicFirst.get().getEstimatedCost()+1){
                           n.setParent(atomicFirst.get());
                        }
                    }

                }
            });
            closedList[(int)first.getTile().getX()][(int) first.getTile().getY()] = true;
            if(first.equals(goal)) break;
        }
        if(DEBUG){
            System.out.println("Iterations needed: " + i);
            System.out.println("Astar time needed: " + (System.currentTimeMillis() - time) + " ms");
            System.out.println();
        }

        if(!first.equals(goal)){
            throw new NoPathException(start, goal);
        }
        else{
            return findPath(first);
        }
    }

    /**
     * Traces the path of the A*
     * @param Vertex is the final Vertex (goal) after the A* found it
     * @return the path from start to goal
     */
    private static LinkedList<Vertex> findPath(Vertex Vertex){
        LinkedList<agent.explorer.Vertex> path = new LinkedList<>();

        while(Vertex != null){
            path.add(0, Vertex);
            Vertex = Vertex.getParent();
        }

        return path;
    }

    public static void resetNr(){
        nr = 0;
    }
}
