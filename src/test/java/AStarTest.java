import Exceptions.NoPathException;
import agent.explorer.AStar;
import agent.explorer.Vertex;
import controller.GameController;
import controller.GameRunner;
import map.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;

public class AStarTest {

    @Test
    void neighborStartEndTest(){
        Vertex start = new Vertex(new Tile(0,0, Tile.Type.DEFAULT));
        Vertex end = new Vertex(new Tile(0,1, Tile.Type.DEFAULT));
        start.addNeighbor(end);
        LinkedList<Vertex> path = null;
        try{
            path = AStar.path(start,end);
            assert(true);
        }catch (NoPathException e){
            assert(false);
        }
        //path.forEach(vertex -> System.out.println("[" + vertex.getTile().getX() + "," + vertex.getTile().getY() + "]"));
        Assertions.assertEquals(end,path.get(path.size()-1));
    }

    @Test
    void StartIsEndTest(){
        Vertex start = new Vertex(new Tile(0,0, Tile.Type.DEFAULT));
        LinkedList<Vertex> path = null;
        try{
            path = AStar.path(start,start);
            assert(true);
        }catch (NoPathException e){
            assert(false);
        }
        //path.forEach(vertex -> System.out.println("[" + vertex.getTile().getX() + "," + vertex.getTile().getY() + "]"));
        Assertions.assertEquals(start,path.get(path.size()-1));
    }

    @Test
    void GeneralTest(){
        //creating 3x3 Grid graph
        Vertex start = new Vertex(new Tile(0,0, Tile.Type.DEFAULT));
        Vertex v01 = new Vertex(new Tile(0,1, Tile.Type.DEFAULT));
        Vertex v10 = new Vertex(new Tile(1,0, Tile.Type.DEFAULT));
        Vertex v11 = new Vertex(new Tile(1,1, Tile.Type.DEFAULT));
        Vertex v02 = new Vertex(new Tile(0,2, Tile.Type.DEFAULT));
        Vertex v12 = new Vertex(new Tile(1,2, Tile.Type.DEFAULT));
        Vertex v20 = new Vertex(new Tile(2,0, Tile.Type.DEFAULT));
        Vertex v21 = new Vertex(new Tile(2,1, Tile.Type.DEFAULT));
        Vertex end = new Vertex(new Tile(2,2, Tile.Type.DEFAULT));

        start.addNeighbor(v01);
        start.addNeighbor(v10);
        v01.addNeighbor(v02);
        v01.addNeighbor(v11);
        v02.addNeighbor(v12);
        v10.addNeighbor(v11);
        v10.addNeighbor(v20);
        v20.addNeighbor(v21);
        v11.addNeighbor(v21);
        v11.addNeighbor(v12);
        end.addNeighbor(v21);
        end.addNeighbor(v12);

        start.addNeighbor(end);
        LinkedList<Vertex> path = null;
        try{
            path = AStar.path(start,end);
            assert(true);
        }catch (NoPathException e){
            assert(false);
        }
        path.forEach(vertex -> System.out.println("[" + vertex.getTile().getX() + "," + vertex.getTile().getY() + "]"));
        Assertions.assertEquals(end,path.get(path.size()-1));
    }

    @Test
    void MapTest(){
        //Preparation
        String mapD= GameRunner.path1;
        GameRunner game = new GameRunner(mapD);
        Vertex[][] graph = game.getScene().getMap().toGraph();
        Vertex start = graph[1][1];
        Vertex end = graph[50][80];

        //A* stuff
        LinkedList<Vertex> path = null;
        try{
            path = AStar.path(start,end);
            assert(true);
        }catch (NoPathException e){
            e.printStackTrace();
            assert(false);
        }
        path.forEach(vertex -> System.out.println("[" + vertex.getTile().getX() + "," + vertex.getTile().getY() + "]"));
        Assertions.assertEquals(end,path.get(path.size()-1));

        start = graph[2][1];
        end = graph[50][80];
        //A* second test
        try{
            path = AStar.path(start,end);
            assert(true);
        }catch (NoPathException e){
            e.printStackTrace();
            assert(false);
        }
        Assertions.assertEquals(end,path.get(path.size()-1));
    }
}
