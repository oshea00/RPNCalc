package com.limpidfox;

public abstract class RPNCalculator
{
	private boolean isDecimalEntered;

	protected String _inputDigits;
	protected String _exponentDigits;
	protected char _exponentSign;
	protected double _exponentValue;
	protected boolean autolift;
	public boolean isEnteringData;
	protected boolean isEnteringExponent;
	protected boolean isPendingOperand;
	protected boolean isLeadingDecimal;
	protected boolean isError;

	private double _lastX;

	public void SetLastX(double value)
	{
		_lastX = value;
	}

	public double GetLastX()
	{
		return _lastX;
	}

	private double _x;

	public void SetX(double value)
	{
		_x = value;
	}

	public double GetX()
	{
		return _x;
	}

	private double _y;

	public void SetY(double value)
	{
		_y = value;
	}

	public double GetY()
	{
		return _y;
	}

	private double _z;

	public void SetZ(double value)
	{
		_z = value;
	}

	public double GetZ()
	{
		return _z;
	}

	private double unknown;

	public void SetT(double value)
	{
		unknown = value;
	}

	public double GetT()
	{
		return unknown;
	}

	public abstract void ProcessKeyStroke(String name);
	public abstract String GetDisplay();

	// Methods
	public RPNCalculator()
	{
		_inputDigits = "";
	}

	public String GetInputDigits()
	{
		return _inputDigits;
	}
	
	public void ProcessDigit(String name)
	{
		isEnteringData = true;

		if (isEnteringExponent)
		{
			String expdigit = "";
			if (name.equals("ZERO"))
			{
					expdigit = "0";
			}
			else if (name.equals("ONE"))
			{
					expdigit = "1";
			}
			else if (name.equals("TWO"))
			{
					expdigit = "2";
			}
			else if (name.equals("THREE"))
			{
					expdigit = "3";
			}
			else if (name.equals("FOUR"))
			{
					expdigit = "4";
			}
			else if (name.equals("FIVE"))
			{
					expdigit = "5";
			}
			else if (name.equals("SIX"))
			{
					expdigit = "6";
			}
			else if (name.equals("SEVEN"))
			{
					expdigit = "7";
			}
			else if (name.equals("EIGHT"))
			{
					expdigit = "8";
			}
			else if (name.equals("NINE"))
			{
					expdigit = "9";
			}
			else if (name.equals("CHS"))
			{
					_exponentSign = (_exponentSign == ' ') ? '-' : ' ';
			}
			else if (name.equals("DEC"))
			{
			}
			if (expdigit.length() > 0)
			{
				_exponentDigits = _exponentDigits.substring(_exponentDigits.length() - 1);
				_exponentDigits += expdigit;
			}
			_exponentValue = Double.parseDouble(((_exponentSign == '-') ? "-" : "") + _exponentDigits);
		}
		else
		{
			if (autolift)
			{
				LiftUpStack();
				autolift = false;
				ResetDigitFlags();
			}
			if (_inputDigits.replace(".","").length() < 10)
			{
				if (name.equals("ZERO"))
				{
						_inputDigits += "0";
				}
				else if (name.equals("ONE"))
				{
						_inputDigits += "1";
				}
				else if (name.equals("TWO"))
				{
						_inputDigits += "2";
				}
				else if (name.equals("THREE"))
				{
						_inputDigits += "3";
				}
				else if (name.equals("FOUR"))
				{
						_inputDigits += "4";
				}
 			    else if (name.equals("FIVE"))
				{
						_inputDigits += "5";
				}
				else if (name.equals("SIX"))
				{
						_inputDigits += "6";
				}
				else if (name.equals("SEVEN"))
				{
						_inputDigits += "7";
				}
				else if (name.equals("EIGHT"))
				{
						_inputDigits += "8";
				}
				else if (name.equals("NINE"))
				{
						_inputDigits += "9";
				}
				else if (name.equals("DEC"))
				{
						if (isDecimalEntered == false)
						{
							_inputDigits += ".";
							isDecimalEntered = true;
						}
				}

				if (_inputDigits.startsWith(".") && _inputDigits.length() == 1)
				{
					// This tells the display routine to display the
					// single decimal point without trying to parse it
					// as a number yet.
					isLeadingDecimal = true;
				}
				else
				{
					isLeadingDecimal = false;
					SetX(Double.parseDouble(_inputDigits));
				}
			}
		}

	}
	protected void ShiftDownStack()
	{
		SetY(GetZ());
		SetZ(GetT());
	}

	protected void LiftUpStack()
	{
		SetT(GetZ());
		SetZ(GetY());
		SetY(GetX());
	}

	protected void ResetDigitFlags()
	{
		_inputDigits = "";
		isDecimalEntered = false;
	}
}