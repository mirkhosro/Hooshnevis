package editor;

import dictionary.Dictionary;

/**
 * Manipulate text data and lays it out in lines.
 * 
 * @author Amir
 * 
 */
public class EditorModel {
	/**
	 * The position of caret in the text.
	 */
	int caretPos;

	/**
	 * The text.
	 */
	private StringBuffer text;

	/**
	 * The Active Word, that is being searched in the dictionary.
	 */
	private String aw;

	/**
	 * Active Word's beginning index in text.
	 */
	private int awStart;

	/**
	 * The dictionary for looking up words.
	 */
	private Dictionary dict;

	public EditorModel(Dictionary dict) {
		text = new StringBuffer();
		this.dict = dict;
		caretPos = 0;
	}
	
	/**
	 * Sends a request to the <code>EditorModel</code> to delete the character
	 * before the caret. 
	 * @return <code>true</code> if a character was deleted.
	 */
	public boolean backspace() {
		if (aw == null) {
			// simple delete. No look up.
			if (caretPos > 0) {
				text.deleteCharAt(--caretPos);
				return true;
			} else
				return false;
		} else {
			// delete the active word's letters
			dict.undoFeed();
			text.delete(awStart, caretPos);
			if (!dict.isAtInitialState()) {
				// the active word is not wholly deleted
				String result = dict.getNextWord();
				text.insert(awStart, result);
			} else {
				// active word is wholly deleted
				aw = null;
			}
			caretPos--;
			return true;
		}
	}

	public boolean addLetters(char[] letters) {
		// when starting to enter a new word
		if (aw == null) {
			awStart = caretPos;
			dict.reset();
		}

		dict.feedLetters(letters);
		String result = dict.getNextWord();
		if (result == null) {
			// word not in dictionary!
			dict.undoFeed();
			return false;
		}
		// set Active Word to result
		aw = result;
		caretPos = awStart + aw.length();
		// update text
		text.delete(awStart, caretPos - 1);
		text.insert(awStart, aw);
		
		return true;
	}

	public boolean addNonLetter(char ch) {
		aw = null;
		text.insert(caretPos, ch);
		caretPos++;
		return true;
	}
	
	/**
	 * Ask the <code>EditorModel</code> to find the next prediction
	 * if a word is already active for searching.
	 * @param isNext if <code>true</code> will switches to the next prediction,
	 * and if <code>false</code>, switches to the previous one.
	 * @return <code>true</code> if a word is active, esle otherwise.
	 */
	public boolean switchPrediction(boolean isNext) {
		if (aw != null) {
			String result;
			if (isNext)
				result = dict.getNextWord();
			else
				result = dict.getPrevWord();
			
			aw = result;
			// update text
			text.delete(awStart, caretPos);
			text.insert(awStart, aw);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the text of the <code>EditorModel</code>.
	 * @return the text of <code>EditorModel</code>.
	 */
	public String getText() {
		return text.toString();
	}

}
