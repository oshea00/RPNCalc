package com.limpidfox;

import java.util.*;

public class HP67
{
	private HashMap<String, String> codes = new HashMap<String, String>(300);
	private ArrayList<String> keys97;
	private HashMap<String, String> codesInv = new HashMap<String, String>(300);
	private ArrayList<String> keys67;
	private String pendingSequence = "";
	private int passThisManyKeys = 0;

	private boolean _stopPrint;

	public void setDisplayHandler(IDisplayUpdateHandler handler)
	{
		_hp97.setDisplayHandler(handler);
	}
	
	public void SetStopPrint(boolean value)
	{
		_stopPrint = value;
	}

	public boolean GetStopPrint()
	{
		return _stopPrint;
	}

	public void SetPowerOn(boolean value)
	{
		_hp97.SetPowerOn(value);
	}

	public boolean GetPowerOn()
	{
		return _hp97.GetPowerOn();
	}

	public void SetRunning(boolean value)
	{
		_hp97.SetRunning(value);
	}

	public boolean GetRunning()
	{
		return _hp97.GetRunning();
	}

	public void SetRunMode(boolean value)
	{
		_hp97.SetRunMode(value);
	}

	public boolean GetRunMode()
	{
		return _hp97.GetRunMode();
	}

	public String GetDisplay()
	{
		return Translate97Codes(_hp97.GetDisplay());
	}

	private HP97 _hp97;

	public HP97 GetInnerHP97()
	{
		return _hp97;
	}

	public HP67()
	{
		_hp97 = new HP97();
		_hp97.SetHP67PrintMode(true);
		keys97 = new ArrayList<String>();
		keys67 = new ArrayList<String>();
		SetupCodeTable();
	}
	
	public void ProcessKeyInput2(String name)
	{
		ArrayList<String> hits;
		ArrayList<String> keysFor97 = new ArrayList<String>(3);
		String keyCode = ExtractKeyCode(name);
		SetStopPrint(true);

		if (passThisManyKeys > 0)
		{
			_hp97.ProcessKeyInput(GetHP97KeyName(ReplaceDigitKeys(keyCode)));
			passThisManyKeys--;
			return;
		}

		// add keyCode to sequence and search for hits
		if (currentKeyStrokes() == 2)
		{
			pendingSequence += String.format("%s", keyCode);
		}
		else
		{
			pendingSequence += String.format("%s ", keyCode);
		}

		// Search for hits:
		// Don't bother looking up these single keys for a single keystroke
		// command - they are either shift keys, or not programmable keys (SST, etc):
		// 
		// Special cases - see comments in function
		if (HandledSpecialKeyCases(keyCode) == true)
		{
			return;
		}

		// Find match
		hits = PossibleMatches(pendingSequence);
		if (hits.isEmpty())
		{
			hits = PossibleMatches(ReplaceDigitKeys(pendingSequence));
		}

		if (hits.isEmpty())
		{
			if (currentKeyStrokes() == 3)
			{
				ResetSequence();
				return;
			}
		}

		// We have an unambiguous match
		if (hits.size() == 1)
		{
			String hp97Seq = hits.get(0);
			String k1 = hp97Seq.substring(0, 3).trim();
			String k2 = hp97Seq.substring(3, 6).trim();
			String k3 = hp97Seq.substring(6, 9).trim();
			if (k1.length()>0)
			{
				_hp97.ProcessKeyInput(GetHP97KeyName(k1));
			}
			if (k2.length() > 0)
			{
				_hp97.ProcessKeyInput(GetHP97KeyName(k2));
			}
			if (k3.length() > 0)
			{
				_hp97.ProcessKeyInput(GetHP97KeyName(k3));
			}
			ResetSequence();
		}
	}

