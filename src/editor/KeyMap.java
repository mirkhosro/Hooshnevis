package editor;

import javax.microedition.lcdui.Canvas;

public class KeyMap {
	private char[][] letters;
	
	public static final KeyMap SONY_ERICSSON;
	
	public static final KeyMap NOKIA;
	
    /**
     * Key code for the UP game action.
     */
    public static final int KEY_UP = -1;

    /**
     * Key code for the DOWN game action.
     */
    public static final int KEY_DOWN = -2;

    /**
     * Key code for the LEFT game action.
     */
    public static final int KEY_LEFT = -3;

    /**
     * Key code for the RIGHT game action.
     */
    public static final int KEY_RIGHT = -4;

    /**
     * Key code for CLEAR key
     */
    public static final int KEY_CLEAR = -8;

	static {
		final char[][] seLetters = {
				{},
				{'\u062c', '\u062d', '\u062e', '\u0686'}, // 2
				{'\u0622', '\u0627', '\u0628', '\u062a', '\u062b', '\u067e'}, // 3
				{'\u0637', '\u0638', '\u0639', '\u063a'}, // 4
				{'\u0633', '\u0634', '\u0635', '\u0636'}, // 5
				{'\u062f', '\u0630', '\u0631', '\u0632', '\u0698'}, // 6
				{'\u0648', '\u064a', '\u0626'}, // 7
				{'\u0645', '\u0646', '\u0647'}, // 8
				{'\u0641', '\u0642', '\u0643', '\u0644', '\u06af'} // 9
		};
		SONY_ERICSSON = new KeyMap(seLetters);

		final char[][] nokiaLetters = {
				{},
				{'\u0628', '\u062a', '\u062b', '\u067e'}, // 2
				{'\u0627', '\u0626'}, // 3
				{'\u0633', '\u0634', '\u0635', '\u0636'}, // 4
				{'\u062f', '\u0630', '\u0631', '\u0632', '\u0698'}, // 5
				{'\u062c', '\u062d', '\u062e', '\u0686'}, // 6
				{'\u0646', '\u0647', '\u0648', '\u064a'}, // 7
				{'\u0641', '\u0642', '\u0643', '\u0644', '\u0645', '\u06af'}, // 8
				{'\u0637', '\u0638', '\u0639', '\u063a'} // 9
		};
		NOKIA = new KeyMap(nokiaLetters);
		}
	
	public KeyMap(char[][] letters) {
		this.letters = letters;
	}
	
	public char[] mappedChars(int keyCode) {
		return letters[keyCode - Canvas.KEY_NUM1];
	}
}
