//import Exceptions.NoPathException;
//import agent.Agent;
//import agent.Guard;
//import agent.explorer.AStar;
//import agent.explorer.Lee;
//import agent.explorer.Vertex;
//import controller.GameRunner;
//import map.Map;
//import map.Tile;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//
//public class LeeTest {
//
//    @Test
//    void MapStepTest(){
//        //Preparation
//        String mapD= "src/main/assets/examinermap_phase1.txt";
//        GameRunner game = new GameRunner(mapD);
//        Map map = new Map(game.getScene());
//        Agent a = game.getController().getGameState().getGuards().get(0);
//
//        //Lee stuff
//        Lee lee = new Lee(map,a);
//        lee.step();
//    }
//
//    @Test
//    void MapTest(){
//        //Preparation
//        String mapD= "src/main/assets/examinermap_phase1.txt";
//        GameRunner game = new GameRunner(mapD);
//        Map map = new Map(game.getScene());
//        Agent a = game.getController().getGameState().getGuards().get(0);
//
//        //Lee stuff
//        Lee lee = new Lee(map,a);
//        while(!lee.isSomewhatExplored()){
//            lee.step();
//        }
//        System.out.println("Done");
//    }
//}
