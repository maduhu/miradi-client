/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.objectdata;

import java.awt.Point;

import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;

public class DiagramPointData extends ObjectData
{
	public DiagramPointData(String tagToUse)
	{
		this(tagToUse, new Point(0, 0));
	}
	
	public DiagramPointData(String tagToUse, Point pointToUse)
	{
		super(tagToUse);
		
		setPoint(pointToUse);
	}

	public Point getPoint()
	{
		return point;
	}
	
	public void setPoint(Point pointToUse)
	{
		point = pointToUse;
	}
	
	@Override
	public void set(String newValue) throws Exception
	{
		if(newValue.length() == 0)
		{
			point = new Point(0, 0);
			return;
		}
		
		point = EnhancedJsonObject.convertToPoint(newValue);
	}

	@Override
	public String get()
	{
		if(point == null)
			return "";

		return EnhancedJsonObject.convertFromPoint(point);
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof DiagramPointData))
			return false;
		
		DiagramPointData other = (DiagramPointData)rawOther;
		return point.equals(other.point);
	}

	@Override
	public int hashCode()
	{
		return point.hashCode();
	}
	
	@Override
	public void writeAsXmpz2XmlData(Xmpz2XmlWriter writer, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		writer.writeDiagramPointData(baseObjectSchema, fieldSchema, point);
	}

	Point point;
}
