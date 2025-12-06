import java.util.HashMap;
public class ProgressTracker {
    private HashMap<String, Integer> levelScores = new HashMap<>();
    public void recordScore(String key, int score) {
        levelScores.put(key, score);
    }
    public void showProgress() {
        System.out.println("\n--- Progress Tracker ---");

        if (levelScores.isEmpty()) {
            System.out.println("No progress yet.");
            return;
        }

        levelScores.forEach((lvl, scr) -> {
            System.out.println(lvl + " → Score: " + scr);
        });
    }
}