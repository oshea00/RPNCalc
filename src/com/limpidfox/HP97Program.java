package com.limpidfox;

import java.util.*;

public class HP97Program
{
	public boolean IsProgram;
	public boolean IsHp97Program;
	public ArrayList<Boolean> Flags;
	public ArrayList<PgmInstruction> Program;
	public ArrayList<Double> Registers;
	public angleModes AngleMode;
	public displayModes DisplayMode;
	public int DisplaySize;
	public String LabelA;
	public String Labela;
	public String LabelB;
	public String Labelb;
	public String LabelC;
	public String Labelc;
	public String LabelD;
	public String Labeld;
	public String LabelE;
	public String Labele;
	
	public HP97Program()
	{
		IsProgram = true;
		DisplayMode = displayModes.Fixed;
		AngleMode = angleModes.Degrees;
		DisplaySize = 2;
		Flags = new ArrayList<Boolean>(4);
		Program = new ArrayList<PgmInstruction>();
	}
}