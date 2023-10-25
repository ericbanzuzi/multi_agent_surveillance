package ui;

import agent.Agent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import map.Vector2d;

public class DrawableAgent extends Circle {

    private Agent agent;
    private Color color;
    private static final int SCALE = MenuControllerWindow.SCALE;

    public DrawableAgent(double centerX, double centerY, double radius, Color color, Agent agent){
        super(centerX*SCALE, centerY*SCALE, radius, color);
        this.color = color;
        this.agent = agent;
    }

    public void updatePosition(){
        this.setCenterX(agent.getPosition().getX()*SCALE);
        this.setCenterY(agent.getPosition().getY()*SCALE);
    }

    public Arc getVision(){
        Arc vision = new Arc();
        vision.setType(ArcType.ROUND);
        if(this.agent.getW().getY() > 0){
            vision.setStartAngle(-Math.toDegrees(this.agent.getW().getAngleTo(new Vector2d(1, 0))) -
                    Math.toDegrees(this.agent.getTheta() / 2));
        } else {
            vision.setStartAngle(Math.toDegrees(this.agent.getW().getAngleTo(new Vector2d(1, 0))) -
                    Math.toDegrees(this.agent.getTheta() / 2));
        }
        vision.setLength(Math.toDegrees(this.agent.getTheta()));
        vision.setCenterX(this.getCenterX());
        vision.setCenterY(this.getCenterY());
        vision.setRadiusX(this.getRadius() + this.agent.getD()*SCALE);
        vision.setRadiusY(this.getRadius() + this.agent.getD()*SCALE);
        vision.setFill(this.color);
        vision.setOpacity(0.2);
        return vision;
    }

    public Agent getAgent() {
        return agent;
    }

}
