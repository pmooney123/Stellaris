import java.awt.*;

public class Projectile {
    protected double x;
    protected double y;
    protected int team_ID;
    protected int speed;
    protected int accel = 0;
    protected double angle =0;
    protected double tx;
    protected double ty;
    protected int age = 0;
    protected int life_span = 400;
    protected int hit_radius = 2;
    protected Color color = Color.GRAY;
    protected int size = 2;
    protected boolean flagForRemoval = false;
    public Projectile(double x, double y, double tx, double ty, int speed, int team_ID) {
        this.x = x;
        this.y = y;
        this.ty = ty;
        this.tx = tx;
        this.speed = speed;
        //this.team_ID = team_ID;
        this.team_ID = team_ID;
        double diff_x = tx - this.x;
        double diff_y = ty - this.y;
        this.angle = Math.atan2(diff_y, diff_x) + Game.rand.nextInt(11) / 100.0 - Game.rand.nextInt(6) / 100.0;
    }
}
