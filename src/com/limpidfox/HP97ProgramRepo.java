package com.limpidfox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

public class HP97ProgramRepo 
{
    public static void save(HP97Program pgm, String pgmFile)
    {
    	try {
    		
			PrintStream out = new PrintStream(new FileOutputStream(pgmFile,false));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<HP97Program xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
			out.println(buildTag("IsProgram",(pgm.IsProgram) ? "true" : "false"));
			out.println(buildTag("IsHp97Program",(pgm.IsHp97Program) ? "true" : "false"));
			out.println("<Flags>");
			for (int i=0;i<pgm.Flags.size();i++)
			{
				out.println(buildTag("boolean",(pgm.Flags.get(i)) ? "true" : "false"));
			}
			out.println("</Flags>");
			out.println("<Program>");
			for (int i=0;i<pgm.Program.size();i++)
			{
				PgmInstruction p = pgm.Program.get(i);
				out.println("<PgmInstruction Label=\""+p.getLabel()+"\" KeyCode1=\""+p.getKeyCode1()+
						                                            "\" KeyCode2=\""+p.getKeyCode2()+
						                                            "\" KeyCode3=\""+p.getKeyCode3()+"\" />");
			}
			out.println("</Program>");
			
			String angleMode="";
			if (pgm.AngleMode == angleModes.Degrees)
				angleMode="Degrees";
			else
			if (pgm.AngleMode == angleModes.Grads)
				angleMode="Grads";
			else
			if (pgm.AngleMode == angleModes.Radians)
				angleMode="Radians";
			
			out.println(buildTag("AngleMode",angleMode));
			
			String displayMode="";
			
			if (pgm.DisplayMode == displayModes.Engineering)
				displayMode="Engineering";
			else
			if (pgm.DisplayMode == displayModes.Fixed)
				displayMode="Fixed";
			else
			if (pgm.DisplayMode == displayModes.Scientific)
				displayMode="Scientific";
			
			out.println(buildTag("DisplayMode",displayMode));

			out.println(buildTag("DisplaySize",""+pgm.DisplaySize));
			out.println(buildTag("LabelA",pgm.LabelA));
			out.println(buildTag("Labela",pgm.Labela));
			out.println(buildTag("LabelB",pgm.LabelB));
			out.println(buildTag("Labelb",pgm.Labelb));
			out.println(buildTag("LabelC",pgm.LabelC));
			out.println(buildTag("Labelc",pgm.Labelc));
			out.println(buildTag("LabelD",pgm.LabelD));
			out.println(buildTag("Labeld",pgm.Labeld));
			out.println(buildTag("LabelE",pgm.LabelE));
			out.println(buildTag("Labele",pgm.Labele));
			out.println("</HP97Program>");
			out.close();
		} catch (FileNotFoundException e) {
		}
    }
    
    public static String buildTag(String tagname, String value)
    {
    	if (value==null||value.length()==0)
    	    return "<"+tagname+" />";
    	else
    		return "<"+tagname+">"+value+"</"+tagname+">";
    }
    
    public static InputStream getInputStream(String pgmFile)
    {
    	try {
			return new FileInputStream(pgmFile);
		} catch (FileNotFoundException e) {
			return null;
		}
    }
    
