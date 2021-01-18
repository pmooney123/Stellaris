import java.awt.*;

public class Ship {
    protected double x;
    protected double y;
    protected int size = 5;
    protected Color color = Color.green;

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
