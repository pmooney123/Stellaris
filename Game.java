
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class Game implements ActionListener {
    public static Game game;
    public static Random rand = new Random();
    public final int WIDTH = 1400, HEIGHT = 1000;
    public static int XOFF = 0, YOFF = 0;
    public final int WIDTHINFO = 400;
    public static int MX = 0, MY = 0;
    public static boolean DOWN_HELD = false;
    public static boolean UP_HELD = false;
    public static boolean LEFT_HELD = false;
    public static boolean RIGHT_HELD = false;
    public static boolean G_HELD = false;
    public static Renderer renderer;
    public static String input = "null";
    public static int count = 0;
    public static long timeElapsed = 0;
    public static ArrayList<Ship> allShips = new ArrayList<>();
    public static ArrayList<Ship> renderedShips = new ArrayList<>();
    public static ArrayList<Ship> selectedShips = new ArrayList<>();
    public static int boxx1; int boxx2; int boxy1; int boxy2;
    public static ArrayList<Ship> fleet1 = new ArrayList<>();
    public static ArrayList<Ship> fleet2 = new ArrayList<>();
    public static int SCATTER = 33;
    public static ArrayList<Projectile> projectileArrayList = new ArrayList<>();

    public static boolean drawingSelector = false;

    public Game() {

        JFrame jframe = new JFrame();
        jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jframe.setUndecorated(true);
        jframe.setVisible(true);
        Timer timer = new Timer(20, this);
        renderer = new Renderer();
        jframe.setSize(WIDTH + WIDTHINFO, HEIGHT);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
        jframe.setResizable(false);

        jframe.add(renderer);
        addKeyListenerHere(jframe);
        addMouseListenerHere(jframe);
        timer.start();
        spawnShips();
        jframe.setFocusable(true);

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        jframe.getContentPane().setCursor(blankCursor);

    }

    public static void main(String[] args) {
        game = new Game();
    }
    public void repaint(Graphics g) {
        Instant start = Instant.now();
        g.setFont(new Font("Monospaced",Font.BOLD,  11));
        statsUpdate(start);
        paintBackground(g);
        paintRenderedShips(g);
        paintInfoPane(g);
        updateRenderedShips();
        drawCursor(g);
        weaponsManager(g);
        drawProjectiles(g);
        drawSelectBox(g);
        moveShips();
        Instant finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
    }
    public void weaponsManager(Graphics g) {
        for (int j = 0; j < renderedShips.size() - 1; j++) {
            if (rand.nextInt(5) == 0) {
                projectileArrayList.add(new Projectile(renderedShips.get(j).x, renderedShips.get(j).y, renderedShips.get(j + 1).x, renderedShips.get(j + 1).y, 15, renderedShips.get(j).team_ID));
            }
        }
        for (Projectile ship : projectileArrayList) {
            ship.age++;
            for (int z = 0; z < ship.speed; z++) {
                ship.speed = ship.speed + ship.accel;
                if (ship.age > ship.life_span) {
                    ship.flagForRemoval = true;
                }
                double vect_x = 1 * Math.cos(ship.angle);
                double vect_y = 1 * Math.sin(ship.angle);
                ship.x = ship.x + vect_x;
                ship.y = ship.y + vect_y;
                for (Ship ship2 : renderedShips) {
                    if (checkWithinRadius(ship.x, ship.y, ship2.x, ship2.y, ship.hit_radius)) {

                        if (ship.team_ID != ship2.team_ID) {
                            System.out.println("Projectile from team " + ship.team_ID + " has collided with ship from team " + ship2.team_ID);
                            g.setColor(Color.YELLOW);
                            g.drawOval((int) ship2.x - 5 - XOFF, (int) ship2.y - 5 - YOFF, 10, 10);
                            ship2.health--;
                        }
                    }
                }
            }
        }

        for (int j = 0; j < projectileArrayList.size(); j++) {
            if (projectileArrayList.get(j).flagForRemoval) {
                projectileArrayList.remove(j);
                j--;
            }
        }

    }
    public void drawProjectiles(Graphics g) {
        for (Projectile projectile : projectileArrayList) {
            g.setColor(projectile.color);
            g.fillRect((int)  projectile.x - XOFF, (int) projectile.y - YOFF, projectile.size, projectile.size);
        }
    }
    public void paintInfoPane(Graphics g) {
        g.setColor(Color.GRAY.darker());
        g.fillRect(WIDTH, 0, WIDTHINFO, HEIGHT);

        g.setColor(Color.white);
        g.drawString("F1/F2 add to fleet, 1/2 select fleet, g -> move selected to mouse.",WIDTH + 10, 50);
        g.drawString("F3 clear fleets. 3 select ALL SHIPS. WASD to pan",WIDTH + 10, 70);
    }
    public void statsUpdate(Instant start) {
        count++;
        input = "default";
        if (true) {
            if (UP_HELD) {
                input = "w";
                YOFF += -10;
            }
            if (DOWN_HELD) {
                input = "s";
                YOFF += 10;
            }
            if (LEFT_HELD) {
                input = "d";
                XOFF += -10;
            }
            if (RIGHT_HELD) {
                input = "a";
                XOFF += 10;
            }
        } //handle key inputs
        MX = MouseInfo.getPointerInfo().getLocation().x;
        MY = MouseInfo.getPointerInfo().getLocation().y;
        for (Ship ship : allShips) {
            ship.color = Color.green;
            ship.team_ID = 0;
        }
        for (Ship ship : fleet1) {
            ship.color = Color.red;
            ship.team_ID = 1;
        }
        for (Ship ship : fleet2) {
            ship.color = Color.blue.brighter();
            ship.team_ID = 2;
        }
    } //tracks ping, count, resets inputs, processes key inputs
    public void drawCursor(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(MX, MY, 5, 5);
    }
    public void paintBackground(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.white);
        g.drawString(timeElapsed + " ms", 10, 50);
    }
    public void paintRenderedShips(Graphics g) {
        for (Ship ship : renderedShips) {
            g.setColor(ship.color);
            g.fillRect((int) ship.x - XOFF, (int) ship.y - YOFF, ship.size, ship.size);
        }
        g.setColor(Color.red.brighter());
        for (Ship ship : selectedShips) {
            g.drawRect((int) (ship.x - XOFF) - 2, (int) (ship.y - YOFF) - 2, 10, 10);
        }
    }
    public void drawSelectBox(Graphics g) {
        if (drawingSelector) {
            boxx2 = MX;
            boxy2 = MY;
            g.setColor(Color.red.darker());
            int quadrship = 0;
            if (boxx2 > boxx1) {
                if (boxy2 > boxy1) {
                    quadrship = 1;
                    g.drawRect(boxx1, boxy1, boxx2 - boxx1, boxy2 - boxy1);
                } else {
                    quadrship = 2;
                    g.drawRect(boxx1, boxy2, boxx2 - boxx1, boxy1 - boxy2);
                }
            } else {
                if (boxy2 > boxy1) {
                    g.drawRect(boxx2, boxy1, boxx1 - boxx2, boxy2 - boxy1);
                    quadrship = 3;
                } else {
                    g.drawRect(boxx2, boxy2, boxx1 - boxx2, boxy1 - boxy2);
                    quadrship = 4;
                }
            }
            g.setColor(Color.red.darker());
            selectedShips.clear();
            for (Ship ship : renderedShips) {
                if (quadrship == 1) {
                    if ((ship.x - XOFF) > boxx1 && (ship.x - XOFF) < boxx2 && (ship.y - YOFF) < boxy2 && (ship.y - YOFF) > boxy1) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrship == 2) {
                    if ((ship.x - XOFF) > boxx1 && (ship.x - XOFF)  < boxx2 && (ship.y - YOFF) < boxy1 && (ship.y - YOFF) > boxy2) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrship == 3) {
                    if ((ship.x - XOFF) > boxx2 && (ship.x - XOFF) < boxx1 && (ship.y - YOFF) < boxy2 && (ship.y - YOFF) > boxy1) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrship == 4) {
                    if ((ship.x - XOFF) > boxx2 && (ship.x - XOFF) < boxx1 && (ship.y - YOFF) < boxy1 && (ship.y - YOFF) > boxy2) {
                        selectedShips.add(ship);
                    }
                }
            }
        }
    }
    public void updateRenderedShips() {
        renderedShips.clear();
        for (Ship ship : allShips) {
            if (ship.x > (XOFF) && ship.x < WIDTH + XOFF && ship.y > YOFF && ship.y < HEIGHT + YOFF) {
                renderedShips.add(ship);
            }
        }
    }
    public void spawnShips() {
        int STARTING_SHIPS = 100;
        for (int z = 0; z < STARTING_SHIPS; z++) {
            allShips.add(new Ship(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
        }
    }
    public void moveShips() {
        for (Ship ship : allShips) {
            //angle
            /*
            double angle = ship.angle;
            while (ship.angle > (6.28 / 2) || ship.angle < (-6.28 / 2)) {
                if (ship.angle < 0) {
                    ship.angle += 6.28;
                } else {
                    ship.angle -= 6.28;
                }

            }
            double slope = (ship.ty - ship.y) / (ship.tx - ship.x);
            double needed_angle = Math.atan2(slope, 1);
            if (ship.tx < ship.x) {
                needed_angle += 3.14;
            }

            if (angle > needed_angle) {
                ship.angle -= 0.01;
            }
            if (angle < needed_angle) {
                ship.angle += 0.01;
            }

            double speed = 3;
            double vect_x = speed * Math.cos(angle);
            double vect_y = speed * Math.sin(angle);
            ship.x = ship.x + vect_x;
            ship.y = ship.y + vect_y;
             */
            double angle = ship.angle;
            if (ship.has_target) {
                double diff_x = ship.tx - ship.x;
                double diff_y = ship.ty - ship.y;
                angle = Math.atan2(diff_y, diff_x);
                double speed = ship.speed;
                double vect_x = speed * Math.cos(angle);
                double vect_y = speed * Math.sin(angle);
                ship.x = ship.x + vect_x;
                ship.y = ship.y + vect_y;

                if (checkWithinRadius(ship.x, ship.y, ship.tx, ship.ty, 2)) {
                    ship.has_target = false;
                }
            } else {
                if (ship.wander) {
                    ship.angle = ship.angle + ((rand.nextInt(41) - 20) / 100.0);
                    double speed = ship.speed;
                    double vect_x = speed * Math.cos(angle);
                    double vect_y = speed * Math.sin(angle);
                    ship.x = ship.x + vect_x;
                    ship.y = ship.y + vect_y;
                }
            }
            if (ship.health <= 0) {
                ship.flagForRemoval = true;
            }
        }
        for (int j = 0; j < allShips.size(); j++) {
            if (allShips.get(j).flagForRemoval) {
                allShips.remove(j);
                j--;
            }
        }
    }
    public boolean checkWithinRadius(double x, double y, double x2, double y2, double radius) {
        double distance = Math.sqrt(Math.pow((x2 - x), 2) + Math.pow((y2 - y), 2));
        return distance < radius;
    }
    public double getDistance(double x, double y, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x), 2) + Math.pow((y2 - y), 2));
    }
    public void addKeyListenerHere(JFrame jframe) {
        jframe.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                SCATTER = selectedShips.size() * 2 + 5;
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case (KeyEvent.VK_W) -> {
                        UP_HELD = true;
                    }
                    case (KeyEvent.VK_S) -> {
                        DOWN_HELD = true;
                    }
                    case (KeyEvent.VK_A) -> {
                        LEFT_HELD = true;
                    }
                    case (KeyEvent.VK_D) -> {
                        RIGHT_HELD = true;

                    }
                    case (KeyEvent.VK_G) -> {
                        G_HELD = true;

                    }
                    case (KeyEvent.VK_1) -> {
                        selectedShips.clear();
                        selectedShips.addAll(fleet1);
                    }
                    case (KeyEvent.VK_F1) -> {
                        fleet1.addAll(selectedShips);
                        for (Ship ship : fleet1) {
                            try {
                                fleet2.remove(ship);
                            } catch (Exception ignored) {

                            }

                        }
                    }
                    case (KeyEvent.VK_2) -> {
                        selectedShips.clear();
                        selectedShips.addAll(fleet2);
                    }
                    case (KeyEvent.VK_F2) -> {
                        fleet2.addAll(selectedShips);
                        for (Ship ship : fleet2) {
                            try {
                                fleet1.remove(ship);
                            } catch (Exception ignored) {

                            }
                        }
                    }
                    case (KeyEvent.VK_3) -> {
                        selectedShips.clear();
                        selectedShips.addAll(allShips);

                    }
                    case (KeyEvent.VK_F3) -> {
                        fleet2.clear();
                        fleet1.clear();
                    }
                    case (KeyEvent.VK_ENTER) -> {
                        for (Ship ship : selectedShips) {
                            ship.random = !ship.random;
                        }
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case (KeyEvent.VK_W) -> {
                        UP_HELD = false;
                    }
                    case (KeyEvent.VK_S) -> {
                        DOWN_HELD = false;
                    }
                    case (KeyEvent.VK_A) -> {
                        LEFT_HELD = false;
                    }
                    case (KeyEvent.VK_D) -> {
                        RIGHT_HELD = false;
                    }
                    case (KeyEvent.VK_G) -> {
                        G_HELD = false;
                    }
                }
            }

        });

    }
    public void addMouseListenerHere(JFrame jframe) {
        jframe.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                ;
            }
            @Override
            public void mousePressed(MouseEvent e) {
                drawingSelector = true;
                boxx1 = MX;
                boxy1 = MY;
                boxx2 = MX;
                boxy2 = MY;

                if (G_HELD) {
                    for (Ship ship : selectedShips) {
                        ship.tx = MX + XOFF + rand.nextInt(SCATTER) - rand.nextInt(SCATTER - 1 / 2);
                        ship.ty = MY + YOFF + rand.nextInt(SCATTER) - rand.nextInt(SCATTER - 1/ 2);
                        ship.has_target = true;
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {


                drawingSelector = false;
                /*
                double closest_distance = 100000;
                if (selectedShips.size() == 0) {
                    for (Ship ship : renderedShips) {

                        if (getDistance(MX, MY, ship.x, ship.y) < closest_distance) {
                            closest_distance = getDistance(MX, MY, ship.x, ship.y);
                            selectedShips.clear();
                            selectedShips.add(ship);
                        }
                    }
                }

                 */

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        renderer.repaint();

    }
}
