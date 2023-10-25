package agent.explorer;

import map.Tile;

import java.util.ArrayList;
import java.util.LinkedList;

public class Vertex implements Comparable{

    private double estimatedCost;
    private double costSoFar;
    private Tile tile;
    private Vertex parent;
    private ArrayList<Vertex> neighbors;

    public int getRunNr() {
        return runNr;
    }

    public void setRunNr(int runNr) {
        this.runNr = runNr;
    }

    private int runNr;

    public Vertex(Tile tile){
        neighbors = new ArrayList<>();
        this.tile = tile;
    }

    /**
     * Adds a vertex to the set of neighbors
     * @param neighbor the adjacent vertex that will be added
     */
    public void addNeighbor(Vertex neighbor){
        if(neighbors.contains(neighbor)) return;
        else{
            neighbors.add(neighbor);
            neighbor.getNeighbors().add(this);
        }
    }

    /**
     * Removes vertex from the neighbor set
     * @param neighbor the vertex that will be removed from the neighbors
     */
    public void removeNeighbor(Vertex neighbor){
        if(!neighbors.contains(neighbor)) return;
        else{
            neighbors.remove(neighbor);
            neighbor.getNeighbors().remove(this);
        }
    }

    /**
     * @return the neighbors in form of an arraylist
     */
    public ArrayList<Vertex> getNeighbors(){
        return neighbors;
    }

    public Tile getTile(){
        return tile;
    }

    /**
     * Merges a vertex into another one. Removes all neighbors of the second vertex.
     * The idea is that the content of two vertices get saved in one and the second one
     * can be safely deleted afterwards.
     * @param vertex that will be merged into the first vertex.
     * @throws Exception if they are not in the same position, so it would not make sense to merge them.
     */
    public void merge(Vertex vertex) throws Exception {
        if(!this.equals(vertex)) throw new Exception("Only equal vertices can be merged");
        vertex.getNeighbors().forEach(this::addNeighbor);
        for (Vertex n : vertex.getNeighbors()) {
            n.removeNeighbor(vertex);
        }
    }

    public boolean equals(Object o) {
        Vertex v = (Vertex) o;
        if(this.tile.getX() == v.getTile().getX() && this.tile.getY() == v.getTile().getY()) return true;
        else return false;
    }

    @Override
    public int compareTo(Object o) {
        if(estimatedCost+costSoFar < ((Vertex)o).getEstimatedCost()+((Vertex)o).getCostSoFar()) return -1;
        else if (estimatedCost+costSoFar > ((Vertex)o).getEstimatedCost()+((Vertex)o).getCostSoFar()) return 1;
        else return 0;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public double getCostSoFar() {
        return costSoFar;
    }

    public void setCostSoFar(double costSoFar) {
        this.costSoFar = costSoFar;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }
}
