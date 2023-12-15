import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class App extends JPanel implements ActionListener, KeyListener {
    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private final int OBSTACLE_SIZE = 100;
    private final int[] obstaclesX = new int[4];
    private final int[] obstaclesY = new int[4];

    private JFrame menuFrame;
    private JButton startButton;
    private JButton howToPlayButton;
    private JButton exitButton;
    private boolean gameStarted = false;
    

    public App() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setupMenu();
    
    }

    
    private void setupMenu() {
        menuFrame = new JFrame("Snake Game Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(300, 200);
        menuFrame.setLocationRelativeTo(null);

        startButton = new JButton("Start Game");
        howToPlayButton = new JButton("How to Play");
        exitButton = new JButton("Exit");

        startButton.addActionListener(e -> {
            if (!gameStarted) {
                gameStarted = true;
                initializeGame();
            }
        });

        howToPlayButton.addActionListener(e -> showHowToPlay());

        exitButton.addActionListener(e -> System.exit(0));

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(3, 1));
        menuPanel.add(startButton);
        menuPanel.add(howToPlayButton);
        menuPanel.add(exitButton);

        menuFrame.add(menuPanel);
        menuFrame.setVisible(true);
    }

    private void initializeGame() {
        menuFrame.dispose(); // Close the menu frame
        JFrame frame = new JFrame("Snake Game"); // Create JFrame

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.add(this); // Add App panel to JFrame
        frame.setVisible(true);

        startGame(); // Start the game
    }
    
    private void startGame() {
        menuFrame.setVisible(false); // Hide the menu frame
        running = true;
        timer = new Timer(100, this);
        resetGame();
        timer.start();
        requestFocusInWindow();
    }
    
    private void showHowToPlay() {
        JOptionPane.showMessageDialog(menuFrame,
                "Use W, A, S, D to control the snake.\n" +
                        "Avoid collisions with the snake's body and obstacles.\n" +
                        "Collect apples to grow longer.",
                "How to Play",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void resetGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;
        newApple();
        placeObstacles();
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        timer.restart();
    }

    public void placeObstacles() {
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            obstaclesX[i] = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            obstaclesY[i] = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.YELLOW);
            for (int i = 0; i < 4; i++) {
                g.fillRect(obstaclesX[i], obstaclesY[i], OBSTACLE_SIZE, OBSTACLE_SIZE);
            }

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        Random random = new Random();
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        for (int i = 0; i < 4; i++) {
            if (x[0] >= obstaclesX[i] && x[0] < obstaclesX[i] + OBSTACLE_SIZE &&
                    y[0] >= obstaclesY[i] && y[0] < obstaclesY[i] + OBSTACLE_SIZE) {
                running = false;
            }
        }
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize() + SCREEN_HEIGHT / 2);
        g.drawString("Press 'Enter' to Play Again", (SCREEN_WIDTH - metrics2.stringWidth("Press 'Enter' to Play Again")) / 2, g.getFont().getSize() * 3 + SCREEN_HEIGHT / 2);
    
        
    
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_D:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_W:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_S:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
            case KeyEvent.VK_ENTER:
                if (!running) {
                    resetGame();
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App(); // Create an instance of App
        });
    }
    
}
