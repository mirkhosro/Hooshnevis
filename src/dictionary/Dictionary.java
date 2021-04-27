package dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class Dictionary {

	private NodeTable nodeTable; // nodes of letters matched

	private Slots slots; // slots data structure loaded from dictionary file

	private int curNodeIndex; // index of the node that is being shown

	private int curLevel; // the active level in the node table

	private Node rootNode; // the root node, that contains no letter

	/**
	 * Makes a new dictionary from the given InputStream.
	 * @param in the InputStream that the Dictionary is built from.
	 * @throws IOException
	 */
	public Dictionary(InputStream in) throws IOException {
		// read data from in to slots DS
		slots = Slots.loadFromFile(in);
		nodeTable = new NodeTable();
		rootNode = new Node(null, -1);
		nodeTable.pushRow().appendToRow(0, rootNode);
		curLevel = 0;
	}
	
	/**
	 * Feeds the given letters to the dictionary and so that it can search for matches. 
	 * @param letters letters fed to dictionary for searching.
	 * @return <code>true</code> if feed was successful, i.e. some matches were found. 
	 * <code>false</code> otherwise.
	 */
	public boolean feedLetters(char[] letters) {
		int length = nodeTable.rowLength(curLevel);
		if (length > 0) { // if there is some word found yet
			// push a new row and the nodes found to it
			nodeTable.pushRow();
			for (int i = 0; i < length; i++) {
				Node curParent = nodeTable.getNodeAt(curLevel, i);
				nodeTable.appendToRow(curLevel + 1, search(curParent, letters));
			}
			curLevel++;
			curNodeIndex = -1;
			return true;
		}
		return false;
	}
	
	/**
	 * Undoes the last letter feed, so that it returns the previous state.
	 * @return <code>true</code> if fed was undone, i.e. if the dictionary was not in
	 * its initial state.
	 */
	public boolean undoFeed() {
		if (curLevel > 0) {
			curLevel--;
			nodeTable.popRow();
			curNodeIndex = -1;
			return true;
		}
		return false;
	}

	/**
	 * Increase the curNodeIndex and return next word
	 */
	public String getNextWord() {
		String nextWord = null;
		int length = nodeTable.rowLength(curLevel);
		if (length > 0) {
			curNodeIndex = (curNodeIndex + 1) % length;
			nextWord = generateWord(nodeTable.getNodeAt(curLevel, curNodeIndex));
		}
		return nextWord;
	}
	
	/**
	 * Decrease the curNodeIndex and return previous word
	 */
	public String getPrevWord() {
		String prevWord = null;
		int length = nodeTable.rowLength(curLevel);
		if (length > 0) {
			if (--curNodeIndex < 0)
				curNodeIndex = length - 1;
			prevWord = generateWord(nodeTable.getNodeAt(curLevel, curNodeIndex));
		}
		return prevWord;
	}

	public String[] getWords() {
		int length = nodeTable.rowLength(curLevel);
		String[] words = new String[length];
		for (int i = 0; i < length; i++)
			words[i] = generateWord(nodeTable.getNodeAt(curLevel, i));
		return words;
	}
	
	/**
	 * Reset the dictionary to its initial state.
	 */
	public void reset() {
		curLevel = 0;
		curNodeIndex = -1;
		nodeTable.clear();
		nodeTable.pushRow().appendToRow(0, rootNode);
	}
	
	/**
	 * Indicates whether the dictionary is at its initial state
	 * where no words are looked up.
	 * @return
	 */
	public boolean isAtInitialState() {
		return (curLevel == 0);
	}
	
	/**
	 * Make the String that ends with Node n
	 * @param n
	 * @return
	 */
	private String generateWord(Node n) {
		StringBuffer sb = new StringBuffer();
		while (n.parent != null) {
			sb.insert(0, slots.getLetterAt(n.slotIndex));
			n = n.parent;
		}
		return sb.toString();
	}

	/**
	 * Searches for the offered letters staring from
	 * where the given node points to.
	 * The offered letters should be sorted.
	 */
	private Vector search(Node n, char[] offeredLetters) {
		// In fact this function finds the intersect of
		// two sorted lists in O(m+n)
		Vector matches = new Vector();
		int offeredIndex = 0;
		int slotIndex = (n == rootNode) ? 0 : slots.getPointerAt(n.slotIndex);
		// don't search if reached a null pointer
		if (slotIndex != -1 && offeredLetters.length > 0) {
			boolean wasLastChild = false;
			do {
				char slotLetter = slots.getLetterAt(slotIndex);
				char offeredLetter = offeredLetters[offeredIndex];
				if (offeredLetter < slotLetter) {
					offeredIndex++;
				} else if (slotLetter < offeredLetter) {
					if (slots.isLastChild(slotIndex))
						wasLastChild = true;
					else
						slotIndex++;
				} else { // are equal
					matches.addElement(new Node(n, slotIndex));
					offeredIndex++;
					if (slots.isLastChild(slotIndex))
						wasLastChild = true;
					else
						slotIndex++;
				}
			} while (offeredIndex < offeredLetters.length && !wasLastChild);
		}
		return matches;
	}
}
