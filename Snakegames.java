import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Snakegames extends JFrame implements ActionListener, KeyListener {

    private static final int TILE_SIZE = 25;
    private static final int GRID_SIZE = 30;
    private static final int GAME_SPEED = 150;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction direction = Direction.RIGHT;

    private Timer timer;
    private int snakeLength = 3;
    private int[] snakeX, snakeY;
    private int appleX, appleY;
    private int score = 0;

    private JPanel gamePanel;
    private Image backgroundImage;
    private List<Image> obstacles;
    private List<Point> obstaclePositions;

    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem startGameItem;
    private JMenuItem howToPlayItem;
    private JMenuItem exitItem;
    
    private boolean gameStarted = false;
    
    public Snakegames() {
        setTitle("Snake Game");
        initializeGameWindow();
        initializeGame();
        
        // Initialize the menu bar and items
         menuBar = new JMenuBar();
         gameMenu = new JMenu("Menu Bar");
         startGameItem = new JMenuItem("Start Game");
         howToPlayItem = new JMenuItem("How to Play");
         exitItem = new JMenuItem("Exit");
 
         // Add action listeners to menu items
         startGameItem.addActionListener(this);
         howToPlayItem.addActionListener(this);
         exitItem.addActionListener(this);
 
         // Add menu items to the game menu
         gameMenu.add(startGameItem);
         gameMenu.add(howToPlayItem);
         gameMenu.addSeparator(); 
         gameMenu.add(exitItem);
 
         // Add the game menu to the menu bar
         menuBar.add(gameMenu);
 
         // Set the menu bar for the frame
         setJMenuBar(menuBar);
    }
    
    private void initializeGameWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    
        backgroundImage = new ImageIcon("./pictures/background.png").getImage();
    
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
    
        // Get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
    
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        gamePanel.setOpaque(false);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);
        add(gamePanel);
    
        pack();
        setLocationRelativeTo(null);
    
        timer = new Timer(GAME_SPEED, this);
        timer.start();
    }
    
    private void initializeGame() {
        snakeX = new int[GRID_SIZE * GRID_SIZE];
        snakeY = new int[GRID_SIZE * GRID_SIZE];
        snakeX[0] = GRID_SIZE / 2 * TILE_SIZE;
        snakeY[0] = GRID_SIZE / 2 * TILE_SIZE;

        spawnApple();
        loadObstacleImages();
        placeObstacles();
    }

    private void loadObstacleImages() {
        obstacles = new ArrayList<>();
        obstacles.add(new ImageIcon("./pictures/3.png").getImage());

    }

    private void placeObstacles() {
        Random random = new Random();
        obstaclePositions = new ArrayList<>();
        for (int i = 0; i < 5; i++) { // Change 5 to the number of obstacles you want
            int obsX = random.nextInt(GRID_SIZE) * TILE_SIZE;
            int obsY = random.nextInt(GRID_SIZE) * TILE_SIZE;

            while ((obsX == appleX && obsY == appleY) || isObstacleOverlappingSnake(obsX, obsY)) {
                obsX = random.nextInt(GRID_SIZE) * TILE_SIZE;
                obsY = random.nextInt(GRID_SIZE) * TILE_SIZE;
            }
            obstaclePositions.add(new Point(obsX, obsY));
        }
    }

    private boolean isObstacleOverlappingSnake(int x, int y) {
        for (int i = 0; i < snakeLength; i++) {
            if (x == snakeX[i] && y == snakeY[i]) {
                return true;
            }
        }
        return false;
    }

    private void spawnApple() {
        Random random = new Random();
        appleX = random.nextInt(GRID_SIZE) * TILE_SIZE;
        appleY = random.nextInt(GRID_SIZE) * TILE_SIZE;
    }

    private void move() {
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (direction) {
            case UP:
                snakeY[0] -= TILE_SIZE;
                break;
            case DOWN:
                snakeY[0] += TILE_SIZE;
                break;
            case LEFT:
                snakeX[0] -= TILE_SIZE;
                break;
            case RIGHT:
                snakeX[0] += TILE_SIZE;
                break;
        }
    }

    private void checkCollisions() {
        // Check if snake collides with walls
        if (snakeX[0] < 0 || snakeX[0] >= getWidth() || snakeY[0] < 0 || snakeY[0] >= getHeight()) {
            gameOver();
        }
    
        // Check if snake collides with itself
        for (int i = 1; i < snakeLength; i++) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                gameOver();
            }
        }
    
        // Check if snake eats apple
        if (snakeX[0] == appleX && snakeY[0] == appleY) {
            score++;
            snakeLength++;
            spawnApple();
        }
    
        // Check if snake strictly collides with obstacles
        for (Point obs : obstaclePositions) {
            int obsWidth = obstacles.get(0).getWidth(this);
            int obsHeight = obstacles.get(0).getHeight(this);
    
            if (snakeX[0] < obs.x + obsWidth &&
                snakeX[0] + TILE_SIZE > obs.x &&
                snakeY[0] < obs.y + obsHeight &&
                snakeY[0] + TILE_SIZE > obs.y) {
                gameOver(); 
            }
        }
    }
    
    
    

    private void gameOver() {
        timer.stop();
        int choice = JOptionPane.showOptionDialog(
                this,
                "Game Over!\nScore: " + score + "\nDo you want to play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Play Again", "Exit"},
                "Play Again");
    
        if (choice == JOptionPane.YES_OPTION) {
            resetGame(); // Reset the game
        } else {
            System.exit(0); // Exit the game if "Exit" is chosen
        }
    }
    
    private void resetGame() {
        initializeGame(); // Reset positions and apple
        score = 0;
        snakeLength = 3; // Reset snake length to 3
        timer.start();
        gameStarted = true; // Set the gameStarted flag to true
    }
    
