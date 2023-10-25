package agent.explorer;

import agent.sensation.VisionMatrix;
import map.Tile;

public class BrickAndMortar {

    private final static boolean DEBUG = true;

    public static Tile explore(BaMMatrix mat, Tile pos){
        marking(mat, pos);
        Tile next = navigation(mat, pos);
        return next;
    }

    private static void marking(BaMMatrix mat, Tile pos){
        mat.exploreVision(pos);
        if(mat.checkNotBlockingCurrent((int)pos.getX(),(int)pos.getY())){
            mat.setVisited((int)pos.getX(),(int)pos.getY());
        }else{
            mat.setExplored((int)pos.getX(),(int)pos.getY());
        }
    }

    private static Tile navigation(BaMMatrix mat, Tile pos){
        // finds unexplored tile with most walls around
        Tile best = mat.bestUnexploredNeighbor((int)pos.getX(),(int)pos.getY());
        if(best != null){
            mat.setLastPos(best);
            return best;
        }
        // finds random already visited tile
        best = mat.rndExploredNeighbor((int)pos.getX(),(int)pos.getY());
        if(best != null){
            mat.setLastPos(best);
            return best;
        }
        // if both are null, the whole map has been explored, so we reset it to continue exploring and repeat the process
        mat.resetMap();
        System.out.println("RESET MAP");
        return navigation(mat, pos);
    }
}
