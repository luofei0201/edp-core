package com.zero.core.baas.data.sql.token;

import com.zero.core.baas.data.sql.token.CharStream;

public class Utilities {
	public static boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

	public static boolean isLetter(char ch) {

		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
	}

	public static boolean isWhite(char ch) {
		return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
	}

	public static boolean isUnicodeBlock(char ch) {
		//UnicodeBlock判断支持,非ASCII字符
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
		return null!=ub && Character.UnicodeBlock.BASIC_LATIN!=ub;		
	}
	
	public static void skipWhite(CharStream stream) {
		if (stream.isEof())
			return;
		for (char ch = stream.get(); isWhite(ch) && stream.next(); ch = stream.get())
			;
	}

	public static boolean isSign(char ch) {
		return ch == '+' || ch == '-';
	}

}
