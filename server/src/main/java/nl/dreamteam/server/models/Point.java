package nl.dreamteam.server.models;

public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}