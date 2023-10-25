package agent.sensation;

import map.Vector2d;

public class Sector {
    //
    private final static double ARC_SPACING = 1;
    private int nrOfLineSegments;
    private Vector2d location;
    private double theta;
    private double vDistance;
    // direction vector,
    // independent on the location,
    // e.g. (1,0) means to the north
    // will be scaled to the distance as its magnitude
    private Vector2d w;

    // line segments: the vectors
    private Vector2d[] lineSegments;

    public Sector(Vector2d location, double theta, double vDistance, Vector2d w) {
        nrOfLineSegments = computeNrOfLineSegments(theta, vDistance);
        this.location = location;
        this.theta = theta;
        this.vDistance = vDistance;
        this.w = w;

        initialiseLineSegments();
        calculateLineSegments();
    }

    // with a constant of vision arc spacing,
    // we calculate the reasonable number of line segments
    // so that we cover almost all the items
    public static int computeNrOfLineSegments(double theta, double vDistance) {
        // get the arc length
        // divide it by the constant
        double arcLength = ((vDistance * vDistance * theta) / Math.PI);
        return (int) (arcLength / ARC_SPACING) + 1;
    }

    private void calculateLineSegments() {
        w = w.scaleTo(vDistance);
        // left most
        lineSegments[0] = w.rotateBy(theta / 2);
        double rotation = roundTo6dp(theta / (nrOfLineSegments-1));
        for (int i = 1; i < nrOfLineSegments; i++) {
            lineSegments[i] = lineSegments[i - 1].rotateBy(-rotation);
        }
        for (int i = 0; i < nrOfLineSegments; i++) {
            lineSegments[i] = lineSegments[i].add(location);
        }

    }

    private double roundTo6dp(double num) {
        return Math.round(num * 1000000.0) / 1000000.0;
    }

    private void initialiseLineSegments() {
        // add one unit for storing w
        lineSegments = new Vector2d[nrOfLineSegments];
        for (int i = 0; i < nrOfLineSegments; i++) {
            lineSegments[i] = new Vector2d();
        }
    }


    public Vector2d[] getLineSegments() {
        return lineSegments;
    }

    @Override
    public String toString() {
        String lineXY = "";
        for (int i = 0; i < lineSegments.length; i++) {
            Vector2d tip = location.add(lineSegments[i]);
            lineXY = lineXY + tip.getX() + "," + tip.getY() + "\n";
        }
        return "Sector{" +
                "lineSegments=\n" +
                lineXY +
                '}';
    }
}