	private boolean HandledSpecialKeyCases(String keyCode)
	{
		if (currentKeyStrokes() == 1)
		{
			if (keyCode.equals("35") || keyCode.equals("32") || keyCode.equals("31"))
			{
					if (_hp97.GetRunning())
					{
						_hp97.ProcessKeyInput("F");
						ResetSequence();
						return true;
					}
					else
					{
						return true;
					}
			}
		}

		if (currentKeyStrokes()==2)
		{
			// Although a Double shift reset is good here
			if ((pendingSequence.startsWith("31") && keyCode.equals("31")) || 
				(pendingSequence.startsWith("32") && keyCode.equals("32")) || 
				(pendingSequence.startsWith("35") && keyCode.equals("35")))
			{
				ResetSequence();
				return true;
			}

			if (pendingSequence.startsWith("35") && keyCode.equals("25"))
			{
				// BST
				_hp97.ProcessKeyInput("BST");
				ResetSequence();
				return true;
			}

			if (pendingSequence.startsWith("32") && keyCode.equals("84")|| pendingSequence.startsWith("35") && keyCode.equals("74"))
			{
				// Print Stack / Register
				SetStopPrint(false);
				return false;
			}

			if (pendingSequence.startsWith("35") && keyCode.equals("22"))
			{
				if (GetRunMode() == true)
				{
					// RTN while in RunMode
					_hp97.ProcessKeyInput("RTN");
					ResetSequence();
					return true;
				}
			}

			if (pendingSequence.startsWith("35") && keyCode.equals("44"))
			{
				// F DEL
				_hp97.ProcessKeyInput("F");
				_hp97.ProcessKeyInput("ONE");
				ResetSequence();
				return true;
			}

			if (pendingSequence.startsWith("31") && keyCode.equals("44"))
			{
				// CL PRGM
				_hp97.ProcessKeyInput("F");
				_hp97.ProcessKeyInput("THREE");
				ResetSequence();
				return true;
			}

			if (pendingSequence.startsWith("31") || pendingSequence.startsWith("32") || pendingSequence.startsWith("35"))
			{
				if (keyCode.equals("35") || keyCode.equals("32") || keyCode.equals("31"))
				{
						pendingSequence=keyCode+" ";
						return true;
				}
			}

			if (pendingSequence.startsWith("22") && keyCode.equals("83"))
			{
				// Must pass the next three strokes GTO .NNN
				_hp97.ProcessKeyInput("GTO");
				_hp97.ProcessKeyInput("DEC");
				ResetSequence();
				passThisManyKeys = 3;
				return true;
			}
		}
		
		if (currentKeyStrokes() > 2 || currentKeyStrokes() == 0)
		{
			return false;
		}

		if (currentKeyStrokes() > 1)
		{
			// See if the yellow shift key was pressed
			if (pendingSequence.startsWith("31"))
			{
				if (keyCode.equals("11") || keyCode.equals("12") || keyCode.equals("13") || keyCode.equals("14") || keyCode.equals("15"))
				{
						_hp97.ProcessKeyInput("F");
						_hp97.ProcessKeyInput(GetHP97KeyName(keyCode));
						ResetSequence();
						return true;
				}
			}
			return false;
		}

		// Single Key Items:
		if (keyCode.equals("25"))
		{
			_hp97.ProcessKeyInput("SST");
			ResetSequence();
			return true;
		}

		if (keyCode.equals("24"))
		{
			_hp97.ProcessKeyInput("RCL");
			_hp97.ProcessKeyInput("IDX");
			ResetSequence();
			return true;
		}

		// Let's check for HP67 Function shorcuts on  A-E
		if (_hp97.GetProgramLenth() == 0 && GetRunMode() == true)
		{
			if (keyCode.equals("11"))
			{
					_hp97.ProcessKeyInput("RECIP");
					ResetSequence();
					return true;
			}
			else if (keyCode.equals("12"))
			{
					_hp97.ProcessKeyInput("SQR");
					ResetSequence();
					return true;
			}
			else if (keyCode.equals("13"))
			{
					_hp97.ProcessKeyInput("YX");
					ResetSequence();
					return true;
			}
			else if (keyCode.equals("14"))
			{
					_hp97.ProcessKeyInput("RDOWN");
					ResetSequence();
					return true;
			}
			else if (keyCode.equals("15"))
			{
					_hp97.ProcessKeyInput("XY");
					ResetSequence();
					return true;
			}
		}

		// Let's check for HP67 Function shorcuts on  A-E
		if (keyCode.equals("11") || keyCode.equals("12") || keyCode.equals("13") || keyCode.equals("14") || keyCode.equals("15"))
		{
				_hp97.ProcessKeyInput(GetHP97KeyName(keyCode));
				ResetSequence();
				return true;
		}

		return false;
	}	

