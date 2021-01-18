import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Game implements ActionListener {
    public static Game game;
    public static Random rand = new Random();
    public final int WIDTH = 1800, HEIGHT = 1000;
    public static int XOFF = 800, YOFF = 400;
    public static boolean DOWN_HELD = false;
    public static boolean UP_HELD = false;
    public static boolean LEFT_HELD = false;
    public static boolean RIGHT_HELD = false;
    public static Renderer renderer;
    public static String input = "null";
    public static int count = 0;
    public static long timeElapsed = 0;
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
        timer.start();
    }
    public static void main(String[] args) {
        game = new Game();
    }
    public void repaint(Graphics g) {
        count++;
        Instant start = Instant.now();
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
        g.setColor(new Color(144, 103, 34));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        Instant finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        g.setColor(Color.black);
        g.drawString(timeElapsed + " ms", 10, 50);
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
                    case (KeyEvent.VK_F1) -> {
                        input = "kill";
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

    @Override
    public void actionPerformed(ActionEvent e) {
        renderer.repaint();
    }
}
