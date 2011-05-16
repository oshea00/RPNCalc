package com.limpidfox;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class HP97DataRepo 
{
    public static void save(HP97Program pgm, String pgmFile)
    {
    	try {
    		
			PrintStream out = new PrintStream(new FileOutputStream(pgmFile,false));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<HP97Program xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
			out.println(buildTag("IsProgram",(pgm.IsProgram) ? "true" : "false"));
			out.println(buildTag("IsHp97Program",(pgm.IsHp97Program) ? "true" : "false"));
			out.println("<Registers>");
			for (int i=0;i<pgm.Registers.size();i++)
			{
				out.println(buildTag("double",Double.toString(pgm.Registers.get(i))));
			}
			out.println("</Registers>");
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
				if (isTagRegisters(line))
				{
					line = in.readLine();
					while (!isTagRegistersEnd(line))
					{
						pgm.Registers.add(Double.parseDouble(getTagValue(line)));
						line = in.readLine();
					}
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


	public static boolean isTagRegisters(String line) {
        return line.contains("<Registers>");        
	}


	public static boolean isTagRegistersEnd(String line) {
        return line.contains("</Registers>");        
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
