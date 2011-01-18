package com.limpidfox;

public class PgmInstruction
{
	private String privateLabel;
	public String getLabel()
	{
		return privateLabel;
	}
	public void setLabel(String value)
	{
		privateLabel = value;
	}
	private String privateKeyCode1;
	public String getKeyCode1()
	{
		return privateKeyCode1;
	}
	public void setKeyCode1(String value)
	{
		privateKeyCode1 = value;
	}
	private String privateKeyCode2;
	public String getKeyCode2()
	{
		return privateKeyCode2;
	}
	public void setKeyCode2(String value)
	{
		privateKeyCode2 = value;
	}
	private String privateKeyCode3;
	public String getKeyCode3()
	{
		return privateKeyCode3;
	}
	public void setKeyCode3(String value)
	{
		privateKeyCode3 = value;
	}

	public PgmInstruction()
	{
	}

	public PgmInstruction(String label)
	{
		setLabel(label);
		setKeyCode1("");
		setKeyCode2("");
		setKeyCode3("");
	}

	public String getDisplayKeyCode()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getKeyCode1());
		if (getKeyCode2().length() > 0)
		{
			sb.append(" " + getKeyCode2());
		}
		if (getKeyCode3().length() > 0)
		{
			sb.append(" " + getKeyCode3());
		}
		return sb.toString();
	}
}