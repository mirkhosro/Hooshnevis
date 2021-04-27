package editor;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;

import util.PersianJoiner;
import dictionary.Dictionary;

public class EditorItem extends CustomItem {
	
	/**
	 * The gap between editor's borders and form's borders.
	 */
	private static final int HGAP = 2;

	private static final int VGAP = 2;

	/**
	 * The form that this item is placed in.
	 */ 
	private Form parent;
	
	/**
	 * The font used to draw text.
	 */
	private Font font;
	
	/**
	 * The mapping from number keys to Persian letters.
	 */
	private KeyMap keyMap;
	
	/**
	 * The model hold the textual data and manipulates that.
	 */
	private EditorModel model;
	
	/**
	 * The number of rows.
	 */
	private int rowsCount;
	
	/**
	 * The number of rows.
	 */
	private int startRow;

	/**
	 * displayable lines 
	 */
	private Vector linesTo;
	
	/**
	 * Width of content area
	 */
	private int width;
	
	/**
	 * Height of content area
	 */
	private int height;
	
	/**
	 * Text in joint form
	 */
	private StringBuffer jointText;
	
	/**
	 * indicates if the joint text should be reversed in order to be displayed correctly
	 * on devices that don't do this automatically
	 */
	private boolean isReverseText;
	
	/**
	 * If the text was changed. Used in paint() method.
	 */
	private boolean isTextChanged;
	
	/**
	 * Construct a new <code>EditorItem</code>.
	 * @param parent the parent form.
	 * @param dict the <code>Dictionary</code> object.
	 */
	public EditorItem(Form parent, Dictionary dict, int rowsCount, KeyMap keyMap) {
		super("");
		this.parent = parent;
		this.keyMap = keyMap;
		this.rowsCount = rowsCount;
		model = new EditorModel(dict);

		this.font = Font.getDefaultFont();
		this.width = getPrefContentWidth(0);
		this.height = getPrefContentHeight(0);
	
		linesTo = new Vector();
		jointText = new StringBuffer();
		joinerOutParams = new int[1];
	}

	protected int getMinContentHeight() {
		return font.getHeight();
	}

	protected int getMinContentWidth() {
		// return the width of the widest letter
		return font.charWidth('W') + 2;
	}

	protected int getPrefContentHeight(int width) {
		return rowsCount * font.getHeight() + 2 * VGAP;
	}

	protected int getPrefContentWidth(int height) {
		return parent.getWidth();
	}
	
	protected void sizeChanged(int w, int h) {
		this.width = w;
		this.height = h;
	}

	private int[] joinerOutParams;
	
	protected void keyPressed(int keyCode) {
		
		// handle number (letter) key press
		if (keyCode >= Canvas.KEY_NUM2 && keyCode <= Canvas.KEY_NUM9) {
			// map the number keys to letters using appropriate KeyMap class 
			char[] letters = keyMap.mappedChars(keyCode);
			model.addLetters(letters);
		}
		// handle punctuation
		if (keyCode == Canvas.KEY_NUM1) {
			//char[] puncs = keyMap.mappedLetters(keyCode);
			//model.addNonLetter(puncs);
		}
		// use UP & DOWN key for switching between words
		if (keyCode == KeyMap.KEY_DOWN) {
			//System.out.println("Key down");
			model.switchPrediction(true);
		}
		if (keyCode == KeyMap.KEY_UP){
			//System.out.println("Key down");
			model.switchPrediction(false);
		}
		
		// use # key for inserting space
		if (keyCode == Canvas.KEY_POUND) {
			model.addNonLetter(' ');
		}
		// use C key for backspace
		if (keyCode == KeyMap.KEY_CLEAR) {
			model.backspace();
		}
		
		// form the joint text
		jointText = PersianJoiner.jointForm(model.getText(), joinerOutParams);		
		indexText(jointText);
		isTextChanged = true;
		
		repaint();
	}
	
