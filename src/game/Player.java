package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {
  public enum Direction {
    UP, DOWN, LEFT, RIGHT
  }

  private int col; // Grid X
  private int row; // Grid Y
  private int size;
  private Color color;
  private BufferedImage minerImage;
  private BufferedImage minerDigImage;
  private boolean facingRight;
  private Direction lastFacingDirection;
  private boolean isDigging;

  public Player(int startCol, int startRow, int size) {
    this.col = startCol;
    this.row = startRow;
    this.size = size;
    this.color = Color.BLUE; // Miner color (fallback)
    this.facingRight = true;
    this.lastFacingDirection = Direction.RIGHT;
    this.isDigging = false;

    try {
      // Load miner images
      minerImage = ImageIO.read(getClass().getResourceAsStream("/game/res/miner.png"));
      minerDigImage = ImageIO.read(getClass().getResourceAsStream("/game/res/miner_dig.png"));
    } catch (Exception e) {
      System.err.println("Could not load miner images: " + e.getMessage());
    }
  }

  public void setDigging(boolean digging) {
    this.isDigging = digging;
  }

  public void move(int dCol, int dRow, int maxCols, int maxRows) {
    if (dCol > 0) {
      facingRight = true;
      lastFacingDirection = Direction.RIGHT;
    } else if (dCol < 0) {
      facingRight = false;
      lastFacingDirection = Direction.LEFT;
    } else if (dRow > 0) {
      lastFacingDirection = Direction.DOWN;
    } else if (dRow < 0) {
      lastFacingDirection = Direction.UP;
    }

    int newCol = col + dCol;
    int newRow = row + dRow;

    if (newCol >= 0 && newCol < maxCols && newRow >= 0 && newRow < maxRows) {
      col = newCol;
      row = newRow;
    }
  }

  public void revertPosition(int dCol, int dRow) {
    col -= dCol;
    row -= dRow;
  }

  public int getCol() {
    return col;
  }

  public int getRow() {
    return row;
  }

  public Direction getLastFacingDirection() {
    return lastFacingDirection;
  }

  public void draw(Graphics g) {
    int px = col * size;
    int py = row * size;

    BufferedImage imgToDraw = isDigging && minerDigImage != null ? minerDigImage : minerImage;

    if (imgToDraw != null) {
      if (facingRight) {
        g.drawImage(imgToDraw, px, py, size, size, null);
      } else {
        // Draw flipped horizontally
        // x, y, width, height -> x + width, y, -width, height works in Graphics
        g.drawImage(imgToDraw, px + size, py, -size, size, null);
      }
    } else {
      // Fallback drawing
      g.setColor(color);
      g.fillOval(px + 5, py + 5, size - 10, size - 10);

      // Draw pickaxe (simple line)
      g.setColor(Color.gray);
      g.fillRect(px + size - 10, py + 5, 5, 20); // Handle
      g.fillRect(px + size - 15, py + 5, 15, 5); // Head
    }
  }
}
