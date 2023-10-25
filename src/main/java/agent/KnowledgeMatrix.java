package agent;

import Exceptions.NoPathException;
import agent.explorer.AStar;
import agent.explorer.Vertex;
import agent.sensation.VisionMatrix;
import controller.GameRunner;
import map.Map;
import map.Tile;
import map.Vector2d;
import map.scenario.Area;

import java.util.*;

public class KnowledgeMatrix {

    private final Map MAP;
    private final int MAX_KNOWLEDGE = 10000;
    private final int RATE_OF_DECAY = 1;
    private final int LOCAL_DECISION_RANGE = 10;
    private final double DECISION_TRESHOLD = 0.80;

    private final boolean DECAY;
    private final int height;
    private final int width;
    private final int[][] matrix;
    private Tile position;

    private boolean followWingPath;
    private LinkedList<Vertex> path;
    private List<Vertex> tempPath = new LinkedList<>();

    public ArrayList<Area> getAreas() {
        return areas;
    }

    private ArrayList<Area> areas;

    public KnowledgeMatrix(Map map, boolean decay, int cas) {
        this.MAP = map;
        this.DECAY = decay;
        this.height = map.getHeight();
        this.width = map.getWidth();
        this.matrix = new int[height][width];
        createAreas();
        this.followWingPath = false;
        this.path = new LinkedList<>();
        if (cas != -1) {
            //System.out.println("case: "+ cas);
            setInitialKnowledge(cas);
        }
    }

    private void createAreas() {
        areas = new ArrayList<>();
        int num_areas = 5;
        int areaWidth = 10;
        int areaHeight = 10;

        for (int i = 0; i < width -1; i += areaWidth) {
            int x2;
            if (i < width - areaWidth) x2 = i + areaWidth;
            else x2 = width -1;
            for (int j = 0; j < height -1; j += areaHeight) {
                int y2;
                if (j < height - areaHeight) y2 = j + areaHeight;
                else y2 = height -1;
                areas.add(new Area(i, j, x2, y2));
            }
        }
    }

    public void setInitialKnowledge(int cas){
        int c = cas % 4;
        int left, right, top, bottom, left2, right2, top2, bottom2;
        switch (c){
            case 0:
                left = width/2; right = width; top = 0; bottom = height;
                left2 = 0; right2 = width; top2 = 0; bottom2 = height/2; break;
            case 1:
                left = width/2; right = width; top = 0; bottom = height;
                left2 = 0; right2 = width; top2 = height/2; bottom2 = height; break;
            case 2:
                left = 0; right = width; top = 0; bottom = height/2;
                left2 = 0; right2 = width/2; top2 = 0; bottom2 = height; break;
            case 3:
                left = 0; right = width; top = height/2; bottom = height;
                left2 = 0; right2 = width/2; top2 = 0; bottom2 = height; break;
            default:
                left = 0; right = 0; top = 0; bottom = 0;
                left2 = 0; right2 = 0; top2 = 0; bottom2 = 0; break;
        }
        setMaxKnowledge(left, right, top ,bottom);
        setMaxKnowledge(left2, right2, top2, bottom2);
    }
    public void setMaxKnowledge(int left, int right, int top, int bottom){
        for (int i = left; i < right; i++) {
            for (int j = top; j < bottom; j++) {
                matrix[j][i] = MAX_KNOWLEDGE;
            }
        }
    }


    public void explore(Tile t) {
        matrix[(int) t.getY()][(int) t.getX()] = MAX_KNOWLEDGE;
    }

