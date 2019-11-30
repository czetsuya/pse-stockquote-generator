package com.czetsuya.pse.utils;

public class StringUtils {

	private StringUtils() {

	}

	public static String negate(String str) {

		if (str.contains("(") && str.contains(")")) {
			str = org.apache.commons.lang3.StringUtils.substringBetween(str, "(", ")");
			str = "-" + str;
		}

		return str;
	}
}
