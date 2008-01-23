/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objectdata;

import java.awt.Dimension;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class DimensionData extends ObjectData
{

	public DimensionData()
	{
		dimension = new Dimension(0, 0);
	}

	public DimensionData(String valueToUse)
	{
		this();
		try
		{
			set(valueToUse);
		}
		catch (Exception e)
		{
			EAM.logWarning("DimensionData ignoring invalid: " + valueToUse);
		}
	}
	
	public Dimension getDimension()
	{
		return dimension;
	}
	
	public void setDimension(Dimension dimensionToUse)
	{
		dimension = dimensionToUse;
	}

	public void set(String newValue) throws Exception
	{
		if(newValue.length() == 0)
		{
			dimension = new Dimension(0, 0);
			return;
		}
		
		dimension = EnhancedJsonObject.convertToDimension(newValue);		
	}

	public String get()
	{
		if(dimension == null)
			return "";
		
		return EnhancedJsonObject.convertFromDimension(dimension);
	}
	
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof DimensionData))
			return false;
		
		DimensionData other = (DimensionData)rawOther;
		return dimension.equals(other.dimension);
	}

	public int hashCode()
	{
		return dimension.hashCode();
	}
	
	Dimension dimension;
}
