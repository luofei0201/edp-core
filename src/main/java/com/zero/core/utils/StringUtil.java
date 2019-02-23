package com.zero.core.utils;

import java.math.BigDecimal;

public class StringUtil {

	public static boolean isEmpty(Object object) {
		if (object == null)
			return true;
		if ("".equals(String.valueOf(object)))
			return true;
		return false;
	}

	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	

	public static void main(String[] args) {
	}

}
