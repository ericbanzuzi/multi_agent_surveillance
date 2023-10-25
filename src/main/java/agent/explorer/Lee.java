package agent.explorer;

import Exceptions.NoPathException;
import agent.Agent;
import agent.sensation.VisionMatrix;
import map.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lee {

    private Agent agent;
    private VisionMatrix visionMatrix; // shared knowledge
    private List<Tile> q;
    private Vertex[][] vertexMap;
    private List<Vertex> currentPath;
    private String dir;

    public Lee(VisionMatrix visionMatrix, Agent agent) {
        this.agent = agent;
        this.visionMatrix = visionMatrix;
        this.q = new LinkedList<>();
        this.currentPath = new LinkedList<>();

        int y = (int) this.agent.getPosition().getY();
        int x = (int) this.agent.getPosition().getX();
        visionMatrix.explore(visionMatrix.getMap().getTileAt(x, y));
        q.add(agent.getPosition());
        addNeighbors(visionMatrix.getMap().getTileAt(x, y)); // maybe start
        this.vertexMap = visionMatrix.getMap().toGraph();
    }

    private boolean visited(Tile t) {
        return visionMatrix.isExplored(t);
    }

    public List<Tile> getNeighbors(Tile t) {
        return visionMatrix.getNeighborsOf(t);
    }

    public void addNeighbors(Tile pos){
        List<Tile> n = getNeighbors(pos);
        for(Tile t : n){
            if(!visionMatrix.isExplored(t) && !q.contains(t)){
                q.add(t);
            }
        }
    }

    public void step(){
        Tile pos = agent.getPosition();
        visionMatrix.explore(pos);
        q.remove(pos);

        addNeighbors(pos);
        List<Tile> fov = visionMatrix.getTilesInFOV(pos, agent.getW(), agent.getTheta(), agent.getD());
        for(Tile t : fov){
            visionMatrix.explore(t);
            this.agent.explore(t);
            if(!visionMatrix.getTypeOf(t).equals(Tile.Type.TELEPORT)){
                q.remove(t);
            }
            addNeighbors(t);
        }

        if(!currentPath.isEmpty()){
            Tile newPos = currentPath.get(0).getTile();
            currentPath.remove(0);
            q.remove(newPos);
            agent.move(newPos);
            visionMatrix.explore(newPos);

        }else {
            Tile targetPos = q.get(0);
            q.remove(0);

            while(visionMatrix.getMap().getTileAt((int)targetPos.getX(),(int)targetPos.getY()).getType().equals(Tile.Type.WALL)
            ||  (visionMatrix.getMap().teleportMarked() && visionMatrix.getMap().getTileAt((int)targetPos.getX(),(int)targetPos.getY()).getType().equals(Tile.Type.TELEPORT))){
                targetPos = q.get(0);
                q.remove(0);
            }

            try {
                currentPath = AStar.path(
                        this.vertexMap[(int)pos.getY()][(int)pos.getX()],
                        this.vertexMap[(int)targetPos.getY()][(int)targetPos.getX()]
                );
                Tile newPos = currentPath.get(1).getTile();
                currentPath.remove(this.vertexMap[(int)pos.getY()][(int)pos.getX()]);
                currentPath.remove(this.vertexMap[(int)newPos.getY()][(int)newPos.getX()]);
                agent.move(newPos);
                visionMatrix.explore(newPos);

            } catch (NoPathException e) {
                e.printStackTrace();
            }

            if(!visionMatrix.isExplored(targetPos))
                q.add(targetPos);
        }
//        System.out.println("moved from ("+pos.getX()+","+pos.getY()+") to ("+agent.getPosition().getX()+","+agent.getPosition().getY()+")");
    }

    public List<Tile> merge(List<Tile> left, List<Tile> right) {

        List<Tile> sorted = new ArrayList<>();
        int i = 0; //next element in the left array to be compared
        int j = 0; //next element in the right array to be compared

        // while left or right array still has elements that have not been placed in the merged array,
        // compare two elements and place the smaller one in the merged array
//        int[] ij = compare(0, 0, left, right, sorted); //next element in the right array to be compared
        while (i < left.size() && j < right.size()) {
            if (heuristic(left.get(i)) < heuristic(right.get(i))) { //if the element of the left is bigger, place it on the merged array and move to next element
                sorted.add(left.get(i));
                i++;
            } else {
                sorted.add(right.get(j));
                j++;
            }
        }

        //copying any remaining elements of the array
        while (i < left.size()) {
            sorted.add(left.get(i));
            i++;
        }

        //copying any remaining elements of the array
        while (j < right.size()) {
            sorted.add(right.get(j));
            j++;
        }
        return sorted;
    }

    public List<Tile> sort(List<Tile> list) {
        // Find the middle point
        if (list.size() > 1) {
            int m = list.size() / 2;

            List<Tile> l1 = list.subList(0, m);
            List<Tile> l2 = list.subList(m, list.size());

            l1 = sort(l1);
            l2 = sort(l2);
            return merge(l1, l2);

        } else {
            return list;
        }
    }

    private int[] compare(int i, int j, List<Tile> left, List<Tile> right, String direction, List<Tile>
            sorted) {
        // change the comparison based on direction
        if (direction.equals("up")) {
            while (i < left.size() && j < right.size()) {
                if (left.get(i).getY() < heuristic(right.get(i))) { //if the element of the left is bigger, place it on the merged array and move to next element
                    sorted.add(left.get(i));
                    i++;
                } else {
                    sorted.add(right.get(j));
                    j++;
                }
            }

        } else if (direction.equals("down")) {
            while (i < left.size() && j < right.size()) {
                if (left.get(i).getY() > right.get(i).getY()) { //if the element of the left is bigger, place it on the merged array and move to next element
                    sorted.add(left.get(i));
                    i++;
                } else {
                    sorted.add(right.get(j));
                    j++;
                }
            }

        } else if (direction.equals("left")) {
            while (i < left.size() && j < right.size()) {
                if (left.get(i).getX() < right.get(i).getX()) { //if the element of the left is smaller, place it on the merged array and move to next element
                    sorted.add(left.get(i));
                    i++;
                } else {
                    sorted.add(right.get(j));
                    j++;
                }
            }
        } else {

            while (i < left.size() && j < right.size()) {
                if (left.get(i).getX() > right.get(i).getX()) { //if the element of the left is bigger, place it on the merged array and move to next element
                    sorted.add(left.get(i));
                    i++;
                } else {
                    sorted.add(right.get(j));
                    j++;
                }
            }
        }
        return new int[]{i,j};
    }

    //calculates the estimated cost todo improve (currently Manhattan distance)
    private  double heuristic(Tile target){
        if(visionMatrix.isExplored(target) && target.getType().equals(Tile.Type.WALL))
            return Integer.MAX_VALUE;
        if(visionMatrix.isExplored(target) && target.getType().equals(Tile.Type.TELEPORT)){
            int dx = (int)Math.round(target.getTeleportGoal().getX() - this.agent.getPosition().getX());
            int dy = (int)Math.round(target.getTeleportGoal().getY() - this.agent.getPosition().getY());
            return Math.abs(dx)+Math.abs(dy);
        }
        int dx = (int)Math.round(target.getX() - this.agent.getPosition().getX());
        int dy = (int)Math.round(target.getY() - this.agent.getPosition().getY());
        return Math.abs(dx)+Math.abs(dy);
    }
}

