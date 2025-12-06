import javax.swing.*;
import java.io.*;
import java.util.*;

public class Quiz {
    private String language;
    private List<Question> wordQuestions = new ArrayList<>();
    private List<SentenceQuestion> sentenceQuestions = new ArrayList<>();
    private List<MatchPairQuestion> matchQuestions = new ArrayList<>();

    // services set by Main
    private ScoreManager scoreManager;
    private AchievementSystem achievementSystem;
    private MistakeManager mistakeManager;

    // XP values
    private static final int XP_EASY = 10;
    private static final int XP_MEDIUM = 15;
    private static final int XP_HARD = 20;

    // Files to persist mistakes (also uses MistakeManager in-memory)
    private final File mistakesFile = new File("data/mistakes.txt");

    public Quiz(String language) {
        this.language = (language == null) ? "spanish" : language.toLowerCase();
        loadWordQuestions();
        loadSentenceQuestions();
        loadMatchPairQuestions();
    }

    public void setServices(ScoreManager scoreManager, AchievementSystem achievementSystem,
                            MistakeManager mistakeManager) {
        this.scoreManager = scoreManager;
        this.achievementSystem = achievementSystem;
        this.mistakeManager = mistakeManager;
    }

    // for GUI word‑bank
    public List<Question> getWordQuestions() {
        return wordQuestions;
    }

    /* ------------- loaders ------------- */

    private void loadWordQuestions() {
        String filePath = "data/" + language + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts;
                if (line.contains(" - ")) parts = line.split(" - ");
                else parts = line.split(",", 3);
                if (parts.length >= 2) {
                    String diff = (parts.length >= 3) ? parts[2].trim() : "easy";
                    wordQuestions.add(new Question(parts[0].trim(), parts[1].trim(), diff));
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: word file not found or unreadable: " + filePath);
        }
    }

