/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.objectpools;

import org.martus.util.UnicodeWriter;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;

public class EAMObjectPool extends ObjectPool
{
	public EAMObjectPool(int objectTypeToStore)
	{
		super(objectTypeToStore);
	}
	
	public BaseObject findObject(BaseId id)
	{
		return (BaseObject)getRawObject(id);
	}
	
	public BaseObject findObject(ORef ref)
	{
		if(ref.getObjectType() != getObjectType())
			return null;
		
		return findObject(ref.getObjectId());
	}
	
	public ORefList getORefList()
	{
		return new ORefList(getObjectType(), new IdList(getObjectType(), getIds()));
	}

	
	public void toXml(UnicodeWriter out) throws Exception
	{
		out.writeln("<Pool objectType='" + getObjectType() + "'>");
		ORefList ids = getSortedRefList();
		for(int i = 0; i < ids.size(); ++i)
		{
			findObject(ids.get(i)).toXml(out);
		}
		out.writeln("</Pool>");
	}

	public ORefList getSortedRefList()
	{
		ORefList sortedList = new ORefList(getObjectType(), getIdList());
		return sortedList;
	}
}
