import java.util.ArrayList;
import java.util.List;
public class MistakeManager {
    private List<String> mistakes = new ArrayList<>();
    public void addMistake(String question, String correct) {
        mistakes.add(question + " | Correct: " + correct);
    }
    public void review() {
        System.out.println("\n--- Mistakes Review ---");
        if (mistakes.isEmpty()) {
            System.out.println("No mistakes yet!");
            return;
        }
        for (String m : mistakes) System.out.println(m);
    }

    public void clear() {
        mistakes.clear();
    }

    public List<String> getMistakes() {
        return mistakes;
    }
}