	private ArrayList<String> PossibleMatches(String keyCodeSeq)
	{
		// Let's trim that out to a full key:
		String searchKeySeq = String.format("%9s",keyCodeSeq.trim());
		java.util.ArrayList<String> hits = new java.util.ArrayList<String>(10);
		String hit = "";

		try
		{
			hit = codesInv.get(searchKeySeq);
			if (hit==null)
				return hits;
			hits.add(hit);
			return hits;
		}
		catch (RuntimeException ex)
		{
			return hits;
		}

	}	
	
    private void ResetSequence()
    {
        pendingSequence = "";
    }

	
	private String ExtractKeyCode(String btn)
	{
		return btn.substring(3, 5);
	}

	private String ReplaceDigitKeys(String code)
	{
		code = code.replace("52", "07");
		code = code.replace("53", "08");
		code = code.replace("54", "09");
		code = code.replace("62", "04");
		code = code.replace("63", "05");
		code = code.replace("64", "06");
		code = code.replace("72", "01");
		code = code.replace("73", "02");
		code = code.replace("74", "03");
		code = code.replace("82", "00");
		return code;
	}
	
	private String ExtractAltKeyCode(String keyCode)
	{
		if (keyCode.equals("52"))
		{
				return "07";
		}
		else if (keyCode.equals("53"))
		{
				return "08";
		}
		else if (keyCode.equals("54"))
		{
				return "09";
		}
		else if (keyCode.equals("62"))
		{
				return "04";
		}
		else if (keyCode.equals("63"))
		{
				return "05";
		}
		else if (keyCode.equals("64"))
		{
				return "06";
		}
		else if (keyCode.equals("72"))
		{
				return "01";
		}
		else if (keyCode.equals("73"))
		{
				return "02";
		}
		else if (keyCode.equals("74"))
		{
				return "03";
		}
		else if (keyCode.equals("82"))
		{
				return "00";
		}
		else
		{
				return "";
		}
	}	
	