    private void loadSentenceQuestions() {
        String filePath = "data/" + language + "_sentences.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 4);
                if (parts.length >= 3) {
                    String diff = (parts.length == 4) ? parts[3].trim() : "easy";
                    sentenceQuestions.add(new SentenceQuestion(
                            parts[0].trim(), parts[1].trim(), parts[2].trim(), diff));
                }
            }
        } catch (IOException e) {
            // optional file
        }
    }

    private void loadMatchPairQuestions() {
        String filePath = "data/" + language + "_pairs.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 3);
                if (parts.length >= 2) {
                    String diff = (parts.length == 3) ? parts[2].trim() : "easy";
                    matchQuestions.add(new MatchPairQuestion(
                            parts[0].trim(), parts[1].trim(), diff));
                }
            }
        } catch (IOException e) {
            // optional file
        }
    }

    /* ------------- console entry ------------- */

    public void startQuiz(User user, Scanner sc) {
        if (user == null) {
            System.out.println("User not provided. Cannot start quiz.");
            return;
        }

        if (wordQuestions.isEmpty() && sentenceQuestions.isEmpty() && matchQuestions.isEmpty()) {
            System.out.println("No questions loaded for '" + language + "'. Check data/ files.");
            return;
        }

        System.out.println("\nChoose difficulty:");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        System.out.print("Enter choice: ");
        int diff = readInt(sc, 1, 3);
        String level = (diff == 1) ? "easy" : (diff == 2) ? "medium" : "hard";

        System.out.println("\nChoose mode:");
        System.out.println("1) Word Translation Quiz");
        System.out.println("2) Fill in the Blanks (Sentences)");
        System.out.println("3) Match the Pairs");
        System.out.println("4) Word Bank (view all words)");
        System.out.println("5) Daily Challenge (5 random)");
        System.out.println("6) Flashcards (learn mode)");
        System.out.println("7) Review Mistakes");
        System.out.print("Enter choice: ");
        int mode = readInt(sc, 1, 7);

        int roundXP = 0;
        switch (mode) {
            case 1:
                roundXP = runTranslationQuiz(level, sc);
                break;
            case 2:
                roundXP = runSentenceQuiz(level, sc);
                break;
            case 3:
                roundXP = runMatchPairs(level, sc);
                break;
            case 4:
                showWordBank();
                return;
            case 5:
                roundXP = runDailyChallenge(level, sc);
                break;
            case 6:
                runFlashcards(level, sc);
                return;
            case 7:
                if (mistakeManager != null) mistakeManager.review();
                else System.out.println("No mistake manager configured.");
                return;
            default:
                System.out.println("Invalid option. Returning to menu.");
                return;
        }

        if (roundXP > 0) {
            user.addScore(roundXP);
            System.out.println("You earned " + roundXP + " XP this round. Total XP: " + user.getTotalScore());
            if (achievementSystem != null) achievementSystem.checkAndAnnounce(user);
            if (scoreManager != null) scoreManager.saveScore(user.getName(), user.getTotalScore());
        } else {
            System.out.println("Round completed. No XP earned.");
        }
    }

    /* ------------- show word bank (console) ------------- */

    public void showWordBank() {
        if (wordQuestions.isEmpty()) {
            System.out.println("No words available for language: " + language);
            return;
        }
        System.out.println("\n--- Word Bank (" + language + ") ---");
        for (Question q : wordQuestions) {
            System.out.printf("%s  ->  %s  [%s]%n",
                    q.getWord(), q.getTranslation(), q.getDifficulty());
        }
    }

    /* ------------- console quiz implementations ------------- */

    private int runTranslationQuiz(String level, Scanner sc) {
        List<Question> pool = filterQuestions(wordQuestions, level);
        if (pool.isEmpty()) {
            System.out.println("No translation questions for " + level);
            return 0;
        }
        Collections.shuffle(pool);
        int xp = 0;
        for (Question q : pool) {
            System.out.print("Translate: " + q.getWord() + " -> ");
            String ans = sc.nextLine().trim();
            if (ans.equalsIgnoreCase(q.getTranslation())) {
                System.out.println("Correct!");
                xp += xpFor(level);
            } else {
                System.out.println("Wrong. Correct: " + q.getTranslation());
                recordMistake(q.getWord(), q.getTranslation());
            }
        }
        System.out.printf("Translation quiz finished — XP: %d (max %d)%n",
                xp, pool.size() * xpFor(level));
        return xp;
    }

    private int runSentenceQuiz(String level, Scanner sc) {
        List<SentenceQuestion> pool = filterSentenceQuestions(sentenceQuestions, level);
        if (pool.isEmpty()) {
            System.out.println("No sentence questions for " + level);
            return 0;
        }
        Collections.shuffle(pool);
        int xp = 0;
        for (SentenceQuestion sq : pool) {
            System.out.println("Meaning: " + sq.getEnglishMeaning());
            System.out.println("Sentence: " + sq.getSentenceWithBlank());
            System.out.print("Your answer: ");
            String ans = sc.nextLine().trim();
            if (ans.equalsIgnoreCase(sq.getMissingWord())) {
                System.out.println("Correct!");
                xp += xpFor(level);
            } else {
                System.out.println("Wrong. Correct: " + sq.getMissingWord());
                recordMistake(sq.getSentenceWithBlank(), sq.getMissingWord());
            }
        }
        System.out.printf("Sentence quiz finished — XP: %d (max %d)%n",
                xp, pool.size() * xpFor(level));
        return xp;
    }

    private int runMatchPairs(String level, Scanner sc) {
        List<MatchPairQuestion> pool = filterMatchQuestions(matchQuestions, level);
        if (pool.isEmpty()) {
            System.out.println("No match-pairs for " + level);
            return 0;
        }
        Collections.shuffle(pool);
        int n = Math.min(4, pool.size());
        List<MatchPairQuestion> chosen = new ArrayList<>(pool.subList(0, n));
        System.out.println("\nMatch the following words:");
        for (int i = 0; i < n; i++)
            System.out.println((i + 1) + ") " + chosen.get(i).getWord());

        List<String> options = new ArrayList<>();
        for (MatchPairQuestion m : chosen) options.add(m.getTranslation());
        Collections.shuffle(options);

        System.out.println();
        for (int i = 0; i < options.size(); i++)
            System.out.println((char) ('a' + i) + ". " + options.get(i));

        System.out.println("\nEnter pairs (example: 1-a,2-b): ");
        String line = sc.nextLine().trim();
        String[] pairs = line.split(",");
        int correct = 0;
        for (String p : pairs) {
            String[] parts = p.trim().split("-");
            if (parts.length != 2) continue;
            try {
                int left = Integer.parseInt(parts[0].trim()) - 1;
                int right = parts[1].toLowerCase().charAt(0) - 'a';
                if (left < 0 || left >= n || right < 0 || right >= n) continue;
                String correctAns = chosen.get(left).getTranslation();
                if (correctAns.equalsIgnoreCase(options.get(right))) {
                    correct++;
                } else {
                    recordMistake(chosen.get(left).getWord(),
                            chosen.get(left).getTranslation());
                }
            } catch (NumberFormatException ignored) {}
        }

        int xp = (int) Math.round(((double) correct / n) * xpFor(level) * n);
        System.out.printf("Match-pairs finished — %d/%d correct, XP: %d%n",
                correct, n, xp);
        return xp;
    }

    private int runDailyChallenge(String level, Scanner sc) {
        if (wordQuestions.size() < 5) {
            System.out.println("Not enough words for daily challenge.");
            return 0;
        }
        Collections.shuffle(wordQuestions);
        List<Question> five = wordQuestions.subList(0, 5);
        int xp = 0;
        System.out.println("\n--- Daily Challenge (5) ---");
        for (Question q : five) {
            System.out.print("Translate: " + q.getWord() + " -> ");
            String ans = sc.nextLine().trim();
            if (ans.equalsIgnoreCase(q.getTranslation())) {
                System.out.println("Correct!");
                xp += xpFor(level);
            } else {
                System.out.println("Wrong. Correct: " + q.getTranslation());
                recordMistake(q.getWord(), q.getTranslation());
            }
        }
        System.out.println("Daily challenge XP: " + xp);
        return xp;
    }

    private void runFlashcards(String level, Scanner sc) {
        List<Question> pool = filterQuestions(wordQuestions, level);
        if (pool.isEmpty()) {
            System.out.println("No flashcards for " + level);
            return;
        }
        Collections.shuffle(pool);
        System.out.println("\n--- Flashcards (press Enter to flip; type 'q' to quit) ---");
        for (Question q : pool) {
            System.out.println("Word: " + q.getWord());
            String cmd = sc.nextLine().trim();
            if ("q".equalsIgnoreCase(cmd)) break;
            System.out.println("Translation: " + q.getTranslation());
            System.out.println("Difficulty: " + q.getDifficulty());
            System.out.println("---------------------");
        }
    }

    /* ------------- shared helpers ------------- */

    private List<Question> filterQuestions(List<Question> list, String level) {
        List<Question> out = new ArrayList<>();
        for (Question q : list)
            if (q.getDifficulty().equalsIgnoreCase(level)) out.add(q);
        return out;
    }

    private List<SentenceQuestion> filterSentenceQuestions(List<SentenceQuestion> list, String level) {
        List<SentenceQuestion> out = new ArrayList<>();
        for (SentenceQuestion q : list)
            if (q.getDifficulty().equalsIgnoreCase(level)) out.add(q);
        return out;
    }

    private List<MatchPairQuestion> filterMatchQuestions(List<MatchPairQuestion> list, String level) {
        List<MatchPairQuestion> out = new ArrayList<>();
        for (MatchPairQuestion q : list)
            if (q.getDifficulty().equalsIgnoreCase(level)) out.add(q);
        return out;
    }

    private int xpFor(String level) {
        switch (level.toLowerCase()) {
            case "medium": return XP_MEDIUM;
            case "hard":   return XP_HARD;
            default:       return XP_EASY;
        }
    }

    private int readInt(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {}
            System.out.print("Enter a valid number (" + min + "-" + max + "): ");
        }
    }

    private void recordMistake(String question, String correct) {
        if (mistakeManager != null) mistakeManager.addMistake(question, correct);
        try {
            if (!mistakesFile.getParentFile().exists())
                mistakesFile.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(mistakesFile, true)) {
                fw.write(question.replaceAll("[\\r\\n]+", " ")
                        + " | Correct: " + correct + System.lineSeparator());
            }
        } catch (IOException e) {
            // ignore
        }
    }

    /* ------------- GUI entry and GUI quiz methods ------------- */

    public void startQuizGUI(JFrame parent, User user) {
        if (user == null) {
            JOptionPane.showMessageDialog(parent,
                    "User not provided. Cannot start quiz.");
            return;
        }

        if (wordQuestions.isEmpty() && sentenceQuestions.isEmpty() && matchQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No questions loaded for '" + language + "'. Check data files.");
            return;
        }

        String[] diffOptions = {"Easy", "Medium", "Hard"};
        String diff = (String) JOptionPane.showInputDialog(
                parent, "Choose difficulty:", "Difficulty",
                JOptionPane.PLAIN_MESSAGE, null, diffOptions, diffOptions[0]);
        if (diff == null) return;
        String level = diff.toLowerCase();

        String[] modeOptions = {
                "Word Translation Quiz",
                "Fill in the Blanks (Sentences)",
                "Match the Pairs",
                "Daily Challenge (5 random)",
                "Flashcards (learn mode)",
                "Review Mistakes"
        };
        String mode = (String) JOptionPane.showInputDialog(
                parent, "Choose mode:", "Mode",
                JOptionPane.PLAIN_MESSAGE, null, modeOptions, modeOptions[0]);
        if (mode == null) return;

        int roundXP = 0;

        if (mode.startsWith("Word Translation")) {
            roundXP = runTranslationQuizGUI(parent, level, user);
        } else if (mode.startsWith("Fill in the Blanks")) {
            roundXP = runSentenceQuizGUI(parent, level, user);
        } else if (mode.startsWith("Match the Pairs")) {
            roundXP = runMatchPairsGUI(parent, level, user);
        } else if (mode.startsWith("Daily Challenge")) {
            roundXP = runDailyChallengeGUI(parent, level, user);
        } else if (mode.startsWith("Flashcards")) {
            runFlashcardsGUI(parent, level);
        } else if (mode.startsWith("Review Mistakes")) {
            if (mistakeManager != null) {
                java.util.List<String> ms = mistakeManager.getMistakes();
                JTextArea area = new JTextArea();
                if (ms.isEmpty()) area.setText("No mistakes yet.");
                else for (String m : ms) area.append(m + "\n");
                area.setEditable(false);
                JOptionPane.showMessageDialog(parent,
                        new JScrollPane(area),
                        "Mistakes", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent,
                        "No mistake manager configured.");
            }
        }

        if (roundXP > 0) {
            JOptionPane.showMessageDialog(parent,
                    "You earned " + roundXP + " XP this round.\nTotal XP: " + user.getTotalScore());
            if (achievementSystem != null) achievementSystem.checkAndAnnounceGUI(parent, user);
            if (scoreManager != null) scoreManager.saveScore(user.getName(), user.getTotalScore());
        }
    }

    private int runTranslationQuizGUI(JFrame parent, String level, User user) {
        List<Question> pool = filterQuestions(wordQuestions, level);
        if (pool.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No translation questions for " + level);
            return 0;
        }
        Collections.shuffle(pool);
        int xp = 0;
        for (Question q : pool) {
            String ans = JOptionPane.showInputDialog(parent,
                    "Translate: " + q.getWord());
            if (ans == null) break;
            if (ans.equalsIgnoreCase(q.getTranslation())) {
                JOptionPane.showMessageDialog(parent, "Correct!");
                xp += xpFor(level);
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Wrong. Correct: " + q.getTranslation());
                recordMistake(q.getWord(), q.getTranslation());
            }
        }
        user.addScore(xp);
        return xp;
    }

    private int runSentenceQuizGUI(JFrame parent, String level, User user) {
        List<SentenceQuestion> pool = filterSentenceQuestions(sentenceQuestions, level);
        if (pool.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No sentence questions for " + level);
            return 0;
        }
        Collections.shuffle(pool);
        int xp = 0;
        for (SentenceQuestion sq : pool) {
            String text = "Meaning: " + sq.getEnglishMeaning()
                    + "\nSentence: " + sq.getSentenceWithBlank()
                    + "\n\nYour answer:";
            String ans = JOptionPane.showInputDialog(parent, text);
            if (ans == null) break;
            if (ans.equalsIgnoreCase(sq.getMissingWord())) {
                JOptionPane.showMessageDialog(parent, "Correct!");
                xp += xpFor(level);
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Wrong. Correct: " + sq.getMissingWord());
                recordMistake(sq.getSentenceWithBlank(), sq.getMissingWord());
            }
        }
        user.addScore(xp);
        return xp;
    }

    private int runMatchPairsGUI(JFrame parent, String level, User user) {
        List<MatchPairQuestion> pool = filterMatchQuestions(matchQuestions, level);
        if (pool.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No match-pairs for " + level);
            return 0;
        }
        Collections.shuffle(pool);
        int n = Math.min(4, pool.size());
        List<MatchPairQuestion> chosen = new ArrayList<>(pool.subList(0, n));

        StringBuilder sb = new StringBuilder();
        sb.append("Match the following words:\n");
        for (int i = 0; i < n; i++) {
            sb.append((i + 1)).append(") ").append(chosen.get(i).getWord()).append("\n");
        }

        List<String> options = new ArrayList<>();
        for (MatchPairQuestion m : chosen) options.add(m.getTranslation());
        Collections.shuffle(options);
        sb.append("\nOptions:\n");
        for (int i = 0; i < options.size(); i++) {
            sb.append((char) ('a' + i)).append(") ").append(options.get(i)).append("\n");
        }
        sb.append("\nEnter pairs (example: 1-a,2-b): ");

        String line = JOptionPane.showInputDialog(parent, sb.toString());
        if (line == null) return 0;
        String[] pairs = line.split(",");
        int correct = 0;
        for (String p : pairs) {
            String[] parts = p.trim().split("-");
            if (parts.length != 2) continue;
            try {
                int left = Integer.parseInt(parts[0].trim()) - 1;
                int right = parts[1].toLowerCase().charAt(0) - 'a';
                if (left < 0 || left >= n || right < 0 || right >= n) continue;
                String correctAns = chosen.get(left).getTranslation();
                if (correctAns.equalsIgnoreCase(options.get(right))) {
                    correct++;
                } else {
                    recordMistake(chosen.get(left).getWord(),
                            chosen.get(left).getTranslation());
                }
            } catch (NumberFormatException ignored) {}
        }
        int xp = (int) Math.round(((double) correct / n) * xpFor(level) * n);
        JOptionPane.showMessageDialog(parent,
                "Match-pairs finished — " + correct + "/" + n + " correct, XP: " + xp);
        user.addScore(xp);
        return xp;
    }

    private int runDailyChallengeGUI(JFrame parent, String level, User user) {
        if (wordQuestions.size() < 5) {
            JOptionPane.showMessageDialog(parent,
                    "Not enough words for daily challenge.");
            return 0;
        }
        Collections.shuffle(wordQuestions);
        List<Question> five = wordQuestions.subList(0, 5);
        int xp = 0;
        for (Question q : five) {
            String ans = JOptionPane.showInputDialog(parent,
                    "Translate (Daily Challenge): " + q.getWord());
            if (ans == null) break;
            if (ans.equalsIgnoreCase(q.getTranslation())) {
                JOptionPane.showMessageDialog(parent, "Correct!");
                xp += xpFor(level);
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Wrong. Correct: " + q.getTranslation());
                recordMistake(q.getWord(), q.getTranslation());
            }
        }
        user.addScore(xp);
        JOptionPane.showMessageDialog(parent, "Daily challenge XP: " + xp);
        return xp;
    }

    private void runFlashcardsGUI(JFrame parent, String level) {
        List<Question> pool = filterQuestions(wordQuestions, level);
        if (pool.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No flashcards for " + level);
            return;
        }
        Collections.shuffle(pool);
        for (Question q : pool) {
            int res = JOptionPane.showConfirmDialog(
                    parent,
                    "Word: " + q.getWord() + "\n\nShow translation?",
                    "Flashcard",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.CANCEL_OPTION
                    || res == JOptionPane.CLOSED_OPTION) break;
            if (res == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(parent,
                        "Translation: " + q.getTranslation()
                                + "\nDifficulty: " + q.getDifficulty());
            }
        }
    }
}
