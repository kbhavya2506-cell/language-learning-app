public class User {
    private String name;
    private int totalScore = 0;
    private int quizzesTaken = 0;
    private ProgressTracker progressTracker = new ProgressTracker();
    private AchievementSystem achievementSystem = new AchievementSystem();

    public User(String name) {
        this.name = name;
    }

    public void addScore(int s) {
        totalScore += s;
        quizzesTaken++;
        achievementSystem.checkAchievements(totalScore, quizzesTaken);
    }

    public String getName() {
        return name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getQuizzesTaken() {
        return quizzesTaken;
    }

    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public AchievementSystem getAchievementSystem() {
        return achievementSystem;
    }

    public java.util.List<String> getAchievements() {
        return achievementSystem.getAchievements();
    }
    public void setTotalScore(int s) {
        this.totalScore = s;
    }

    public void setQuizzesTaken(int q) {
        this.quizzesTaken = q;
    }
}