    public static HP97Program load(String pgmFile)
    {
    	HP97Program pgm = new HP97Program();

    	try {
        	BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream(pgmFile)));
			String line = in.readLine();
			while (!line.contains("</HP97Program>"))
			{
				if (isTagIsProgram(line))
				{
					pgm.IsProgram = Boolean.parseBoolean(getTagValue(line));
				}
				else
				if (isTagIsHp97Program(line))
				{
					pgm.IsHp97Program = Boolean.parseBoolean(getTagValue(line));
				}
				else
				if (isTagAngleMode(line))
				{
					String mode = getTagValue(line);
					if (mode.equals("Degrees"))
						pgm.AngleMode = angleModes.Degrees;
					if (mode.equals("Grads"))
						pgm.AngleMode = angleModes.Grads;
					if (mode.equals("Radians"))
						pgm.AngleMode = angleModes.Radians;
				}
				else
				if (isTagDisplayMode(line))
				{
					String mode = getTagValue(line);
					if (mode.equals("Fixed"))
						pgm.DisplayMode = displayModes.Fixed;
					if (mode.equals("Engineering"))
						pgm.DisplayMode = displayModes.Engineering;
					if (mode.equals("Scientific"))
						pgm.DisplayMode = displayModes.Scientific;
				}
				else
				if (isTagDisplaySize(line))
				{
					pgm.DisplaySize = Integer.parseInt(getTagValue(line));
				}
				else
				if (isTagFlags(line))
				{
					line = in.readLine();
					while (!isTagFlagsEnd(line))
					{
						pgm.Flags.add(Boolean.parseBoolean(getTagValue(line)));
						line = in.readLine();
					}
				}
				else
				if (isTagProgram(line))
				{
					line = in.readLine();
					while (!isTagProgramEnd(line))
					{
						PgmInstruction p = new PgmInstruction();
						p.setLabel(getAttributeValue("Label",line));
						p.setKeyCode1(getAttributeValue("KeyCode1",line));
						p.setKeyCode2(getAttributeValue("KeyCode2",line));
						p.setKeyCode3(getAttributeValue("KeyCode3",line));
						pgm.Program.add(p);
						line = in.readLine();
					}
				}
				else
				if (isTagLabelA(line))
				{
					pgm.LabelA = getTagValue(line);
				}
				else
				if (isTagLabela(line))
				{
					pgm.Labela = getTagValue(line);
				}
				else
				if (isTagLabelB(line))
				{
					pgm.LabelB = getTagValue(line);
				}
				else
				if (isTagLabelb(line))
				{
					pgm.Labelb = getTagValue(line);
				}
				else
				if (isTagLabelC(line))
				{
					pgm.LabelC = getTagValue(line);
				}
				else
				if (isTagLabelc(line))
				{
					pgm.Labelc = getTagValue(line);
				}
				else
				if (isTagLabelD(line))
				{
					pgm.LabelD = getTagValue(line);
				}
				else
				if (isTagLabeld(line))
				{
					pgm.Labeld = getTagValue(line);
				}
				else
				if (isTagLabelE(line))
				{
					pgm.LabelE = getTagValue(line);
				}
				else
				if (isTagLabele(line))
				{
					pgm.Labele = getTagValue(line);
				}

				line = in.readLine();
			}

			in.close();
    	} 
    	catch (IOException e) 
    	{
		}
    	
    	return pgm;
    }

    public static boolean isTagIsProgram(String line)
    {
        return line.contains("<IsProgram>");        
    }
    
    public static String getTagValue(String line)
    {
    	String[] toks = line.split("[<>]");
    	
    	// Check for empty tag
    	if (toks.length<4)
    		return "";
    	
    	String val = toks[2].trim();
    	return val;
    }

	public static boolean isTagIsHp97Program(String line) {
        return line.contains("<IsHp97Program>");        
	}


	public static boolean isTagFlags(String line) {
        return line.contains("<Flags>");        
	}


	public static boolean isTagFlagsEnd(String line) {
        return line.contains("</Flags>");        
	}


	public static boolean isTagProgram(String line) {
        return line.contains("<Program>");        
	}
    
	public static boolean isTagProgramEnd(String line) {
        return line.contains("</Program>");        
	}
    
	public static boolean isTagAngleMode(String line) {
        return line.contains("<AngleMode>");        
	}
    
	public static boolean isTagDisplayMode(String line) {
        return line.contains("<DisplayMode>");        
	}
    
	public static boolean isTagDisplaySize(String line) {
        return line.contains("<DisplaySize>");        
	}
    
	public static boolean isTagLabelA(String line) {
        return line.contains("<LabelA>");        
	}

	public static boolean isTagLabela(String line) {
        return line.contains("<Labela>");        
	}
	public static boolean isTagLabelB(String line) {
        return line.contains("<LabelB>");        
	}
	public static boolean isTagLabelb(String line) {
        return line.contains("<Labelb>");        
	}
	public static boolean isTagLabelC(String line) {
        return line.contains("<LabelC>");        
	}
	public static boolean isTagLabelc(String line) {
        return line.contains("<Labelc>");        
	}
	public static boolean isTagLabelD(String line) {
        return line.contains("<LabelD>");        
	}
	public static boolean isTagLabeld(String line) {
        return line.contains("<Labeld>");        
	}
	public static boolean isTagLabelE(String line) {
        return line.contains("<LabelE>");        
	}
	public static boolean isTagLabele(String line) {
        return line.contains("<Labele>");        
	}

	public static String getAttributeValue(String attr, String line) 
	{
    	String[] toks = line.split("[<\"=>]");

    	for (int i=0;i<toks.length;i++)
    	{
    		if (toks[i].contains(attr))
    			return toks[i+2];
    	}
    	return "";
	}
}
