public class SentenceQuestion {
    private String englishMeaning;
    private String sentenceWithBlank;
    private String missingWord;
    private String difficulty;

    public SentenceQuestion(String englishMeaning, String sentenceWithBlank, String missingWord, String difficulty) {
        this.englishMeaning = englishMeaning;
        this.sentenceWithBlank = sentenceWithBlank;
        this.missingWord = missingWord;
        this.difficulty = difficulty;
    }

    public String getEnglishMeaning() {
        return englishMeaning;
    }

    public String getSentenceWithBlank() {
        return sentenceWithBlank;
    }

    public String getMissingWord() {
        return missingWord;
    }

    public String getDifficulty() {
        return difficulty;
    }
}