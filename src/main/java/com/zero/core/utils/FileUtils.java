package com.zero.core.utils;

import java.math.BigDecimal;

public class FileUtils {
	
	public static float bytes2kb(long bytes) {
		BigDecimal filesize = new BigDecimal(bytes);
		BigDecimal megabyte = new BigDecimal(1024 * 1024);
		 float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
		 BigDecimal   b  =   new BigDecimal(returnValue);  
		if (returnValue > 1)
			return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
		b  =   new BigDecimal(returnValue);  
		return  b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public static String docType(String docName) {
		String prefix = docName.substring(docName.lastIndexOf(".") + 1);
		return prefix;
	}
	
	public static void main(String[] args) {
		System.out.println(docType("Application.java"));
	}

}
