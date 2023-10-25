package map;


public class Tile extends Vector2d {

    public enum Type {
        DEFAULT(0),
        SHADED(0),
        WALL(Integer.MAX_VALUE),
        TELEPORT(1),
        UNEXPLORED(0),
        TARGET(0);

        int weight;

        Type(int i) {
            this.weight = i;
        }

        public int getWeight() {
            return weight;
        }
    }

    public enum Marker {
        DEFAULT(0),
        TELEPORT(Integer.MAX_VALUE),
        PHEROMONEINTRUDER(Integer.MAX_VALUE),
        PHEROMONEGUARD(Integer.MIN_VALUE),
        YELLINTRUDER(Integer.MAX_VALUE),
        YELLGUARD(Integer.MIN_VALUE);

        int weight;

        Marker(int i) {
            this.weight = i;
        }

        public int getWeight() {
            return weight;
        }
    }

    private Type type;
    private Marker marker;
    private int weight;
    private Vector2d teleportGoal;

    public Tile(int x, int y, Type type) {
        super(x, y);
        this.type = type;
        this.weight = type.weight;
        this.marker = Marker.DEFAULT;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if (type == Type.UNEXPLORED) {
            throw new UnsupportedOperationException("Type.UNEXPLORED is only used as a return value in VisionMatrix.getTypeOf()");
        }
        this.type = type;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        if (marker == null) {
            setMarker(Marker.DEFAULT);
        }
        return marker;
    }

    // helper for the markers
    public void addExtraWeight(int i) {
        weight += i;
    }

    public int getWeight() {
        return weight;
    }

    public Vector2d getTeleportGoal() {
        if (!this.getType().equals(Type.TELEPORT)) {
            System.out.println("This is not a TELEPORTER!!!");
            return null;
        }
        return teleportGoal;
    }

    public void setTeleportGoal(Vector2d goal) {
        if (!this.getType().equals(Type.TELEPORT)) {
            System.out.println("This is not a TELEPORTER!!!");
            return;
        }
        teleportGoal = goal;
    }


    @Override
    public String toString() {
        return "[" + this.getX() + "," + this.getY() + ": " + this.type + "]";
    }

    @Override
    public boolean equals(Object o){
        if(((Tile)o).getX() == super.getX() && ((Tile)o).getY() == super.getY()){
            return true;
        }
        return false;
    }
}
