package game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HeartAPI {

  private static final String API_URL = "https://marcconrad.com/uob/heart/api.php?out=json";

  public static class Puzzle {
    public String imageUrl;
    public int solution;
  }

  public static Puzzle getPuzzle() {
    try {
      URL url = new URL(API_URL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);

      int responseCode = connection.getResponseCode();
      if (responseCode == 200) {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
        }
        in.close();
        connection.disconnect();

        return parseJson(content.toString());
      } else {
        System.err.println("API Request failed with code: " + responseCode);
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static Puzzle parseJson(String json) {
    Puzzle puzzle = new Puzzle();
    try {
      // Regex for solution (number)
      java.util.regex.Pattern solPattern = java.util.regex.Pattern.compile("\"solution\"\\s*:\\s*(\\d+)");
      java.util.regex.Matcher solMatcher = solPattern.matcher(json);
      if (solMatcher.find()) {
        puzzle.solution = Integer.parseInt(solMatcher.group(1));
      }

      // Regex for image URL (supports "question" or "image" key)
      java.util.regex.Pattern imgPattern = java.util.regex.Pattern.compile("\"(?:question|image)\"\\s*:\\s*\"(.*?)\"");
      java.util.regex.Matcher imgMatcher = imgPattern.matcher(json);
      if (imgMatcher.find()) {
        String url = imgMatcher.group(1);
        // Fix escaped slashes
        url = url.replace("\\/", "/");
        puzzle.imageUrl = url;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    if (puzzle.imageUrl == null)
      return null; // We need the image

    return puzzle;
  }
}
