import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AchievementSystem {

    private List<String> achievements = new ArrayList<>();

    public void checkAchievements(int totalScore, int quizzesTaken) {

        if (totalScore >= 50 && !hasAchievement("Bronze Scorer")) {
            addAchievement("Bronze Scorer");
        }
        if (totalScore >= 100 && !hasAchievement("Silver Scorer")) {
            addAchievement("Silver Scorer");
        }
        if (quizzesTaken >= 5 && !hasAchievement("Quiz Master")) {
            addAchievement("Quiz Master");
        }
    }

    public void checkAndAnnounce(User user) {
        for (String a : achievements) {
            System.out.println("🏆 New Achievement Unlocked: " + a);
        }
    }

    // NEW: GUI version, does NOT remove console one
    public void checkAndAnnounceGUI(java.awt.Component parent, User user) {
        if (achievements.isEmpty()) return;
        StringBuilder sb = new StringBuilder("New Achievements:\n");
        for (String a : achievements) sb.append(" - ").append(a).append("\n");
        JOptionPane.showMessageDialog(parent, sb.toString(),
                "Achievements", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean hasAchievement(String a) {
        return achievements.contains(a);
    }
    public void addAchievement(String a) {
        achievements.add(a);
    }
    public List<String> getAchievements() {
        return achievements;
    }
}
