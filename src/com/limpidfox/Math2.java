package com.limpidfox;

public class Math2 {

	public static final double Truncate(double val)
	{
		long intpart = (long) val;
		return (double) intpart;
	}
	
	public static final int SignificantDigitsToLeft(double num)
	{
		return String.format("%d", (int) Truncate(num)).replace("-", "").length();
	}
	
}
