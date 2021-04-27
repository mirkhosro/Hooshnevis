package util;

/**
 * When a <code>String</code> in Persian Unicode range is given to the
 * <code>adaptedForm</code> of this class, it returns the same string but
 * made of presentation form characters. It is useful for devices that do not
 * automatically join the Persian letters.
 * @author Amir
 *
 */
public class PersianJoiner {
	private static short map[] = new short[213];

	static {
		map[0x21] = (short) 0xFE80; // Á
		map[0x22] = (short) 0xFE81; // Â
		map[0x23] = (short) 0xFE83; // Ã
		map[0x24] = (short) 0xFE85; // Ä
		map[0x25] = (short) 0xFE87; // Å
		map[0x26] = (short) 0xFE89; // Æ
		map[0x27] = (short) 0xFE8D; // Ç
		map[0x28] = (short) 0xFE8F; // È
		map[0x29] = (short) 0xFE93; // É
		map[0x2A] = (short) 0xFE95; // Ê
		map[0x2B] = (short) 0xFE99; // Ë
		map[0x2C] = (short) 0xFE9D; // Ì
		map[0x2D] = (short) 0xFEA1; // Í
		map[0x2E] = (short) 0xFEA5; // Î
		map[0x2F] = (short) 0xFEA9; // Ï
		map[0x30] = (short) 0xFEAB; // Ð
		map[0x31] = (short) 0xFEAD; // Ñ
		map[0x32] = (short) 0xFEAF; // Ò
		map[0x33] = (short) 0xFEB1; // Ó
		map[0x34] = (short) 0xFEB5; // Ô
		map[0x35] = (short) 0xFEB9; // Õ
		map[0x36] = (short) 0xFEBD; // Ö
		map[0x37] = (short) 0xFEC1; // Ø
		map[0x38] = (short) 0xFEC5; // Ù
		map[0x39] = (short) 0xFEC9; // Ú
		map[0x3A] = (short) 0xFECD; // Û
		map[0x41] = (short) 0xFED1; //
		map[0x42] = (short) 0xFED5; // Þ
		map[0x43] = (short) 0xFED9; // ß
		map[0x44] = (short) 0xFEDD; // á
		map[0x45] = (short) 0xFEE1; // ã
		map[0x46] = (short) 0xFEE5; // ä
		map[0x47] = (short) 0xFEE9; // å
		map[0x48] = (short) 0xFEED; // æ
		map[0x49] = (short) 0xFEEF; // ì alef maghsure
		map[0x4A] = (short) 0xFEF1; // í

		map[0x7E] = (short) 0xFB56; // 
		map[0x86] = (short) 0xFB7A; // 
		map[0x98] = (short) 0xFB8A; // Ž
		map[0xAF] = (short) 0xFB92; // 

		map[0xA9] = (short) 0xFB8E; //
		map[0xCC] = (short) 0xFBFC; //
		map[0xC0] = (short) 0xFBA4; // É
	}

	public static StringBuffer jointForm(String inText, int[] outParams) {
		if (inText == null)
			return null;

		int length = inText.length();
		StringBuffer outText = new StringBuffer(length);
		if (length == 0)
			return outText;
		// append a space at the end as a non-joining letter
		inText = inText + " ";
		//inText.append(' ');

		boolean prevLeftJoint = false;
		boolean thisRightJoint = false;
		boolean thisLeftJoint = false;
		boolean nextRightJoint = false;
		int shrinks = 0; // number of times two letters L & A shrink into LA
		

		char letter = inText.charAt(0);
		for (int i = 0; i < length; i++) {
			char nextLetter = inText.charAt(i + 1);
			thisLeftJoint = isLeftJoint(letter);
			nextRightJoint = isRightJoint(nextLetter);
			
			// add the formed letter to output string according to
			// joining conditions if it's Persian

			// other cases than LA & LLH
			boolean otherCases = true;
			// lam special case
			if (letter == 0x644) {
				// LA
				if (nextLetter == 0x627 || nextLetter == 0x622
						|| nextLetter == 0x623 || nextLetter == 0x625) {
					outText.append(formedLA(nextLetter, prevLeftJoint));
					prevLeftJoint = false; // alef of LA is not left joint
					letter = inText.charAt(i + 2); // after next
					i++;
					shrinks++;
					otherCases = false;
				} 
			} 
			if (otherCases) {
				if (isPersian(letter)) {
					outText.append(formedLetter(letter, prevLeftJoint
							&& thisRightJoint, thisLeftJoint && nextRightJoint));					
				} else
					outText.append(letter);
				// go to the next letter
				prevLeftJoint = thisLeftJoint;
				thisRightJoint = nextRightJoint;
				letter = nextLetter;
			}
		}
		
		// delete the appended char
		//inText.deleteCharAt(length - 1);
		if (outParams != null && outParams.length > 0) {
			outParams[0] = shrinks;
		}
		
		// return the output value
		return outText;
	}

	private static char formedLetter(char letter, boolean rightJoint,
			boolean leftJoint) {
		// form letters only in Farsi range
		int offset = 0;

		if (rightJoint)
			offset += 1;

		if (leftJoint)
			offset += 2;

		// return the proper letter :
		// offset = 0 -> isolated
		// offset = 1 -> final
		// offset = 2 -> initial
		// offset = 3 -> middle
		int start = map[letter - 0x600];
		if (start == 0)
			start = letter;
		return (char) (start + offset);
	}

	private static char formedLA(char letter2, boolean right_joint) {
		int offset = right_joint ? 1 : 0;
		int code = 0;

		switch (letter2) {
		case 0x622:
			code = 0xFEF5;
			break;
		case 0x623:
			code = 0xFEF7;
			break;
		case 0x625:
			code = 0xFEF9;
			break;
		case 0x627:
			code = 0xFEFB;
			break;
		default:
			throw new RuntimeException("LA without alef after lam.");
		}
		return (char) (code + offset);
	}

	private static boolean isLeftJoint(char letter) {
		// return true if a letter can be joint from left
		// and false otherwise
		int code = (int) letter;
		boolean ret = false;

		if (code == 0x628 || code >= 0x62a && code <= 0x62e || code >= 0x633
				&& code <= 0x63A || code >= 0x640 && code <= 0x647
				|| code == 0x649 || code == 0x64A || code == 0x672
				|| code == 0x67E || code == 0x686 || code == 0x6A9 
				|| code == 0x6AA || code == 0x6AF || code == 0x6CC)
			ret = true;

		return ret;
	}

	private static boolean isRightJoint(char letter) {
		return letter != '\u0621' && letter != ' ';
	}

	private static boolean isPersian(char letter) {
		return letter >= 0x621 && letter <= 0x6C0;
	}
}