	protected void paint(Graphics g, int w, int h) {
		paintBackground(g, w, h);
		g.setFont(this.font);
		
		final int textLength = jointText.length();
		if (textLength > 0) {
			paintCursor(g);
			String text;
			if (isReverseText && isTextChanged) { 
				text = jointText.reverse().toString();
				isTextChanged = false;
			} else
				text = jointText.toString();

			final int rowHeight = font.getHeight();
			int endRow = Math.min(startRow + rowsCount, linesTo.size());
			int y = 0;
			int from = 0;
			for (int i = startRow; i < endRow; i++) {
				int to = getLinesTo(i);
				if (!isReverseText) {
					g.drawSubstring(text, from, to - from, w - HGAP, y,
							Graphics.TOP | Graphics.RIGHT);
				} else {
					g.drawSubstring(text, textLength - to, to - from, w - HGAP, y,
							Graphics.TOP | Graphics.RIGHT);					
				}
				y += rowHeight;
				from = to;
			}
		}
	}
	
	private int getLinesTo(int index) {
		return ((Integer)linesTo.elementAt(index)).intValue();
	}
	/**
	 * Prepare the background for drawing text.
	 * @param g <code>Graphics</code> object.
	 * @param w width
	 * @param h height
	 */
	private void paintBackground(Graphics g, int w, int h) {
		int oldColor = g.getColor();
		g.setColor(0x00ffffff);
		g.fillRect(0, 0, w, h);
		g.setColor(oldColor);
	}
		
	private void paintCursor(Graphics g) {
		int shrinks = joinerOutParams[0];
		final int cp = model.caretPos - shrinks; // caret position
		// find cursor's row and index of its end of row
		int cursorLineFrom = 0;
		int cursorRow;
		for (cursorRow = 0;  cp > getLinesTo(cursorRow); ++cursorRow) {
			cursorLineFrom = getLinesTo(cursorRow);
		}
		// find cursor's actual X and Y
		final int rowH = font.getHeight();
		int cursorY = VGAP + (cursorRow - startRow) * rowH;
		int cursorX = width - HGAP - 1;
		for (int i = cursorLineFrom; i < cp; i++)
			cursorX -= font.charWidth(jointText.charAt(i));
		
		// paint the cursor
		int oldColor = g.getColor();
		g.setColor(0x00777777);
		g.drawLine(cursorX, cursorY, cursorX, cursorY + rowH);
		g.drawLine(cursorX, cursorY, cursorX + 2, cursorY);
		g.setColor(oldColor);
	}
	/**
	 * Splits the text onto displayable substrings
	 */
	private void indexText(StringBuffer text) {
		linesTo.removeAllElements();
		int lastSpace = -1;
		int curWidth = 0;
		final int lineWidth = width - 2 * HGAP;
		final int textLength = text.length();
		
		// we keep the length of text up to character at position pos
		// whenever this length exceeds the length of the line, we split the
		// text from the last space, or from the last character, to a new line
		for (int pos = 0; pos < textLength; pos++) {
			char ch = text.charAt(pos);
			curWidth += font.charWidth(ch);
			
			if (curWidth > lineWidth) {
				// a new line is added
				int lineTo;
				if (lastSpace < 0 || ch == ' ') {
					// the word spans the whole line or
					// the line is breaking at a space
					lineTo = pos;
				} else
					// break from the last space
					lineTo = lastSpace + 1;
				linesTo.addElement(new Integer(lineTo));
				
				// calculate length of text moved to the next line
				curWidth = 0;
				for (int i = lineTo; i <= pos; i++)
					curWidth += font.charWidth(text.charAt(i));				
				lastSpace = -1;
			}
			if (ch == ' ')
				lastSpace = pos;
		}
		linesTo.addElement(new Integer(textLength));
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public boolean isReverseText() {
		return isReverseText;
	}

	public void setReverseText(boolean textReverse) {
		this.isReverseText = textReverse;
	}
	
	public String getText() {
		return model.getText();
	}

}