	private String GetHP97KeyName(String hp97KeyCode)
	{
		if (hp97KeyCode.equals("00"))
		{
				return "ZERO";
		}
		else if (hp97KeyCode.equals("01"))
		{
				return "ONE";
		}
		else if (hp97KeyCode.equals("02"))
		{
				return "TWO";
		}
		else if (hp97KeyCode.equals("03"))
		{
				return "THREE";
		}
		else if (hp97KeyCode.equals("04"))
		{
				return "FOUR";
		}
		else if (hp97KeyCode.equals("05"))
		{
				return "FIVE";
		}
		else if (hp97KeyCode.equals("06"))
		{
				return "SIX";
		}
		else if (hp97KeyCode.equals("07"))
		{
				return "SEVEN";
		}
		else if (hp97KeyCode.equals("08"))
		{
				return "EIGHT";
		}
		else if (hp97KeyCode.equals("09"))
		{
				return "NINE";
		}
		else if (hp97KeyCode.equals("11"))
		{
				return "A";
		}
		else if (hp97KeyCode.equals("12"))
		{
				return "B";
		}
		else if (hp97KeyCode.equals("13"))
		{
				return "C";
		}
		else if (hp97KeyCode.equals("14"))
		{
				return "D";
		}
		else if (hp97KeyCode.equals("15"))
		{
				return "E";
		}
		else if (hp97KeyCode.equals("16"))
		{
				return "F";
		}
		else if (hp97KeyCode.equals("21"))
		{
				return "LBL";
		}
		else if (hp97KeyCode.equals("22"))
		{
				return "GTO";
		}
		else if (hp97KeyCode.equals("23"))
		{
				return "GSB";
		}
		else if (hp97KeyCode.equals("24"))
		{
				return "RTN";
		}
		else if (hp97KeyCode.equals("25"))
		{
				return "BST";
		}
		else if (hp97KeyCode.equals("26"))
		{
				return "SST";
		}
		else if (hp97KeyCode.equals("31"))
		{
				return "YX";
		}
		else if (hp97KeyCode.equals("32"))
		{
				return "LN";
		}
		else if (hp97KeyCode.equals("33"))
		{
				return "EX";
		}
		else if (hp97KeyCode.equals("34"))
		{
				return "TOPOLAR";
		}
		else if (hp97KeyCode.equals("35"))
		{
				return "STO";
		}
		else if (hp97KeyCode.equals("36"))
		{
				return "RCL";
		}
		else if (hp97KeyCode.equals("41"))
		{
				return "SIN";
		}
		else if (hp97KeyCode.equals("42"))
		{
				return "COS";
		}
		else if (hp97KeyCode.equals("43"))
		{
				return "TAN";
		}
		else if (hp97KeyCode.equals("44"))
		{
				return "TORECT";
		}
		else if (hp97KeyCode.equals("45"))
		{
				return "IDX";
		}
		else if (hp97KeyCode.equals("46"))
		{
				return "RCLI";
		}
		else if (hp97KeyCode.equals("51"))
		{
				return "RS";
		}
		else if (hp97KeyCode.equals("52"))
		{
				return "RECIP";
		}
		else if (hp97KeyCode.equals("53"))
		{
				return "SQUARE";
		}
		else if (hp97KeyCode.equals("54"))
		{
				return "SQR";
		}
		else if (hp97KeyCode.equals("55"))
		{
				return "PERC";
		}
		else if (hp97KeyCode.equals("56"))
		{
				return "SUMPLUS";
		}
		else if (hp97KeyCode.equals("-11"))
		{
				return "FIX";
		}
		else if (hp97KeyCode.equals("-12"))
		{
				return "SCI";
		}
		else if (hp97KeyCode.equals("-13"))
		{
				return "ENG";
		}
		else if (hp97KeyCode.equals("-14"))
		{
				return "PRINT";
		}
		else if (hp97KeyCode.equals("-21"))
		{
				return "ENTER";
		}
		else if (hp97KeyCode.equals("-22"))
		{
				return "CHS";
		}
		else if (hp97KeyCode.equals("-23"))
		{
				return "EEX";
		}
		else if (hp97KeyCode.equals("-24"))
		{
				return "DIV";
		}
		else if (hp97KeyCode.equals("-31"))
		{
				return "RDOWN";
		}
		else if (hp97KeyCode.equals("-32"))
		{
				return "SEVEN";
		}
		else if (hp97KeyCode.equals("-33"))
		{
				return "EIGHT";
		}
		else if (hp97KeyCode.equals("-34"))
		{
				return "NINE";
		}
		else if (hp97KeyCode.equals("-35"))
		{
				return "MULT";
		}
		else if (hp97KeyCode.equals("-41"))
		{
				return "XY";
		}
		else if (hp97KeyCode.equals("-42"))
		{
				return "FOUR";
		}
		else if (hp97KeyCode.equals("-43"))
		{
				return "FIVE";
		}
		else if (hp97KeyCode.equals("-44"))
		{
				return "SIX";
		}
		else if (hp97KeyCode.equals("-45"))
		{
				return "SUB";
		}
		else if (hp97KeyCode.equals("-51"))
		{
				return "CLX";
		}
		else if (hp97KeyCode.equals("-52"))
		{
				return "ONE";
		}
		else if (hp97KeyCode.equals("-53"))
		{
				return "TWO";
		}
		else if (hp97KeyCode.equals("-54"))
		{
				return "THREE";
		}
		else if (hp97KeyCode.equals("-55"))
		{
				return "PLUS";
		}
		else if (hp97KeyCode.equals("-61"))
		{
				return "ZERO";
		}
		else if (hp97KeyCode.equals("-62"))
		{
				return "DEC";
		}
		else if (hp97KeyCode.equals("-63"))
		{
				return "DSP";
		}
		else
		{
				return "";
		}
	}
	
	private int currentKeyStrokes()
    {
        switch (pendingSequence.length())
        {
            case 0: return 0;
            case 3: return 1;
            case 6: return 2;
            case 8: return 3;
        }
        return 0;
    }

	
	private String Translate97Codes(String line)
	{
		if (!GetRunMode())
		{
			if (line.length() > 4)
			{
				String code = line.substring(6, 15);
				try
				{
					String xlate = codes.get(code);
					if (xlate == null)
						return line;
					return line.replace(code, xlate);
				}
				catch (RuntimeException ex)
				{
					return line;
				}
			}
			else
			{
				return line;
			}
		}
		else
		{
			return line;
		}
	}	
	