private void drawGame(Graphics g) {
    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

    for (Point obs : obstaclePositions) {
       
        int obstacleWidth = obstacles.get(0).getWidth(this);
        int obstacleHeight = obstacles.get(0).getHeight(this);

      
        g.drawImage(obstacles.get(0), obs.x, obs.y, obstacleWidth, obstacleHeight, this);
    }

    for (int i = 0; i < snakeLength; i++) {
        if (i == 0) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLUE);
        }
        g.fillRect(snakeX[i], snakeY[i], TILE_SIZE, TILE_SIZE);
    }

    g.setColor(Color.RED);
    g.fillOval(appleX, appleY, TILE_SIZE, TILE_SIZE);
}



private void startGame() {
    initializeGame();
    gamePanel.requestFocusInWindow(); 
    timer.start();
    gameStarted = true; 
}



@Override
public void actionPerformed(ActionEvent e) {
    if (gameStarted) { 
        move();
        gamePanel.repaint();
        checkCollisions();
    }

    Object source = e.getSource();

    if (source == startGameItem && !gameStarted) {
        // Start the game only if "Start Game" is clicked and game has not started
        startGame();
    } else if (source == howToPlayItem) {
        // Show a dialog with instructions on how to play
        JOptionPane.showMessageDialog(this, "How to Play:\nUse arrow keys to control the snake.\nEat the red apples to grow.\nAvoid colliding with the walls and yourself.\nColliding with obstacles ends the game.", "How to Play", JOptionPane.INFORMATION_MESSAGE);
    } else if (source == exitItem) {
        // Exit the game
        System.exit(0);
    } 
}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP && direction != Direction.DOWN) {
            direction = Direction.UP;
        } else if (key == KeyEvent.VK_DOWN && direction != Direction.UP) {
            direction = Direction.DOWN;
        } else if (key == KeyEvent.VK_LEFT && direction != Direction.RIGHT) {
            direction = Direction.LEFT;
        } else if (key == KeyEvent.VK_RIGHT && direction != Direction.LEFT) {
            direction = Direction.RIGHT;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
