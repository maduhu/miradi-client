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

import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;

public class TaggedObjectSetPool extends BaseObjectPool
{
	public TaggedObjectSetPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.TAGGED_OBJECT_SET);
	}
	
	public void put(TaggedObjectSet taggedObjectSet) throws Exception
	{
		put(taggedObjectSet.getId(), taggedObjectSet);
	}
	
	public TaggedObjectSet find(BaseId id)
	{
		return (TaggedObjectSet) getRawObject(id);
	}
	
	public Vector<TaggedObjectSet> getAllTaggedObjectSets()
	{
		ORefList allTaggedObjectSetRefs = getORefList();
		Vector<TaggedObjectSet> allTaggedObjectSets = new Vector<TaggedObjectSet>();
		for (int index = 0; index < allTaggedObjectSetRefs.size(); ++index)
		{
			TaggedObjectSet taggedObjectSet = (TaggedObjectSet) findObject(allTaggedObjectSetRefs.get(index));
			allTaggedObjectSets.add(taggedObjectSet);
		}
			
		return allTaggedObjectSets;
	}
	
	public Vector<TaggedObjectSet> findTaggedObjectSetsWithFactor(ORef factorRef) throws Exception
	{
		Vector<TaggedObjectSet> taggedObjectSetsWithFactor = new Vector<TaggedObjectSet>();
		ORefList taggedObjectRefs = getORefList();
		for (int index = 0; index < taggedObjectRefs.size(); ++index)
		{
			TaggedObjectSet taggedObjectSet = find(taggedObjectRefs.get(index).getObjectId());
			if (taggedObjectSet.getTaggedObjectRefs().contains(factorRef))
				taggedObjectSetsWithFactor.add(taggedObjectSet);
		}
		
		return taggedObjectSetsWithFactor;
	}


	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId)
	{
		return new TaggedObjectSet(objectManager, actualId);
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return TaggedObjectSet.createSchema(projectToUse);
	}
}
