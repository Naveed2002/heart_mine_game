package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel {

  private static final int TILE_SIZE = 40;
  private static final int COLS = 40; // Increase map size to demonstrate camera
  private static final int ROWS = 30; // Increase map size to demonstrate camera
  private static final int WINDOW_WIDTH = 800; // 20 tiles wide
  private static final int WINDOW_HEIGHT = 600; // 15 tiles high
  private static final int WIDTH = TILE_SIZE * COLS;
  private static final int HEIGHT = TILE_SIZE * ROWS;
  private static final int HUD_HEIGHT = 50;

  private Block[][] blocks;
  private Player player;
  private ScoreManager scoreManager;
  private KeyHandler keyHandler;
  private boolean isGameOver;
  private boolean isDigging; // To prevent spamming and blocking UI logic
  private Timer gravityTimer;

  public GamePanel(String playerName) {
    this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT + HUD_HEIGHT));
    this.setBackground(Color.DARK_GRAY);
    this.setFocusable(true);
    this.keyHandler = new KeyHandler(this);
    this.addKeyListener(keyHandler);

    initGame(playerName);
  }

  private void initGame(String playerName) {
    blocks = new Block[ROWS][COLS]; // 15 rows, 20 cols
    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        blocks[r][c] = new Block(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE);

        double rand = Math.random();
        if (rand < 0.20) {
          // 20% chance to be solid white stone obstacle
          blocks[r][c].setType(Block.Type.WHITESTONE);
          blocks[r][c].setContent(Block.Type.WHITESTONE);
        } else {
          // Randomize hidden content
          double innerRand = Math.random();
          if (innerRand < 0.05) {
            blocks[r][c].setContent(Block.Type.GEM);
          } else if (innerRand < 0.15) {
            blocks[r][c].setContent(Block.Type.GOLD);
          } else if (innerRand < 0.35) {
            blocks[r][c].setContent(Block.Type.COPPER);
          } else {
            // Stone, but maybe a hidden bomb?
            if (Math.random() < 0.1) { // 10% chance if stone
              blocks[r][c].setContent(Block.Type.BOMB);
            } else {
              blocks[r][c].setContent(Block.Type.STONE);
            }
          }
        }
      }
    }

    // Generate some random ladders (vertical shafts)
    int numLadders = 5;
    for (int i = 0; i < numLadders; i++) {
      int ladderCol = (int) (Math.random() * COLS);
      int ladderTopRow = (int) (Math.random() * (ROWS - 5));
      int ladderLength = 3 + (int) (Math.random() * 4); // 3 to 6 blocks tall

      for (int r = ladderTopRow; r < Math.min(ROWS, ladderTopRow + ladderLength); r++) {
        blocks[r][ladderCol].setType(Block.Type.LADDER);
        blocks[r][ladderCol].setContent(Block.Type.LADDER);
        blocks[r][ladderCol].dig(0); // Make ladders pre-dug so you can see them and walk on them immediately
      }
    }

    // Pre-dig the surface (top row) so the player has somewhere to walk!
    for (int c = 0; c < COLS; c++) {
      if (blocks[0][c].getType() == Block.Type.WHITESTONE) {
        blocks[0][c].setType(Block.Type.STONE);
        blocks[0][c].setContent(Block.Type.STONE);
      }
      blocks[0][c].dig(0);
    }

    // Generate random winding dirt tunnels (horizontal emphasis)
    int numTunnels = 6;
    for (int t = 0; t < numTunnels; t++) {
      int startRow = 3 + (int) (Math.random() * (ROWS - 6)); // Avoid very top and very bottom
      int currentRow = startRow;

      // Carve a tunnel across the map horizontally
      for (int c = 0; c < COLS; c++) {
        // 20% chance to shift up or down a row
        if (Math.random() < 0.20) {
          currentRow += (Math.random() > 0.5) ? 1 : -1;
        }

        // Clamp row inside the map bounds (leave bottom row untouched)
        currentRow = Math.max(1, Math.min(currentRow, ROWS - 2));

        // Clear the block if it's not indestructible
        if (blocks[currentRow][c].getType() != Block.Type.WHITESTONE
            && blocks[currentRow][c].getType() != Block.Type.TREASURE_BOX) {
          blocks[currentRow][c].setType(Block.Type.STONE);
          blocks[currentRow][c].setContent(Block.Type.STONE);
          blocks[currentRow][c].dig(0);
        }
      }
    }

    player = new Player(0, 0, TILE_SIZE); // Start at top-left
    scoreManager = new ScoreManager(playerName);
    isGameOver = false;
    isDigging = false;

    // Ensure start block is safe and clear
    blocks[0][0].setType(Block.Type.STONE);
    blocks[0][0].setContent(Block.Type.STONE);
    blocks[0][0].dig(0);

    // Place exactly one treasure box somewhere (that isn't 0,0)
    boolean treasurePlaced = false;
    while (!treasurePlaced) {
      int tx = (int) (Math.random() * COLS);
      int ty = (int) (Math.random() * ROWS);
      if ((tx != 0 || ty != 0) && blocks[ty][tx].getType() != Block.Type.WHITESTONE) {
        blocks[ty][tx].setType(Block.Type.TREASURE_BOX);
        blocks[ty][tx].setContent(Block.Type.TREASURE_BOX);
        blocks[ty][tx].dig(0); // Dig it immediately so it's visible on the map!
        treasurePlaced = true;
      }
    }
  }

  public void movePlayer(int dx, int dy) {
    if (isGameOver || isDigging)
      return; // Prevent movement while digging or game over

    // Even if blocked, we want to update the player's facing direction
    player.move(dx, dy, COLS, ROWS);

    // BUT we must undo the position if it's blocked.
    // So let's calculate target first, checking if the move is valid
    int targetCol = player.getCol();
    int targetRow = player.getRow();

    // Check bounds and block collisions
    boolean canMove = false;
    if (targetCol >= 0 && targetCol < COLS && targetRow >= 0 && targetRow < ROWS) {
      Block targetBlock = blocks[targetRow][targetCol];
      if (targetBlock.getType() != Block.Type.WHITESTONE) {
        // You can only walk into it if it's already dug (empty space) OR it's a ladder
        // But we DO NOT want to walk through the treasure box, we want to hit it!
        if ((targetBlock.isDug() && targetBlock.getType() != Block.Type.TREASURE_BOX)
            || targetBlock.getType() == Block.Type.LADDER) {
          canMove = true;
        }
      }
    }

    if (!canMove) {
      // Revert position manually to avoid triggering the move() method which would
      // flip the lastFacingDirection
      player.revertPosition(dx, dy);
    }

    repaint();
  }

  public void dig() {
    System.out.println("Dig pressed!");
    if (isGameOver || isDigging) {
      System.out.println("Return early: isGameOver=" + isGameOver + ", isDigging=" + isDigging);
      return;
    }

    // Determine target block based on facing direction
    int targetCol = player.getCol();
    int targetRow = player.getRow();
    Player.Direction dir = player.getLastFacingDirection();

    switch (dir) {
      case UP:
        targetRow--;
        break;
      case DOWN:
        targetRow++;
        break;
      case LEFT:
        targetCol--;
        break;
      case RIGHT:
        targetCol++;
        break;
    }

    // Check bounds
    if (targetCol < 0 || targetCol >= COLS || targetRow < 0 || targetRow >= ROWS) {
      System.out.println("Cannot dig outside map bounds.");
      return;
    }

    Block currentBlock = blocks[targetRow][targetCol];
    System.out.println("Mining block at (" + targetCol + ", " + targetRow + ") is type: "
        + currentBlock.getType() + ", isDug=" + currentBlock.isDug());

    // Allow digging treasure box even if it's "isDug == true" since we made it
    // pre-dug to be visible
    if (currentBlock.isDug() && currentBlock.getType() != Block.Type.TREASURE_BOX) {
      System.out.println("Block is already dug. Returning early.");
      return;
    }

    // Cannot dig ladders or whitestone
    if (currentBlock.getType() == Block.Type.WHITESTONE || currentBlock.getType() == Block.Type.LADDER) {
      System.out.println("Cannot dig this block type.");
      return;
    }

    isDigging = true;
    player.setDigging(true);
    repaint();

    playSound("/dig.wav");

    // The block's true identity is hidden in 'contentType' until dug.
    // Reveal it first before checking what we hit.
    currentBlock.dig(0);
    Block.Type hitType = currentBlock.getType();

    if (hitType == Block.Type.BOMB) {
      System.out.println("BOOM! Bomb hit!");
      playSound("/bomb.wav");

      boolean hasLivesLeft = scoreManager.loseLife();
      paintImmediately(0, 0, getWidth(), getHeight()); // Force synchronous UI update for heart loss

      if (!hasLivesLeft) {
        System.out.println("Fetching puzzle to revive...");
        new Thread(() -> {
          // Add slight delay so the UI actually shows 0 hearts before freezing on a
          // JOptionPane
          try {
            Thread.sleep(150);
          } catch (InterruptedException e) {
          }

          HeartAPI.Puzzle puzzle = HeartAPI.getPuzzle();

          if (puzzle != null) {
            try {
              java.net.URL url = new java.net.URI(puzzle.imageUrl).toURL();
              BufferedImage image = javax.imageio.ImageIO.read(url);
              ImageIcon icon = new ImageIcon(image);

              SwingUtilities.invokeLater(() -> {
                String input = (String) JOptionPane.showInputDialog(
                    this,
                    "Solve the puzzle to get 3 hearts back!",
                    "Revive Puzzle",
                    JOptionPane.QUESTION_MESSAGE,
                    icon,
                    null,
                    "");

                // Check answer
                if (input != null && input.trim().equals(String.valueOf(puzzle.solution))) {
                  scoreManager.resetLives();
                  paintImmediately(0, 0, getWidth(), getHeight()); // Force synchronous UI update for revive
                  showNiceRevivedPopup();
                  // Since reviving, player continues digging. Note: block was a bomb and is dug.

                  // Hide bomb block so player doesn't trigger it again immediately!
                  currentBlock.setContent(Block.Type.STONE);

                  isDigging = false;
                  player.setDigging(false);
                  repaint();
                } else {
                  if (input != null) { // Wrong answer
                    JOptionPane.showMessageDialog(this, "Wrong answer! Game Over.", "Failed",
                        JOptionPane.WARNING_MESSAGE);
                  }
                  isGameOver = true;
                  showGameOver();
                }
              });
            } catch (Exception e) {
              e.printStackTrace();
              SwingUtilities.invokeLater(() -> {
                isGameOver = true;
                showGameOver();
              });
              resetDiggingState();
            }
          } else {
            System.err.println("Failed to fetch puzzle.");
            SwingUtilities.invokeLater(() -> {
              isGameOver = true;
              showGameOver();
            });
            resetDiggingState();
          }
        }).start();
      } else {
        showNiceBombPopup();

        // Hide bomb block so player doesn't trigger it again immediately
        currentBlock.setContent(Block.Type.STONE);
        isDigging = false;
        player.setDigging(false);
        repaint();
      }
    } else {
      handleDigResult(currentBlock);

      // Keep the dig animation on screen for 200ms before restoring the idle sprite
      javax.swing.Timer digTimer = new javax.swing.Timer(200, e -> {
        isDigging = false;
        player.setDigging(false);
        repaint();
      });
      digTimer.setRepeats(false);
      digTimer.start();
    }
  }

  private void resetDiggingState() {
    SwingUtilities.invokeLater(() -> {
      isDigging = false;
      player.setDigging(false);
      repaint();
    });
  }

  private void handleDigResult(Block block) {
    // Dig the block
    block.dig(0);

    Block.Type type = block.getType(); // Revealed type

    if (type == Block.Type.COPPER) {
      scoreManager.addPoints(10);
      System.out.println("Found Copper! +10");
    } else if (type == Block.Type.GOLD) {
      scoreManager.addPoints(20);
      System.out.println("Found Gold! +20");
    } else if (type == Block.Type.GEM) {
      scoreManager.addPoints(50);
      System.out.println("Found Gem! +50");
    } else if (type == Block.Type.TREASURE_BOX) {
      scoreManager.addPoints(500); // Massive bonus for the single treasure
      System.out.println("Found Treasure Box! +500");
      playSound("/dig.wav"); // Maybe a special sound here later?
      showNiceTreasurePopup();
    } else if (type == Block.Type.LADDER) {
      // Ladders are just structural, no points awarded
      System.out.println("Hit a ladder!");
    }
  }

  private void showNiceRevivedPopup() {
    JDialog dialog = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Revived!", true);
    dialog.setUndecorated(true);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(30, 45, 30));
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(80, 220, 80), 4, true),
        BorderFactory.createEmptyBorder(20, 40, 20, 40)));

    try {
      java.net.URL url = Block.class.getResource("/heart.png");
      if (url != null) {
        ImageIcon heartIcon = new ImageIcon(
            new ImageIcon(url).getImage().getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH));
        JLabel iconLabel = new JLabel(heartIcon);
        iconLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
      }
    } catch (Exception e) {
    }

    JLabel titleLabel = new JLabel("Revived!");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
    titleLabel.setForeground(new Color(100, 255, 100));
    titleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

    JLabel msgLabel = new JLabel("Correct! Got 3 hearts back.");
    msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
    msgLabel.setForeground(Color.WHITE);
    msgLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

    JButton okButton = new JButton("Awesome!");
    okButton.setFocusPainted(false);
    okButton.setBackground(new Color(60, 180, 60));
    okButton.setForeground(Color.WHITE);
    okButton.setFont(new Font("SansSerif", Font.BOLD, 16));
    okButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
    okButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    okButton.setOpaque(true);
    okButton.setBorderPainted(false);
    okButton.addActionListener(e -> dialog.dispose());

    panel.add(titleLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(msgLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 25)));
    panel.add(okButton);

    dialog.add(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void showNiceBombPopup() {
    JDialog dialog = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Bomb!", true);
    dialog.setUndecorated(true);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(40, 30, 30));
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 50, 50), 4, true),
        BorderFactory.createEmptyBorder(20, 40, 20, 40)));

    try {
      java.net.URL url = Block.class.getResource("/bomb.png");
      if (url != null) {
        ImageIcon bombIcon = new ImageIcon(
            new ImageIcon(url).getImage().getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH));
        JLabel iconLabel = new JLabel(bombIcon);
        iconLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
      }
    } catch (Exception e) {
    }

    JLabel titleLabel = new JLabel("BOOM!");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
    titleLabel.setForeground(new Color(255, 80, 80));
    titleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

    JLabel msgLabel = new JLabel("You hit a bomb! Lost a heart.");
    msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
    msgLabel.setForeground(Color.WHITE);
    msgLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

    JButton okButton = new JButton("Ouch...");
    okButton.setFocusPainted(false);
    okButton.setBackground(new Color(200, 60, 60));
    okButton.setForeground(Color.WHITE);
    okButton.setFont(new Font("SansSerif", Font.BOLD, 16));
    okButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
    okButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    okButton.setOpaque(true);
    okButton.setBorderPainted(false);
    okButton.addActionListener(e -> dialog.dispose());

    panel.add(titleLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(msgLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 25)));
    panel.add(okButton);

    dialog.add(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void showNiceTreasurePopup() {
    JDialog dialog = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Treasure Found!", true);
    dialog.setUndecorated(true);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(45, 35, 10)); // Gold/brownish background
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 215, 0), 4, true), // Gold border
        BorderFactory.createEmptyBorder(20, 40, 20, 40)));

    try {
      java.net.URL url = Block.class.getResource("/tresure_box_img.png");
      if (url != null) {
        ImageIcon treasureIcon = new ImageIcon(
            new ImageIcon(url).getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH));
        JLabel iconLabel = new JLabel(treasureIcon);
        iconLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
      }
    } catch (Exception e) {
    }

    JLabel titleLabel = new JLabel("JACKPOT!");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
    titleLabel.setForeground(new Color(255, 215, 0)); // Gold text
    titleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

    JLabel msgLabel = new JLabel("You found the hidden Treasure Box! +500 Points!");
    msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
    msgLabel.setForeground(Color.WHITE);
    msgLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

    JButton okButton = new JButton("Claim Loot!");
    okButton.setFocusPainted(false);
    okButton.setBackground(new Color(218, 165, 32)); // Goldenrod button
    okButton.setForeground(Color.BLACK);
    okButton.setFont(new Font("SansSerif", Font.BOLD, 16));
    okButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
    okButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    okButton.setOpaque(true);
    okButton.setBorderPainted(false);
    okButton.addActionListener(e -> dialog.dispose());

    panel.add(titleLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(msgLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 25)));
    panel.add(okButton);

    dialog.add(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void showGameOver() {
    if (gravityTimer != null)
      gravityTimer.stop();
    JOptionPane.showMessageDialog(this,
        "Game Over!\nTarget Hit: BOMB\nFinal Score: " + scoreManager.getScore(),
        "Game Over",
        JOptionPane.WARNING_MESSAGE);

    // Allow restart?
    int choice = JOptionPane.showConfirmDialog(this, "Play Again?", "Restart", JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
      initGame(scoreManager.getPlayerName());
      repaint();
    } else {
      System.exit(0);
    }
  }

  private void playSound(String resource) {
    try {
      java.net.URL url = getClass().getResource(resource);
      if (url != null) {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
      } else {
        System.err.println("Could not find " + resource);
      }
    } catch (Exception e) {
      System.err.println("Error playing sound " + resource + ": " + e.getMessage());
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Draw HUD background (static, ignore camera)
    g.setColor(new Color(25, 20, 30)); // match start screen theme
    g.fillRect(0, 0, WINDOW_WIDTH, HUD_HEIGHT);

    // Draw UI text inside HUD
    scoreManager.draw(g, WINDOW_WIDTH);

    // Calculate Camera Offset
    // Center the camera on the player
    int startX = player.getCol() * TILE_SIZE + (TILE_SIZE / 2);
    int startY = player.getRow() * TILE_SIZE + (TILE_SIZE / 2);

    int offsetX = WINDOW_WIDTH / 2 - startX;
    int offsetY = WINDOW_HEIGHT / 2 - startY;

    // Clamp camera within map bounds
    if (offsetX > 0)
      offsetX = 0; // Left edge
    if (offsetY > 0)
      offsetY = 0; // Top edge
    if (offsetX < WINDOW_WIDTH - WIDTH)
      offsetX = WINDOW_WIDTH - WIDTH; // Right edge
    if (offsetY < WINDOW_HEIGHT - HEIGHT)
      offsetY = WINDOW_HEIGHT - HEIGHT; // Bottom edge

    // Shift graphics context for the game area (with Camera Offset)
    g.translate(offsetX, HUD_HEIGHT + offsetY);

    // Draw blocks
    // Optimize: Only draw blocks inside the camera bounds
    int startCol = Math.max(0, -offsetX / TILE_SIZE);
    int endCol = Math.min(COLS, (-offsetX + WINDOW_WIDTH) / TILE_SIZE + 1);
    int startRow = Math.max(0, -offsetY / TILE_SIZE);
    int endRow = Math.min(ROWS, (-offsetY + WINDOW_HEIGHT) / TILE_SIZE + 1);

    for (int r = startRow; r < endRow; r++) {
      for (int c = startCol; c < endCol; c++) {
        blocks[r][c].draw(g);
      }
    }

    // --- Block Highlight (Minecraft style) ---
    int targetCol = player.getCol();
    int targetRow = player.getRow();
    Player.Direction dir = player.getLastFacingDirection();
    switch (dir) {
      case UP:
        targetRow--;
        break;
      case DOWN:
        targetRow++;
        break;
      case LEFT:
        targetCol--;
        break;
      case RIGHT:
        targetCol++;
        break;
    }

    // Draw highlight if it's within bounds and not a ladder/whitestone
    if (targetCol >= 0 && targetCol < COLS && targetRow >= 0 && targetRow < ROWS) {
      Block targetBlock = blocks[targetRow][targetCol];
      if (!targetBlock.isDug() && targetBlock.getType() != Block.Type.WHITESTONE
          && targetBlock.getType() != Block.Type.LADDER) {
        int hx = targetCol * TILE_SIZE;
        int hy = targetRow * TILE_SIZE;
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        java.awt.Stroke oldStroke = g2.getStroke();

        g2.setStroke(new java.awt.BasicStroke(2)); // Thicker line
        g2.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
        g2.drawRect(hx + 1, hy + 1, TILE_SIZE - 2, TILE_SIZE - 2);

        g2.setStroke(oldStroke);
      }
    }

    // Draw player
    player.draw(g);

    // --- Circular POV (Fog of War) ---
    java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
    int px = player.getCol() * TILE_SIZE + (TILE_SIZE / 2);
    int py = player.getRow() * TILE_SIZE + (TILE_SIZE / 2);
    int radius = TILE_SIZE * 4; // radius

    float[] dist = { 0.0f, 0.9f, 1.0f };
    // opacity for a lighter, brighter background
    Color[] colors = { new Color(0, 0, 0, 0), new Color(0, 0, 0, 60), new Color(0, 0, 0, 130) };
    java.awt.RadialGradientPaint p = new java.awt.RadialGradientPaint(
        new java.awt.geom.Point2D.Float(px, py), radius, dist, colors);

    java.awt.Paint oldPaint = g2d.getPaint();
    g2d.setPaint(p);

    // Fill only the screen area
    g2d.fillRect(-offsetX, -offsetY, WINDOW_WIDTH, WINDOW_HEIGHT);

    // Restore original paint
    g2d.setPaint(oldPaint);

    // Undo camera shift for static UI overlays
    g.translate(-offsetX, -(HUD_HEIGHT + offsetY));

    if (isDigging) {
      g.setColor(Color.WHITE);
      g.drawString("Digging...", WINDOW_WIDTH / 2 - 30, (WINDOW_HEIGHT + HUD_HEIGHT) / 2);
    }
  }
}
