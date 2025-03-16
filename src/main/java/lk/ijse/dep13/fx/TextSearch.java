package lk.ijse.dep13.fx;

import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.SpinnerValueFactory;
import java.util.ArrayList;
import java.util.regex.*;

public class TextSearch {
    private final TextArea txtArea;
    private final Spinner<Integer> spnrSearch;
    private final ArrayList<int[]> matchPositions = new ArrayList<>();
    private int currentMatchIndex = -1;

    public TextSearch(TextArea txtArea, Spinner<Integer> spnrSearch) {
        this.txtArea = txtArea;
        this.spnrSearch = spnrSearch;
        this.spnrSearch.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
        this.spnrSearch.valueProperty().addListener((obs, oldVal, newVal) -> highlightMatch(newVal - 1));
    }

    public void search(String searchText) {
        searchText = searchText.trim();
        String textAreaContent = txtArea.getText();

        if (textAreaContent.isEmpty() || searchText.isEmpty()) {
            resetSpinner();
            return;
        }

        // Reset previous matches
        matchPositions.clear();
        currentMatchIndex = -1;

        // Regex pattern for partial matches
        Pattern pattern = Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(textAreaContent);

        // Find all occurrences
        while (matcher.find()) {
            matchPositions.add(new int[]{matcher.start(), matcher.end()});
        }

        // Update spinner values
        updateSpinner();

        // Highlight the first match if found
        if (!matchPositions.isEmpty()) {
            spnrSearch.getValueFactory().setValue(1); // Select first match
        }
    }

    private void highlightMatch(int index) {
        if (index >= 0 && index < matchPositions.size()) {
            int[] match = matchPositions.get(index);
            txtArea.selectRange(match[0], match[1]);
        }
    }

    private void updateSpinner() {
        int matchCount = matchPositions.size();
        if (matchCount > 0) {
            spnrSearch.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, matchCount, 1));
        } else {
            resetSpinner();
        }
    }

    private void resetSpinner() {
        spnrSearch.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
    }

    public int getOccurrenceCount() {
        return matchPositions.size();
    }
}
