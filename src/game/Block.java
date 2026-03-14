package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Block {
  public enum Type {
    STONE, WHITESTONE, COPPER, GOLD, GEM, BOMB, TREASURE_BOX, LADDER
  }

  private static BufferedImage stoneImg;
  private static BufferedImage whiteStoneImg;
  private static BufferedImage dirtImg;
  private static BufferedImage copperImg;
  private static BufferedImage goldImg;
  private static BufferedImage gemImg;
  private static BufferedImage bombImg;
  private static BufferedImage grassImg;
  private static BufferedImage treasureBoxImg;
  private static BufferedImage ladderImg;

  static {
    try {
      stoneImg = loadImage("/stone.png");
      whiteStoneImg = loadImage("/whitestone.png");
      dirtImg = loadImage("/dirt.png");
      copperImg = loadImage("/copper.png");
      goldImg = loadImage("/gold.png");
      gemImg = loadImage("/gem.png");
      bombImg = loadImage("/bomb.png");
      grassImg = loadImage("/grass.png");
      treasureBoxImg = loadImage("/tresure_box_img.png");
      ladderImg = loadImage("/ladder.png");
    } catch (Exception e) {
      System.err.println("Error loading block textures: " + e.getMessage());
    }
  }

  private static BufferedImage loadImage(String path) {
    try {
      return ImageIO.read(Block.class.getResourceAsStream(path));
    } catch (Exception e) {
      return null;
    }
  }

  private int x, y;
  private int size;
  private boolean isDug;
  private Type type; // The revealed type
  private Type contentType; // The hidden content

  public Block(int x, int y, int size) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.isDug = false;
    this.type = Type.STONE;
    this.contentType = Type.STONE; // Default
  }

  public void setContent(Type type) {
    this.contentType = type;
  }

  public Type getContent() {
    return contentType;
  }

  public void dig(int solution) {
    if (isDug)
      return;
    isDug = true;
    // Set the revealed type to the content
    this.type = this.contentType;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public boolean isDug() {
    return isDug;
  }

  public void draw(Graphics g) {
    if (!isDug) {
      // Background (Stone/Grass/Ladder)
      if (type == Type.LADDER && ladderImg != null) {
        // Draw the ladder texture fully
        g.drawImage(ladderImg, x, y, size, size, null);
      } else if (y == 0 && grassImg != null) {
        g.drawImage(grassImg, x, y, size, size, null);
      } else if (type == Type.WHITESTONE && whiteStoneImg != null) {
        g.drawImage(whiteStoneImg, x, y, size, size, null);
      } else if (stoneImg != null) {
        g.drawImage(stoneImg, x, y, size, size, null);
      } else {
        // Fallback procedural stone
        g.setColor(Color.GRAY);
        g.fillRect(x, y, size, size);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, size, size);
      }

      // Visualize content if not STONE and not BOMB (Hidden)
      if (contentType != Type.STONE && contentType != Type.BOMB) {
        BufferedImage overlay = null;
        switch (contentType) {
          case COPPER:
            overlay = copperImg;
            break;
          case GOLD:
            overlay = goldImg;
            break;
          case GEM:
            overlay = gemImg;
            break;
          default:
            break;
        }
        if (overlay != null) {
          // Draw smaller overlay
          int itemSize = (int) (size * 0.6);
          int offset = (size - itemSize) / 2;
          g.drawImage(overlay, x + offset, y + offset, itemSize, itemSize, null);
        }
      }

    } else {
      // Dug state
      // Draw background (dirt or ladder depending on type)
      if (type == Type.LADDER && ladderImg != null) {
        g.drawImage(ladderImg, x, y, size, size, null);
      } else if (dirtImg != null) {
        g.drawImage(dirtImg, x, y, size, size, null);
      } else {
        g.setColor(new Color(60, 40, 20)); // Dark brown dirt
        g.fillRect(x, y, size, size);
      }

      BufferedImage itemImg = null;
      switch (type) {
        case COPPER:
          itemImg = copperImg;
          break;
        case GOLD:
          itemImg = goldImg;
          break;
        case GEM:
          itemImg = gemImg;
          break;
        case BOMB:
          itemImg = bombImg;
          break;
        case TREASURE_BOX:
          itemImg = treasureBoxImg;
          break;
        default:
          break;
      }

      if (itemImg != null) {
        int itemSize = (int) (size * 0.8);
        int offset = (size - itemSize) / 2;
        g.drawImage(itemImg, x + offset, y + offset, itemSize, itemSize, null);
      }
    }
    // Shared border
    if (isDug) {
      g.setColor(new Color(50, 30, 10));
      g.drawRect(x, y, size, size);
    }
  }
}