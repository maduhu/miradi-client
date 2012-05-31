/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.xmpz2.xmpz2schema;

import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.xmpz2.Xmpz2XmlConstants;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;

public class BaseObjectSchemaWriter implements Xmpz2XmlConstants
{
	public BaseObjectSchemaWriter(BaseObjectSchema baseObjectSchemaToUse)
	{
		baseObjectSchema = baseObjectSchemaToUse;
	}

	public String getPoolName()
	{
		return Xmpz2XmlWriter.createPoolElementName(getXmpz2ElementName());
	}
	
	public String getXmpz2ElementName()
	{
		return getBaseObjectSchema().getXmpz2ElementName();
	}
	
	public BaseObjectSchema getBaseObjectSchema()
	{
		return baseObjectSchema;
	}
	
	private BaseObjectSchema baseObjectSchema;
}
