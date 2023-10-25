package ui;

import agent.Guard;
import agent.Intruder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;


public class AgentFactory {

    private static final Color RED = Color.RED;
    private static final Color BLUE = Color.BLUE;
    private static final int SCALE = MenuControllerWindow.SCALE;

    protected List<DrawableAgent> createDrawableIntruders(List<Intruder> intruders) {

        ArrayList<DrawableAgent> agents = new ArrayList<>();
        for(Intruder intruder : intruders){
            DrawableAgent agent = new DrawableAgent(intruder.getPosition().getX(), intruder.getPosition().getY(), (double) SCALE/2, RED, intruder);
            agents.add(agent);
        }
        return agents;
    }

    protected List<DrawableAgent> createDrawableGuards(List<Guard> guards) {

        ArrayList<DrawableAgent> agents = new ArrayList<>();
        for(Guard guard : guards){
            DrawableAgent agent = new DrawableAgent(guard.getPosition().getX(), guard.getPosition().getY(), (double) SCALE/2, BLUE, guard);
            agents.add(agent);
        }
        return agents;
    }

}
