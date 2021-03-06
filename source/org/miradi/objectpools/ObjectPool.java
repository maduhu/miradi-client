/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objects.BaseObject;

public class ObjectPool
{
	public ObjectPool(int objectTypeToStore)
	{
		objectType = objectTypeToStore;
		map = new HashMap<BaseId, BaseObject>();
	}
	
	public int getObjectType()
	{
		return objectType;
	}
	
	public int size()
	{
		return map.size();
	}
	
	public BaseId[] getIds()
	{
		return getRawIds().toArray(new BaseId[0]);
	}
	
	public IdList getIdList()
	{
		return new IdList(objectType, getIds());
	}
	
	public ORefSet getRefSet()
	{
		return new ORefSet(getRefList());
	}
	
	public ORefList getRefList()
	{
		return new ORefList(objectType, getIdList());
	}

	public void put(BaseObject baseObject) throws Exception
	{
		put(baseObject.getId(), baseObject);
	}
	
	public void put(BaseId id, BaseObject obj) throws Exception
	{
		if(map.containsKey(id))
			throw new RuntimeException("Id Already Exists: " + id.asInt() + " in " + getClass().getName());
		
		map.put(id, obj);
	}
	
	Set<BaseId> getRawIds()
	{
		return map.keySet();
	}
	
	Collection<BaseObject> getValues()
	{
		return map.values();
	}

	public Object getRawObject(BaseId id)
	{
		return map.get(id);
	}
	
	public void remove(BaseId id)
	{
		map.remove(id);
	}
	
	public boolean isEmpty()
	{
		return size() == 0;
	}

	private int objectType;
	private HashMap<BaseId, BaseObject> map;
}
