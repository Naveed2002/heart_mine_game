package game;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * 
 * check the names. ---------cat player_data.json
 * Handles saving and loading game configuration/user data in JSON format.
 */
public class ConfigManager {
  private static final String DATA_FILE = "player_data.json";

  public static void savePlayerName(String name) {
    String json = "{ \"playerName\": \"" + name + "\" }";
    try (FileWriter writer = new FileWriter(DATA_FILE)) {
      writer.write(json);
    } catch (Exception e) {
      System.err.println("Error saving player data: " + e.getMessage());
    }
  }

  public static String loadPlayerName() {
    File file = new File(DATA_FILE);
    if (!file.exists()) {
      return "";
    }

    try (Scanner scanner = new Scanner(file)) {
      StringBuilder content = new StringBuilder();
      while (scanner.hasNextLine()) {
        content.append(scanner.nextLine());
      }

      String json = content.toString();
      // Simple JSON parsing to get the value of playerName
      if (json.contains("\"playerName\":")) {
        int start = json.indexOf("\"playerName\":") + 13;
        // Simple JSON parsing to get the value of playerName
        // Let's refine the index logic
        int valueStart = json.indexOf("\"", start) + 1;
        int valueEnd = json.indexOf("\"", valueStart);
        return json.substring(valueStart, valueEnd);
      }
    } catch (Exception e) {
      System.err.println("Error loading player data: " + e.getMessage());
    }
    return "";
  }
}
