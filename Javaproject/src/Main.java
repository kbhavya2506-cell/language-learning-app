import java.io.*;
import java.util.*;

public class Main {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String LEADERBOARD_FILE = DATA_DIR + "/leaderboard.txt";
    private static final String MISTAKES_FILE = DATA_DIR + "/mistakes.txt";

    public static void main(String[] args) {
        ensureDataDir();
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Language Learning App ===");
        System.out.print("Enter your username: ");
        String username = sc.nextLine().trim();
        if (username.isEmpty()) username = "Player";
        User user = new User(username);

        System.out.println("Choose language:");
        System.out.println("1) Spanish");
        System.out.println("2) French");
        System.out.println("3) Hindi");
        System.out.print("Enter choice [1]: ");
        int langChoice = readInt(sc, 1, 3);
        String lang = (langChoice == 2) ? "french"
                : (langChoice == 3) ? "hindi" : "spanish";

        Quiz quiz = new Quiz(lang);
        ScoreManager scoreManager = new ScoreManager();
        AchievementSystem achievementSystem = user.getAchievementSystem();
        MistakeManager mistakeManager = new MistakeManager();
        quiz.setServices(scoreManager, achievementSystem, mistakeManager);

        // restore previous progress for this username (if any)
        loadUserSnapshot(user);

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1) Play Quiz");
            System.out.println("2) Word Bank");
            System.out.println("3) Review Mistakes");
            System.out.println("4) Show Leaderboard");
            System.out.println("5) Show Profile");
            System.out.println("6) Save Profile");
            System.out.println("7) Exit");
            System.out.print("Choose: ");
            int choice = readInt(sc, 1, 7);
            switch (choice) {
                case 1:
                    quiz.startQuiz(user, sc);
                    break;
                case 2:
                    quiz.showWordBank();
                    break;
                case 3:
                    mistakeManager.review();
                    break;
                case 4:
                    scoreManager.showLeaderboard();
                    break;
                case 5:
                    showProfileConsole(user);
                    break;
                case 6:
                    saveUserSnapshot(user);
                    System.out.println("Profile saved.");
                    break;
                case 7:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        sc.close();
    }

    /* console helpers */

    private static void ensureDataDir() {
        File d = new File(DATA_DIR);
        if (!d.exists()) d.mkdirs();
    }

    private static int readInt(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {}
            System.out.print("Enter a valid number (" + min + "-" + max + "): ");
        }
    }

    private static void showProfileConsole(User u) {
        System.out.println("\n--- Profile ---");
        System.out.println("Username: " + u.getName());
        System.out.println("Total XP: " + u.getTotalScore());
        System.out.println("Quizzes taken: " + u.getQuizzesTaken());
        System.out.println("Achievements:");
        if (u.getAchievements().isEmpty()) {
            System.out.println(" - None yet");
        } else {
            for (String a : u.getAchievements()) {
                System.out.println(" - " + a);
            }
        }
    }

    /* shared helpers: used also by GUI launcher if you have one */

    public static void showProfileGUI(java.awt.Component parent, User u) {
        StringBuilder sb = new StringBuilder();
        sb.append("Username: ").append(u.getName()).append("\n");
        sb.append("Total XP: ").append(u.getTotalScore()).append("\n");
        sb.append("Quizzes taken: ").append(u.getQuizzesTaken()).append("\n");
        sb.append("Achievements:\n");
        if (u.getAchievements().isEmpty()) {
            sb.append(" - None yet\n");
        } else {
            for (String a : u.getAchievements()) {
                sb.append(" - ").append(a).append("\n");
            }
        }
        javax.swing.JOptionPane.showMessageDialog(parent, sb.toString(),
                "Profile", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    public static void saveUserSnapshot(User u) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            bw.write(u.getName() + "," + u.getTotalScore() + "," + u.getQuizzesTaken());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Failed to save profile: " + e.getMessage());
        }
    }

    // NEW: actually restore last saved snapshot for this username
    public static void loadUserSnapshot(User u) {
        File f = new File(USERS_FILE);
        if (!f.exists()) return;

        String username = u.getName();
        String lastLineForUser = null;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    // keep the latest entry for this username
                    lastLineForUser = line;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load profile: " + e.getMessage());
            return;
        }

        if (lastLineForUser == null) return; // first time login for this name

        String[] parts = lastLineForUser.split(",");
        try {
            int totalScore   = Integer.parseInt(parts[1]);
            int quizzesTaken = Integer.parseInt(parts[2]);

            u.setTotalScore(totalScore);
            u.setQuizzesTaken(quizzesTaken);

            // recompute achievements from stored progress
            u.getAchievementSystem().checkAchievements(totalScore, quizzesTaken);

            System.out.println("Loaded previous profile for " + username
                    + " (XP=" + totalScore + ", quizzes=" + quizzesTaken + ").");
        } catch (NumberFormatException ex) {
            System.out.println("Corrupt line for user " + username + " in users.txt");
        }
    }
}
