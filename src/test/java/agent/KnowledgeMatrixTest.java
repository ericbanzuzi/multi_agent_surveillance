package agent;

import controller.GameRunner;
import map.Map;
import map.scenario.Area;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class KnowledgeMatrixTest {

    @Test
    void createAreasTest(){
        String mapD= GameRunner.path1;
        GameRunner game = new GameRunner(mapD);
        Map map = game.getScene().getMap();
        KnowledgeMatrix k = new KnowledgeMatrix(map, false, 1);

        ArrayList<Area> areas = k.getAreas();
        for (Area a: areas) {
            if(a.getLeftBoundary() < 0) {
                System.out.println("left: " + a.getLeftBoundary());
                assert(false);
            }
            if(a.getTopBoundary() < 0){
                System.out.println("top: " + a.getTopBoundary());
                assert (false);
            }
            if(a.getRightBoundary() >= map.getWidth()) {
                System.out.println("right: " + a.getLeftBoundary());
                assert (false);
            }
            if(a.getBottomBoundary() >= map.getHeight()) {
                System.out.println("bottom: " + a.getBottomBoundary());
                assert(false);
            }
        }
        assert true;
    }

}