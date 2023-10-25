package map;

public class Vector2d {

    private double x;
    private double y;

    public Vector2d() {
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2d copy() {
        return new Vector2d(this.x, this.y);
    }

    public Vector2d scale(double num) {
        return new Vector2d(x * num, y * num);
    }

    public Vector2d getNorm() {
        return this.scaleTo(1);
    }

    public Vector2d scaleTo(double expectedMagnitude) {
        Vector2d scaled = this.copy();
        return scaled.scale(expectedMagnitude / this.getMagnitude());
    }

    public void addToX(double num) {
        this.x = x + num;
    }

    public void addToY(double num) {
        this.y = y + num;
    }

    public void add(double num) {
        this.x = x + num;
        this.y = y + num;
    }

    public Vector2d add(Vector2d other) {
        return addMul(other, 1);
    }

    public Vector2d minus(Vector2d other) {
        return addMul(other, -1);
    }

    public Vector2d addMul(Vector2d other, double mul) {
        double xDot = x + other.x * mul;
        double yDot = y + other.y * mul;
        return new Vector2d(xDot, yDot);
    }

    public double getDotProduct(Vector2d other) {
        return this.x * other.x + this.y * other.y;
    }

    public double cross(Vector2d other) {
        return this.x * other.y - this.y * other.x;
    }


    public double getMagnitude() {
        return Math.sqrt(
                Math.pow(this.x, 2) + Math.pow(this.y, 2)
        );
    }

    public double getDistanceTo(Vector2d other) {
        return Math.sqrt(
                Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2)
        );
    }

    public double getAngleTo(Vector2d other) {
        return roundTo3dp(Math.acos(
                this.getDotProduct(other) / (this.getMagnitude() * other.getMagnitude())
        ));
    }

    /**
     * rotate the vector counterclockwise with an angle
     *
     * @param angle rotating angle
     * @return rotated vector
     */
    public Vector2d rotateBy(double angle) {
        double x2 = roundTo6dp(Math.cos(angle)* this.x - Math.sin(angle) * this.y);
        double y2 = roundTo6dp(Math.sin(angle) * this.x+ Math.cos(angle) * this.y);
        return new Vector2d(x2, y2);
    }

    private double roundTo6dp(double num) {
        return Math.round(num * 1000000.0) / 1000000.0;
    }
    public void roundVectorTo3dp(){
        this.x = roundTo3dp(this.x);
        this.y = roundTo3dp(this.y);
    }
    private double roundTo3dp(double num) {
        return Math.round(num * 1000.0) / 1000.0;
    }


    @Override
    public String toString() {
        return "[" + this.getX() + "," + this.getY() + "]";
    }

    @Override
    public boolean equals(Object o){
        if(((Vector2d)o).getX() == x && ((Vector2d)o).getY() == y){
            return true;
        }
        return false;
    }
}