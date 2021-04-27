package dictionary;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Slots {
	private char[] letter;

	private short[] pointer;

	private byte [] flags;
	
	private short size;
	
	private static final int FLAG_IS_LAST_CHILD = 1;

	private static final int FLAG_IS_WORD_COMPLETE = 2;

	// main is for testing purposes
//	public static void main(String[] args) throws IOException {
//		InputStream is = new FileInputStream("dict.bin");
//		Slots slots = Slots.loadFromFile(is);
//		is.close();
//		SlotList sl = new SlotList();
//		for (int i = 0; i < slots.size(); i++) {
//			SlotList.Slot s = new SlotList.Slot(slots.letter[i]);
//			s.pointer = slots.pointer[i];
//			s.isLastChild = slots.getIsLastChild(i);
//			s.isWordComplete = slots.getIsWordComplete(i);
//			sl.add(s);
//		}
//		File outFile = new File("bin_read.txt");
//		sl.printToFileAsText(outFile);
//	}
	
	private Slots(char[] letter, short[] pointer, byte[] flags) {
		if (letter.length != pointer.length || letter.length != flags.length)
			throw new RuntimeException("Lengths arrays not equal!");
		else {
			this.letter = letter;
			this.pointer = pointer;
			this.flags = flags;
			size = (short)letter.length;
		}
	}

	public static Slots loadFromFile(InputStream in) throws IOException {
		DataInputStream dos = new DataInputStream(in);
		short version = dos.readShort();
		// check version
		if (version != 1) {
			throw new IOException(
					"The dictionary file is for a newer version\n"
							+ "and cannot be read!");
		}
		// read dictionary file
		short size = dos.readShort();
		char[] letter = new char[size];
		short[] pointer = new short[size];
		byte[] flags = new byte[size];
		for (int i = 0; i < size; i++) {
			byte b = dos.readByte();
			letter[i] = unmapToChar(b);
			pointer[i] = dos.readShort();
			flags[i] = (byte)(b >> 6);
		}
		return new Slots(letter, pointer, flags);
	}
	
	private static char unmapToChar(byte b) {
		final char ALPHABET_OFFSET = 0x620;
		// get the original char
		char c = (char)((b & 63) + ALPHABET_OFFSET);
		// map Pe, Che, Zhe, Gaf to the free range in unicode table
		switch (c) {
		case '\u063b': // Pe
			c = '\u067e';
			break;
		case '\u063c': // Che
			c = '\u0686';
			break;
		case '\u063d': // Zhe
			c = '\u0698';
			break;
		case '\u063e': // Gaf
			c = '\u06af';
			break;
		}
		return c;
	}
	
	public char getLetterAt(int index) {
		return letter[index];
	}
	
	public short getPointerAt(int index) {
		return pointer[index];
	}
	
	public boolean isLastChild(int index) {
		return (flags[index] & FLAG_IS_LAST_CHILD) != 0;
	}

	public boolean isWordComplete(int index) {
		return (flags[index] & FLAG_IS_WORD_COMPLETE) != 0;
	}
	
	public short size() {
		return size;
	}
}