    public void decay_knowledge() {
        if(DECAY){
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width ; j++) {
                    if (matrix[i][j] != 0) matrix[i][j] -= RATE_OF_DECAY;
                }
            }
        }
    }

    public Vector2d decision(Tile position, boolean avoidOtherGuard){
        if (DECAY)decay_knowledge();
        this.position = position;
        Vector2d positionChange;

        if(path == null || path.isEmpty()) followWingPath = false;

        if (followWingPath) {
            // temp path only gets used in investigation
            if(!validMove(path.get(0).getTile())) {
                if(tempPath.isEmpty()) {
                    tempPath = findPath(path.get(0).getTile());
                    return tempPath.remove(0).getTile();
                }else {
                    return tempPath.remove(0).getTile();
                }
            }
            tempPath.clear();
            return path.remove(0).getTile();
        }
        else positionChange = localDecision();

        if (positionChange == null || avoidOtherGuard) {
            followWingPath = true;
            path = globalDecision();
            return path.remove(0).getTile();
        }

        return position.add(positionChange);
//        if(decision[2] > DECISION_TRESHOLD) return decision;
//        return globalDecision(position);

    }

    private LinkedList<Vertex> globalDecision() {
        //System.out.println("Using global heuristic");
        double scaledKnowledge = 10;
        int[] target = new int[]{1, 1} ;
        try {
            double time = System.currentTimeMillis();
            for (Area a : areas) {

                double distance = Math.sqrt(Math.pow(a.getCenter()[0] - position.getX(), 2) + Math.pow(a.getCenter()[1] - position.getY(), 2));
                if(distance >= 5){
                    double scaledKnowledgeTemp = getKnowledge(a)/ Math.sqrt(distance);
                    if (scaledKnowledgeTemp < scaledKnowledge) {
                        scaledKnowledge = getKnowledge(a)/ Math.sqrt(distance);
                        target = a.getCenter();
                    }
                }

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("FIUUUUUUUUUUUUCK");
        }
        //System.out.println("Current position: " + this.position);
        //System.out.println("Target positoin: "+ Arrays.toString(target));
        LinkedList<Vertex> path = null;
        try {
            Vertex[][] graph = MAP.toGraph();
            Vertex start = graph[(int) position.getY()][(int) position.getX()];
            Vertex end = graph[target[1]][target[0]];

            try {
//                System.out.println("Positoin: [" + (int) position.getY() + ", " +(int) position.getX() + "]");
//                System.out.println("Target: [" + target[1] + ", " + target[0] + "]");
                path = AStar.path(start, end);

            } catch (NoPathException e) {
                e.printStackTrace();
                assert (false);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Happens in graph");
        }
        if (path == null){
            return randomMove();
        }
        if(path.size() <=1) return randomMove();
        path.remove(0);
        return path;

    }

    private LinkedList<Vertex> randomMove(){

        Random rand = new Random();
        VisionMatrix visionMatrix = new VisionMatrix(MAP);
        List<Tile> neighbors = visionMatrix.getNeighborsOf(position);

        while(true) {
            int randomIndex = rand.nextInt(neighbors.size());
            Tile t = neighbors.get(randomIndex);
            if(!t.getType().equals(Tile.Type.WALL) || !MAP.getTileAt((int)t.getX(),(int) t.getY()).getType().equals(Tile.Type.WALL)) {
                LinkedList<Vertex> list = new LinkedList<>();
                list.add(new Vertex(t));
                return list;
            };
        }
    }

    private double getKnowledge(Area a) {
        double  area= 0;
        double sum = 0;
        for (int i = a.getLeftBoundary(); i < a.getRightBoundary(); i++) {
            for (int j = a.getTopBoundary(); j < a.getBottomBoundary(); j++) {
                area++;
                sum += matrix[j][i];
            }
        }
        return sum/(area*MAX_KNOWLEDGE);
    }

    public Vector2d localDecision(){
        int x = (int) position.getX();
        int y = (int) position.getY();

        int left = (int) Math.max(x - LOCAL_DECISION_RANGE, 0);
        if ( x!=0 && MAP.getTileAt(x-1,y).getType().equals(Tile.Type.WALL)) left = x;
        int right = (int) Math.min(x + LOCAL_DECISION_RANGE, width - 1);
        if ( x != width-1 && MAP.getTileAt(x+1, y).getType().equals(Tile.Type.WALL)) right = x;
        int top = (int) Math.max(y - LOCAL_DECISION_RANGE, 0);
        if ( y!= 0 && MAP.getTileAt(x, y-1).getType().equals(Tile.Type.WALL)) top = y;
        int bottom = (int) Math.min(y + LOCAL_DECISION_RANGE, height -1);
        if ( y!= height-1 &&MAP.getTileAt(x, y+1).getType().equals(Tile.Type.WALL)) bottom = y;

        double knowledgeLeft;
        if (MAP.getTileAt(x-1, y).getType().equals(Tile.Type.WALL)) knowledgeLeft = 1;
        else knowledgeLeft = getKnowledge(left, x, y, y,1);

        double knowledgeRight;
        if (MAP.getTileAt(x +1, y).getType().equals(Tile.Type.WALL)) knowledgeRight = 1;
        else knowledgeRight = getKnowledge(x, right, y, y,2);

        double knowledgeTop;
        if (MAP.getTileAt(x, y -1).getType().equals(Tile.Type.WALL)) knowledgeTop = 1;
        else knowledgeTop = getKnowledge(x, x, top, y,3);

        double knowledgeBottom;
        if (MAP.getTileAt(x, y +1).getType().equals(Tile.Type.WALL)) knowledgeBottom = 1;
        else knowledgeBottom = getKnowledge(x, x, y, bottom,4);

        if(tooMuchKnowledge(knowledgeLeft, knowledgeRight, knowledgeTop, knowledgeBottom)) return null;
        return getDecision(knowledgeLeft, knowledgeRight, knowledgeTop, knowledgeBottom);

    }

    private boolean tooMuchKnowledge(double knowledgeLeft, double knowledgeRight, double knowledgeTop, double knowledgeBottom) {
        double h = DECISION_TRESHOLD;
        return (knowledgeLeft > h) && (knowledgeRight > h) && (knowledgeTop > h) && (knowledgeBottom > 0.8);
    }

    /**
     * calculates the (normalized) knowledge in the area between the given boundaries
     * @param left  left boundary
     * @param right right boundary
     * @param top   top boundary
     * @param bottom bottom boundary
     * @return  knowledge (average knowledge divided by the area)
     */
    public double getKnowledge(int left, int right, int top, int bottom, int direction){
        int area = 0;
//        System.out.println("Right = " + right + ", left = " + left + ",bottom = " + bottom + ",top = " + top);
        double sum = 0;
        switch (direction){
            //left
            case 1:
                for (int i = top; i <= bottom; i++) {
                    //right to left
                    for (int j = right; j >= left ; j--) {
                        if(MAP.getTileAt(j, i).getType().equals(Tile.Type.WALL)) {
//                            System.out.println("sum: " + sum + ", area: " + area);
//                            System.out.println("Breaks: " + sum/((double)(area*MAX_KNOWLEDGE)));
                            return sum/((double)(area*MAX_KNOWLEDGE));
                        }
                        sum += matrix[i][j];
                        area++;
                    }
                }
                break;
            //right (same as bottom)
            case 2:
            //bottom
            case 4:
                //top to bottom
                for (int i = top; i <= bottom; i++) {
                    //right to left
                    for (int j = left; j <= right ; j++) {
                        if(MAP.getTileAt(j, i).getType().equals(Tile.Type.WALL)) {
//                            System.out.println("sum: " + sum + ", area: " + area);
//                            System.out.println("Breaks: " + sum/((double)(area*MAX_KNOWLEDGE)));
                            return sum/((double)(area*MAX_KNOWLEDGE));
                        }
                        sum += matrix[i][j];
                        area++;
                    }
                }
                break;
            //top
            case 3:
                // bottom to top
                for (int i = bottom; i >= top; i--) {
                    for (int j = left; j <= right ; j++) {
                        if(MAP.getTileAt(j, i).getType().equals(Tile.Type.WALL)) {
//                            System.out.println("sum: " + sum + ", area: " + area);
//                            System.out.println("Breaks: " + sum/((double)(area*MAX_KNOWLEDGE)));
                            return sum/((double)(area*MAX_KNOWLEDGE));
                        }
                        sum += matrix[i][j];
                        area++;
                    }
                }
                break;
            //wrong
            default:
                System.out.println("Wrong parameter!!!!!");
        }

//        System.out.println(sum/(area*MAX_KNOWLEDGE));
//        System.out.println("area = " + area);
        double normalized = sum/((double)(area*MAX_KNOWLEDGE));
        //System.out.println(normalized);
        if(area != 0) return normalized;
//        System.out.println("returning 2");
        return 2;
    }

    /**
     *  returns the direction based on which quadrant near the agent has the lowest knowledge
     * @param knowledgeLeft
     * @param knowledgeRight
     * @param knowledgeTop
     * @param knowledgeBottom
     * @return
     */
    public Vector2d getDecision(double knowledgeLeft, double knowledgeRight, double knowledgeTop, double knowledgeBottom){
        double[] list = new double[]{knowledgeLeft, knowledgeRight, knowledgeTop, knowledgeBottom};
        checkWalls(list);
        int pos = 0;
        double min = list[0];
        for (int i = 1; i <list.length ; i++) {
            if (list[i] < min
                    || (list[i] == min && Math.random() < 0.5)){
                min = list[i];
                pos = i;
            }
        }
        return switch (pos) {
            case 0 -> new Vector2d(-1, 0);
            case 1 -> new Vector2d(1, 0);
            case 2 -> new Vector2d(0, -1);
            case 3 -> new Vector2d(0, 1);
            default -> new Vector2d(-1, 0);
        };
    }

    public void checkWalls(double[] list){
        int x = (int) position.getX();
        int y = (int) position.getY();
        if (MAP.getTileAt(x-1, y).getType().equals(Tile.Type.WALL)) list[0] = 1;
        if (MAP.getTileAt(x + 1, y).getType().equals(Tile.Type.WALL)) list[1] = 1;
        if (MAP.getTileAt(x, y -1).getType().equals(Tile.Type.WALL)) list[2] = 1;
        if (MAP.getTileAt(x, y + 1).getType().equals(Tile.Type.WALL)) list[3] = 1;
    }

    public List<Vertex> findPath(Tile goal){
        int[] target = new int[]{(int)goal.getX(), (int)goal.getY()};
        LinkedList<Vertex> path = null;
        try {
            Vertex[][] graph = MAP.toGraph();
            Vertex start = graph[(int) position.getY()][(int) position.getX()];
            Vertex end = graph[target[1]][target[0]];

            try {
                path = AStar.path(start, end);
            } catch (NoPathException e) {
                e.printStackTrace();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Happens in graph");
        }
        if (path == null){
            return randomMove();
        }
        if(path.size() <=1) return randomMove();
        path.remove(0);
        return path;

    }

    public boolean validMove(Tile goal){
        double[] xs = new double[]{this.position.getX()-1, this.position.getX()-1, this.position.getX()-1, this.position.getX(), this.position.getX(),
                this.position.getX()+1, this.position.getX()+1, this.position.getX()+1, this.position.getX()};
        double[] ys = new double[]{this.position.getY()-1, this.position.getY(), this.position.getY()+1, this.position.getY()-1, this.position.getY()+1,
                this.position.getY()-1, this.position.getY(), this.position.getY()+1, this.position.getY()};
        for(double x : xs){
            for(double y: ys){
                if(goal.getX() == x && goal.getY() == y) {
                    return true;
                }
            }
        }
        return false;
    }

//    public int[] globalDecision(Tile position){
//
//    }
}
