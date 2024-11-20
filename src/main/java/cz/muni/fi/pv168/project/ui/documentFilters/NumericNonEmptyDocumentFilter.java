package cz.muni.fi.pv168.project.ui.documentFilters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class NumericNonEmptyDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null && string.matches("\\d+")) {
            String newText = getUpdatedText(fb.getDocument(), offset, 0, string);
            if (isValidNumber(newText)) {
                super.insertString(fb, offset, string, attr);
            }
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text != null && text.matches("\\d+")) {
            String newText = getUpdatedText(fb.getDocument(), offset, length, text);
            if (isValidNumber(newText)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        Document doc = fb.getDocument();
        String newText = getUpdatedText(doc, offset, length, "");
        if (!newText.isEmpty()) {
            super.remove(fb, offset, length);
        }
    }

    private boolean isValidNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getUpdatedText(Document doc, int offset, int length, String insertText) throws BadLocationException {
        StringBuilder currentText = new StringBuilder(doc.getText(0, doc.getLength()));
        currentText.replace(offset, offset + length, insertText);
        return currentText.toString();
    }
}