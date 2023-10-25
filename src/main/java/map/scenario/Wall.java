package map.scenario;

public class Wall extends Area {

    public Wall() {
        super();
    }

    public Wall(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    public boolean inWall(double x, double y) {
        boolean tmp = false;
        if (isHit(x, y)) {
            tmp = true;
        }
        return (tmp);
    }

    // TODO: methods
}

