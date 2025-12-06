import java.io.*;
import java.util.*;

public class ScoreManager {
    private static final String FILE = "data/leaderboard.txt";

    public void saveScore(String user, int score) {
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(user + "," + score + "\n");
        } catch (Exception e) {
            System.out.println("Error writing leaderboard.");
        }
    }

    public void showLeaderboard() {
        System.out.println("\n--- Leaderboard ---");
        File f = new File(FILE);
        if (!f.exists()) {
            System.out.println("No leaderboard yet.");
            return;
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (Exception e) {
            System.out.println("Error reading leaderboard.");
        }
        lines.stream()
                .sorted((a, b) -> {
                    int s1 = Integer.parseInt(a.split(",")[1]);
                    int s2 = Integer.parseInt(b.split(",")[1]);
                    return Integer.compare(s2, s1);
                })
                .forEach(System.out::println);
    }

    // for GUI
    public List<String> getLeaderboardLines() {
        List<String> lines = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (Exception ignored) {}
        lines.sort((a, b) -> {
            int s1 = Integer.parseInt(a.split(",")[1]);
            int s2 = Integer.parseInt(b.split(",")[1]);
            return Integer.compare(s2, s1);
        });
        return lines;
    }
}