	private void SetupCodeTable()
	{
		codes.put("       00", "       00");
		codes.put("       01", "       01");
		codes.put("       02", "       02");
		codes.put("       03", "       03");
		codes.put("       04", "       04");
		codes.put("       05", "       05");
		codes.put("       06", "       06");
		codes.put("       07", "       07");
		codes.put("       08", "       08");
		codes.put("       09", "       09");
		codes.put("      -62", "       83");
		codes.put("       52", "    35 62");
		codes.put("    16 33", "    32 53");
		codes.put("    16 31", "    35 64");
		codes.put(" 16 22 00", " 35 61 00");
		codes.put(" 16 22 01", " 35 61 01");
		codes.put(" 16 22 02", " 35 61 02");
		codes.put(" 16 22 03", " 35 61 03");
		codes.put("      -22", "       42");
		codes.put("    16-53", "    31 43");
		codes.put("      -51", "       44");
		codes.put("       42", "    31 63");
		codes.put("    16 42", "    32 63");
		codes.put("    16-21", "    35 41");
		codes.put("      -24", "       81");
		codes.put("    16 45", "    32 73");
		codes.put("   -63 00", "    23 00");
		codes.put("   -63 01", "    23 01");
		codes.put("   -63 02", "    23 02");
		codes.put("   -63 03", "    23 03");
		codes.put("   -63 04", "    23 04");
		codes.put("   -63 05", "    23 05");
		codes.put("   -63 06", "    23 06");
		codes.put("   -63 07", "    23 07");
		codes.put("   -63 08", "    23 08");
		codes.put("   -63 09", "    23 09");
		codes.put("   -63 45", "    23 24");
		codes.put(" 16 25 46", "    31 33");
		codes.put(" 16 25 45", "    32 33");
		codes.put("      -23", "       43");
		codes.put("      -13", "    35 23");
		codes.put("      -21", "       41");
		codes.put("       33", "    32 52");
		codes.put(" 16 23 00", " 35 71 00");
		codes.put(" 16 23 01", " 35 71 01");
		codes.put(" 16 23 02", " 35 71 02");
		codes.put(" 16 23 03", " 35 71 03");
		codes.put("    16 44", "    32 83");
		codes.put("      -11", "    31 23");
		codes.put("    16-23", "    35 43");
		codes.put("    23 00", " 31 22 00");
		codes.put("    23 01", " 31 22 01");
		codes.put("    23 02", " 31 22 02");
		codes.put("    23 03", " 31 22 03");
		codes.put("    23 04", " 31 22 04");
		codes.put("    23 05", " 31 22 05");
		codes.put("    23 06", " 31 22 06");
		codes.put("    23 07", " 31 22 07");
		codes.put("    23 08", " 31 22 08");
		codes.put("    23 09", " 31 22 09");
		codes.put("    23 11", " 31 22 11");
		codes.put("    23 12", " 31 22 12");
		codes.put("    23 13", " 31 22 13");
		codes.put("    23 14", " 31 22 14");
		codes.put("    23 15", " 31 22 15");
		codes.put(" 23 16 11", " 32 22 11");
		codes.put(" 23 16 12", " 32 22 12");
		codes.put(" 23 16 13", " 32 22 13");
		codes.put(" 23 16 14", " 32 22 14");
		codes.put(" 23 16 15", " 32 22 15");
		codes.put("    23 45", " 31 22 24");
		codes.put("    22 00", "    22 00");
		codes.put("    22 01", "    22 01");
		codes.put("    22 02", "    22 02");
		codes.put("    22 03", "    22 03");
		codes.put("    22 04", "    22 04");
		codes.put("    22 05", "    22 05");
		codes.put("    22 06", "    22 06");
		codes.put("    22 07", "    22 07");
		codes.put("    22 08", "    22 08");
		codes.put("    22 09", "    22 09");
		codes.put("    22 11", "    22 11");
		codes.put("    22 12", "    22 12");
		codes.put("    22 13", "    22 13");
		codes.put("    22 14", "    22 14");
		codes.put("    22 15", "    22 15");
		codes.put(" 22 16 11", " 22 31 11");
		codes.put(" 22 16 12", " 22 31 12");
		codes.put(" 22 16 13", " 22 31 13");
		codes.put(" 22 16 14", " 22 31 14");
		codes.put(" 22 16 15", " 22 31 15");
		codes.put("    22 45", "    22 24");
		codes.put("    16 35", "    32 74");
		codes.put("    16 36", "    31 74");
		codes.put("    16-55", "    35 83");
		codes.put("    16 34", "    31 83");
		codes.put(" 16 26 46", "    31 34");
		codes.put(" 16 26 45", "    32 34");
		codes.put("    21 00", " 31 25 00");
		codes.put("    21 01", " 31 25 01");
		codes.put("    21 02", " 31 25 02");
		codes.put("    21 03", " 31 25 03");
		codes.put("    21 04", " 31 25 04");
		codes.put("    21 05", " 31 25 05");
		codes.put("    21 06", " 31 25 06");
		codes.put("    21 07", " 31 25 07");
		codes.put("    21 08", " 31 25 08");
		codes.put("    21 09", " 31 25 09");
		codes.put("    21 11", " 31 25 11");
		codes.put("    21 12", " 31 25 12");
		codes.put("    21 13", " 31 25 13");
		codes.put("    21 14", " 31 25 14");
		codes.put("    21 15", " 31 25 15");
		codes.put(" 21 16 11", " 32 25 11");
		codes.put(" 21 16 12", " 32 25 12");
		codes.put(" 21 16 13", " 32 25 13");
		codes.put(" 21 16 14", " 32 25 14");
		codes.put(" 21 16 15", " 32 25 15");
		codes.put("       32", "    31 52");
		codes.put("    16 32", "    31 53");
		codes.put("    16-63", "    35 82");
		codes.put("      -45", "       51");
		codes.put("    16-62", "    32 41");
		codes.put("    16 52", "    35 81");
		codes.put("       34", "    32 72");
		codes.put("       55", "    31 82");
		codes.put("    16 55", "    32 82");
		codes.put("    16-24", "    35 73");
		codes.put("      -55", "       61");
		codes.put("    16-13", "    35 74");
		codes.put("    16-14", "    32 84");
		codes.put("      -14", "    31 84");
		codes.put("    16-51", "    31 42");
		codes.put("    16 51", "    35 72");
		codes.put("       44", "    31 72");
		codes.put("      -31", "    35 53");
		codes.put("    16-31", "    35 54");
		codes.put("    16-22", "    35 42");
		codes.put("    16 46", "    31 73");
		codes.put("    36 00", "    34 00");
		codes.put("    36 01", "    34 01");
		codes.put("    36 02", "    34 02");
		codes.put("    36 03", "    34 03");
		codes.put("    36 04", "    34 04");
		codes.put("    36 05", "    34 05");
		codes.put("    36 06", "    34 06");
		codes.put("    36 07", "    34 07");
		codes.put("    36 08", "    34 08");
		codes.put("    36 09", "    34 09");
		codes.put("    36 11", "    34 11");
		codes.put("    36 12", "    34 12");
		codes.put("    36 13", "    34 13");
		codes.put("    36 14", "    34 14");
		codes.put("    36 15", "    34 15");
		codes.put("    36 46", "    35 34");
		codes.put("    36 45", "    34 24");
		codes.put("    16 24", "    31 24");
		codes.put("       51", "       84");
		codes.put("       24", "    35 22");
		codes.put("    16 54", "    32 21");
		codes.put("      -12", "    32 23");
		codes.put(" 16 21 00", " 35 51 00");
		codes.put(" 16 21 01", " 35 51 01");
		codes.put(" 16 21 02", " 35 51 02");
		codes.put(" 16 21 03", " 35 51 03");
		codes.put("       56", "       21");
		codes.put("    16 56", "    35 21");
		codes.put("       41", "    31 62");
		codes.put("    16 41", "    32 62");
		codes.put("    16-11", "    35 84");
		codes.put("       54", "    31 54");
		codes.put(" 35-24 00", " 33 81 00");
		codes.put(" 35-24 01", " 33 81 01");
		codes.put(" 35-24 02", " 33 81 02");
		codes.put(" 35-24 03", " 33 81 03");
		codes.put(" 35-24 04", " 33 81 04");
		codes.put(" 35-24 05", " 33 81 05");
		codes.put(" 35-24 06", " 33 81 06");
		codes.put(" 35-24 07", " 33 81 07");
		codes.put(" 35-24 08", " 33 81 08");
		codes.put(" 35-24 09", " 33 81 09");
		codes.put(" 35-45 00", " 33 51 00");
		codes.put(" 35-45 01", " 33 51 01");
		codes.put(" 35-45 02", " 33 51 02");
		codes.put(" 35-45 03", " 33 51 03");
		codes.put(" 35-45 04", " 33 51 04");
		codes.put(" 35-45 05", " 33 51 05");
		codes.put(" 35-45 06", " 33 51 06");
		codes.put(" 35-45 07", " 33 51 07");
		codes.put(" 35-45 08", " 33 51 08");
		codes.put(" 35-45 09", " 33 51 09");
		codes.put(" 35-55 00", " 33 61 00");
		codes.put(" 35-55 01", " 33 61 01");
		codes.put(" 35-55 02", " 33 61 02");
		codes.put(" 35-55 03", " 33 61 03");
		codes.put(" 35-55 04", " 33 61 04");
		codes.put(" 35-55 05", " 33 61 05");
		codes.put(" 35-55 06", " 33 61 06");
		codes.put(" 35-55 07", " 33 61 07");
		codes.put(" 35-55 08", " 33 61 08");
		codes.put(" 35-55 09", " 33 61 09");
		codes.put(" 35-35 00", " 33 71 00");
		codes.put(" 35-35 01", " 33 71 01");
		codes.put(" 35-35 02", " 33 71 02");
		codes.put(" 35-35 03", " 33 71 03");
		codes.put(" 35-35 04", " 33 71 04");
		codes.put(" 35-35 05", " 33 71 05");
		codes.put(" 35-35 06", " 33 71 06");
		codes.put(" 35-35 07", " 33 71 07");
		codes.put(" 35-35 08", " 33 71 08");
		codes.put(" 35-35 09", " 33 71 09");
		codes.put(" 35-24 45", " 33 81 24");
		codes.put(" 35-45 45", " 33 51 24");
		codes.put(" 35-55 45", " 33 61 24");
		codes.put(" 35-35 45", " 33 71 24");
		codes.put("    35 00", "    33 00");
		codes.put("    35 01", "    33 01");
		codes.put("    35 02", "    33 02");
		codes.put("    35 03", "    33 03");
		codes.put("    35 04", "    33 04");
		codes.put("    35 05", "    33 05");
		codes.put("    35 06", "    33 06");
		codes.put("    35 07", "    33 07");
		codes.put("    35 08", "    33 08");
		codes.put("    35 09", "    33 09");
		codes.put("    35 11", "    33 11");
		codes.put("    35 12", "    33 12");
		codes.put("    35 13", "    33 13");
		codes.put("    35 14", "    33 14");
		codes.put("    35 15", "    33 15");
		codes.put("    35 46", "    35 33");
		codes.put("    35 45", "    33 24");
		codes.put("    16 43", "    32 64");
		codes.put("       43", "    31 64");
		codes.put("      -35", "       71");
		codes.put("    16-61", "    31 41");
		codes.put("    16-42", "    31 61");
		codes.put("    16-43", "    31 51");
		codes.put("    16-44", "    31 81");
		codes.put("    16-45", "    31 71");
		codes.put("    16-32", "    32 61");
		codes.put("    16-33", "    32 51");
		codes.put("    16-34", "    32 81");
		codes.put("    16-35", "    32 71");
		codes.put("    16 53", "    31 21");
		codes.put("       53", "    32 54");
		codes.put("    16-41", "    35 24");
		codes.put("      -41", "    35 52");
		codes.put("       31", "    35 63");
		for (String k : codes.keySet())
		{
			keys97.add(k);
		}
		SetupReverseCodeTable();
		for (String k : codesInv.keySet())
		{
			keys67.add(k);
		}
	}	

    private void SetupReverseCodeTable()
    {
        for (String k : keys97)
        {
            codesInv.put(codes.get(k), k);
        }
    }

	public String[] PrintProgram()
	{
		// Make sure PrintQueue is empty
		_hp97.PrintQueue.clear();
		_hp97.PrintHP67Program();
		String[] lines = new String[_hp97.PrintQueue.size()];
		int cnt = 0;

		while (_hp97.PrintQueue.size()>0)
		{
			String hpline = _hp97.PrintQueue.remove();
			String code97 = hpline.substring(hpline.length() - 9, hpline.length() - 9 + 9);
			String code67 = codes.get(code97);
			lines[cnt++] = hpline.replace(code97, code67);
		}

		return lines;
	}
    
}