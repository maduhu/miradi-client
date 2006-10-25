/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objectdata;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.utils.InvalidNumberException;

public class NumberData extends ObjectData
{
	public NumberData()
	{
		value = Double.NaN;
	}
	
	public NumberData(String valueToUse)
	{
		this();
		try
		{
			set(valueToUse);
		}
		catch (Exception e)
		{
			EAM.logDebug("NumberData ignoring invalid: " + valueToUse);
		}
	}

	public void set(String newValue) throws Exception
	{
		if(newValue.length() == 0)
		{
			value = Double.NaN;
			return;
		}
		
		try
		{
			value = Double.parseDouble(newValue);
		}
		catch (NumberFormatException e)
		{
			throw new InvalidNumberException(e);
		}
	}

	public String get()
	{
		if(new Double(value).isNaN())
			return "";
		return Double.toString(value);
	}
	
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof NumberData))
			return false;
		
		NumberData other = (NumberData)rawOther;
		return new Double(value).equals(new Double(other.value));
	}

	public int hashCode()
	{
		return (int)value;
	}


	double value;
}
