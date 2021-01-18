import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class Game implements ActionListener {
    public static Game game;
    public static Random rand = new Random();
    public final int WIDTH = 1800, HEIGHT = 1000;
    public static int XOFF = 0, YOFF = 0;

    public static int MX = 800, MY = 400;
    public static boolean DOWN_HELD = false;
    public static boolean UP_HELD = false;
    public static boolean LEFT_HELD = false;
    public static boolean RIGHT_HELD = false;
    public static Renderer renderer;
    public static String input = "null";
    public static int count = 0;
    public static long timeElapsed = 0;
    public static ArrayList<Ship> allShips = new ArrayList<>();
    public static ArrayList<Ship> renderedShips = new ArrayList<>();
    public static ArrayList<Ship> selectedShips = new ArrayList<>();
    public static int boxx1; int boxx2; int boxy1; int boxy2;
    public static boolean drawingSelector = false;

    public Game() {
        JFrame jframe = new JFrame();

        Timer timer = new Timer(20, this);
        renderer = new Renderer();
        jframe.setSize(WIDTH, HEIGHT);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
        jframe.setResizable(false);
        jframe.add(renderer);
        addKeyListenerHere(jframe);
        addMouseListenerHere(jframe);
        timer.start();
        spawnShips();

        JTextField enterBox = new JTextField();
        enterBox.setSize(100, 20);
        enterBox.setLocation(100, 900);
        enterBox.setBackground(Color.white);
        enterBox.setForeground(Color.black);
        jframe.add(enterBox);

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

        statsUpdate(start);
        paintBackground(g);
        paintRenderedShips(g);
        updateRenderedShips();
        drawCursor(g);
        drawSelectBox(g);
        Instant finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
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
            g.drawRect((int) ship.x - 2, (int) ship.y - 2, 10, 10);
        }
    }
    public void drawSelectBox(Graphics g) {
        if (drawingSelector) {
            boxy2 = MY;
            boxx2 = MX;
            g.setColor(Color.red.darker());
            int quadrant = 0;
            if (boxx2 > boxx1) {
                if (boxy2 > boxy1) {
                    g.drawRect(boxx1, boxy1, boxx2 - boxx1, boxy2 - boxy1);
                    quadrant = 1;
                } else {
                    g.drawRect(boxx1, boxy2, boxx2 - boxx1, boxy1 - boxy2);
                    quadrant = 2;
                }
            } else {
                if (boxy2 > boxy1) {
                    g.drawRect(boxx2, boxy1, boxx1 - boxx2, boxy2 - boxy1);
                    quadrant = 3;
                } else {
                    g.drawRect(boxx2, boxy2, boxx1 - boxx2, boxy1 - boxy2);
                    quadrant = 4;
                }
            }
            g.setColor(Color.red.darker());
            g.drawRect(boxx1, boxy1, boxx2 - boxx1, boxy2 - boxy1);
            selectedShips.clear();
            for (Ship ship : renderedShips) {
                if (quadrant == 1) {
                    if (ship.x > boxx1 && ship.x < boxx2 && ship.y < boxy2 && ship.y > boxy1) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrant == 2) {
                    if (ship.x > boxx1 && ship.x < boxx2 && ship.y < boxy1 && ship.y > boxy2) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrant == 3) {
                    if (ship.x > boxx2 && ship.x < boxx1 && ship.y < boxy2 && ship.y > boxy1) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrant == 4) {
                    if (ship.x > boxx2 && ship.x < boxx1 && ship.y < boxy1 && ship.y > boxy2) {
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

    public void addKeyListenerHere(JFrame jframe) {
        jframe.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
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

                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case (KeyEvent.VK_W) -> {
                        UP_HELD = false;
                        System.out.println("bad");
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
                }
            }

        });

    }
    public void addMouseListenerHere(JFrame jframe) {
        jframe.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("CLICKED");
            }
            @Override
            public void mousePressed(MouseEvent e) {
                drawingSelector = true;
                boxx1 = MX;
                boxy1 = MY;
                boxx2 = MX;
                boxy2 = MY;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                drawingSelector = false;
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
