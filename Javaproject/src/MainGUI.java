import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainGUI extends JFrame {

    private static final String DATA_DIR = "data";
    private User user;
    private Quiz quiz;
    private ScoreManager scoreManager;
    private AchievementSystem achievementSystem;
    private MistakeManager mistakeManager;

    public MainGUI() {
        ensureDataDir();
        setTitle("Language Learning App");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Username
        String username = JOptionPane.showInputDialog(
                this, "Enter your username:", "Player");
        if (username == null || username.trim().isEmpty()) username = "Player";
        user = new User(username.trim());

        // Choose language
        String[] langs = {"Spanish", "French", "Hindi"};
        String langChoice = (String) JOptionPane.showInputDialog(
                this, "Choose language:", "Language",
                JOptionPane.PLAIN_MESSAGE, null, langs, langs[0]);
        if (langChoice == null) langChoice = "Spanish";
        String lang = langChoice.toLowerCase();

        quiz = new Quiz(lang);
        scoreManager = new ScoreManager();
        achievementSystem = user.getAchievementSystem(); // same instance used in User
        mistakeManager = new MistakeManager();
        quiz.setServices(scoreManager, achievementSystem, mistakeManager);

        Main.loadUserSnapshot(user);

        setLayout(new BorderLayout());
        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel p = new JPanel(new GridLayout(7, 1, 8, 8));
        String[] labels = {
                "Play Quiz",
                "Word Bank",
                "Review Mistakes",
                "Show Leaderboard",
                "Show Profile",
                "Save Profile",
                "Exit"
        };
        for (String label : labels) {
            JButton b = new JButton(label);
            b.addActionListener(e -> handleMenu(label));
            p.add(b);
        }
        return p;
    }

    private void handleMenu(String label) {
        switch (label) {
            case "Play Quiz":
                quiz.startQuizGUI(this, user);
                break;
            case "Word Bank":
                showWordBankGUI();
                break;
            case "Review Mistakes":
                showMistakesGUI();
                break;
            case "Show Leaderboard":
                showLeaderboardGUI();
                break;
            case "Show Profile":
                Main.showProfileGUI(this, user);
                break;
            case "Save Profile":
                Main.saveUserSnapshot(user);
                JOptionPane.showMessageDialog(this, "Profile saved.");
                break;
            case "Exit":
                dispose();
                System.exit(0);
                break;
        }
    }

    private void showWordBankGUI() {
        java.util.List<Question> words = quiz.getWordQuestions();
        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No words available.", "Word Bank",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JTextArea area = new JTextArea();
        for (Question q : words) {
            area.append(q.getWord() + " -> " + q.getTranslation()
                    + " [" + q.getDifficulty() + "]\n");
        }
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Word Bank", JOptionPane.PLAIN_MESSAGE);
    }

    private void showMistakesGUI() {
        java.util.List<String> ms = mistakeManager.getMistakes();
        JTextArea area = new JTextArea();
        if (ms.isEmpty()) {
            area.setText("No mistakes yet!");
        } else {
            for (String m : ms) area.append(m + "\n");
        }
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Mistakes", JOptionPane.PLAIN_MESSAGE);
    }

    private void showLeaderboardGUI() {
        java.util.List<String> lines = scoreManager.getLeaderboardLines();
        JTextArea area = new JTextArea();
        if (lines.isEmpty()) {
            area.setText("No leaderboard yet.");
        } else {
            for (String l : lines) area.append(l + "\n");
        }
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Leaderboard", JOptionPane.PLAIN_MESSAGE);
    }

    private static void ensureDataDir() {
        File d = new File(DATA_DIR);
        if (!d.exists()) d.mkdirs();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
