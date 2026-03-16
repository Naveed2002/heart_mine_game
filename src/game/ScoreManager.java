package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ScoreManager {
  private int score;
  private int lives;
  private String playerName;
  private BufferedImage heartImg;

  public ScoreManager(String playerName) {
    this.playerName = playerName;
    this.score = 0;
    this.lives = 3; // Start 3 lives for a player

    try {
      heartImg = ImageIO.read(getClass().getResourceAsStream("/game/res/heart.png"));
    } catch (Exception e) {
      System.err.println("Could not load heart.png");
    }
  }

  public void addPoints(int points) {
    score += points;
  }

  public boolean loseLife() {
    lives--;
    return lives > 0;
  }

  public void resetLives() {
    this.lives = 3;
  }

  public int getScore() {
    return score;
  }

  public int getLives() {
    return lives;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void draw(Graphics g, int screenWidth) {
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 20));

    // Draw player name
    String playerText = "Miner: " + playerName;
    g.drawString(playerText, 20, 32);

    // Draw lives (hearts) beside the player name
    int heartsStrX = 20 + g.getFontMetrics().stringWidth(playerText) + 30;
    if (heartImg != null) {
      for (int i = 0; i < lives; i++) {
        g.drawImage(heartImg, heartsStrX + (i * 35), 10, 30, 30, null);
      }
    } else {
      g.drawString("Lives: " + lives, heartsStrX, 32);
    }

    // Draw score
    String scoreText = "Score: " + score;
    int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
    g.drawString(scoreText, screenWidth - scoreWidth - 20, 32);
  }
}
