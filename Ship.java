import java.awt.*;

public class Ship {
    protected double x;
    protected double y;
    protected int size = 5;
    protected Color color = Color.green;
    protected double angle = 0;
    protected boolean random = false;
    protected double speed = 2;
    protected boolean has_target = false;
    protected boolean wander = false;
    protected int tx;
    protected int ty;
    protected int team_ID = 0;
    protected int health = 10;
    protected boolean flagForRemoval =false;
    public Ship (double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Ship (double x, double y, Color color, int size) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;

    }
}
