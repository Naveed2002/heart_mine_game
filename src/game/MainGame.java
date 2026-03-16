package game;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainGame extends JFrame {

  public MainGame() {
    this.setTitle("Heart Mining Game");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);

    // Set Window & Dock Icon (macOS support)
    try {
      java.net.URL iconUrl = getClass().getResource("/game/res/miner.png");
      if (iconUrl != null) {
        java.awt.Image iconImg = new javax.swing.ImageIcon(iconUrl).getImage();
        // Standard window icon
        this.setIconImage(iconImg);

        // Icon
        if (java.awt.Taskbar.isTaskbarSupported()) {
          java.awt.Taskbar taskbar = java.awt.Taskbar.getTaskbar();
          if (taskbar.isSupported(java.awt.Taskbar.Feature.ICON_IMAGE)) {
            taskbar.setIconImage(iconImg);
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Could not set window icon: " + e.getMessage());
    }

    // Main Menu Panel
    javax.swing.JPanel startPanel = new javax.swing.JPanel();
    startPanel.setPreferredSize(new java.awt.Dimension(800, 600)); // Match typical GamePanel size
    startPanel.setBackground(new java.awt.Color(35, 30, 40)); // Dark grey/purple underground look
    startPanel.setLayout(new java.awt.GridBagLayout());

    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    gbc.insets = new java.awt.Insets(10, 10, 10, 10);
    gbc.gridx = 0;

    // Logo (Miner Image)
    try {
      java.awt.image.BufferedImage rawImg = javax.imageio.ImageIO
          .read(getClass().getResourceAsStream("/game/res/miner.png"));
      // Scale it up nicely for a logo
      java.awt.Image scaledImg = rawImg.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
      javax.swing.JLabel logoLabel = new javax.swing.JLabel(new javax.swing.ImageIcon(scaledImg));
      gbc.gridy = 0;
      startPanel.add(logoLabel, gbc);
    } catch (Exception e) {
      System.err.println("Could not load logo /miner.png: " + e.getMessage());
    }

    // Title
    javax.swing.JLabel titleLabel = new javax.swing.JLabel("HEART MINING");
    titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
    titleLabel.setForeground(new java.awt.Color(230, 200, 50)); // Golden yellow
    gbc.gridy = 1;
    startPanel.add(titleLabel, gbc);

    // Subtitle
    javax.swing.JLabel subTitle = new javax.swing.JLabel("Enter Your Miner Identity:");
    subTitle.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
    subTitle.setForeground(java.awt.Color.WHITE);
    gbc.gridy = 2;
    startPanel.add(subTitle, gbc);

    // Input Field
    String savedName = ConfigManager.loadPlayerName();
    javax.swing.JTextField nameField = new javax.swing.JTextField(15);
    if (!savedName.isEmpty()) {
      nameField.setText(savedName);
    }
    nameField.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
    nameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    nameField.setBackground(new java.awt.Color(50, 45, 60));
    nameField.setForeground(java.awt.Color.WHITE);
    nameField.setCaretColor(java.awt.Color.WHITE);
    nameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 200, 50), 2));
    gbc.gridy = 3;
    startPanel.add(nameField, gbc);

    // Start Button--------------
    javax.swing.JButton startBtn = new javax.swing.JButton("START DIGGING");
    startBtn.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
    startBtn.setForeground(java.awt.Color.WHITE);
    startBtn.setBackground(new java.awt.Color(200, 50, 50)); // Red/Heart theme
    startBtn.setFocusPainted(false);
    startBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 30, 15, 30));
    startBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    gbc.gridy = 4;
    gbc.insets = new java.awt.Insets(30, 10, 10, 10);
    startPanel.add(startBtn, gbc);

    //
  
    startBtn.addActionListener(e -> {
      String playerName = nameField.getText().trim();
      if (playerName.isEmpty()) {
        playerName = "Miner";
      } else {
        // Save the valid name to JSON
        ConfigManager.savePlayerName(playerName);
      }

      // Transition to game
      this.getContentPane().removeAll();
      GamePanel gamePanel = new GamePanel(playerName);
      this.add(gamePanel);
      this.revalidate();
      this.repaint();
      this.pack();
      this.setLocationRelativeTo(null);
      gamePanel.requestFocusInWindow();
    });

    this.add(startPanel);
    this.pack();
    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new MainGame();
    });
  }
}
