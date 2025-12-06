
public class Question {
    String word;
    String translation;
    String difficulty;
    public Question(String word, String translation, String difficulty) {
        this.word = word;
        this.translation = translation;
        this.difficulty = difficulty;
    }
    public String getWord() {
        return word;
    }
    public String getTranslation() {
        return translation;
    }
    public String getDifficulty() {
        return difficulty;
    }
}
