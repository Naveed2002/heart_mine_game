package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private GamePanel gamePanel;

    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_UP) {
            gamePanel.movePlayer(0, -1);
        } else if (code == KeyEvent.VK_DOWN) {
            gamePanel.movePlayer(0, 1);
        } else if (code == KeyEvent.VK_LEFT) {
            gamePanel.movePlayer(-1, 0);
        } else if (code == KeyEvent.VK_RIGHT) {
            gamePanel.movePlayer(1, 0);
        } else if (code == KeyEvent.VK_SPACE) {
            gamePanel.dig();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
