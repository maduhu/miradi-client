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
package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.TaggedObjectSetSchema;

public class TaggedObjectSet extends BaseObject
{
	public TaggedObjectSet(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, new TaggedObjectSetSchema());
	}
		
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	@Override
	public String getShortLabel()
	{
		return getData(TAG_SHORT_LABEL);
	}
	
	@Override
	public boolean isRefList(String tag)
	{
		if (tag.equals(TAG_TAGGED_OBJECT_REFS))
			return true;
		
		return super.isRefList(tag);
	}
	
	public ORefList getTaggedObjectRefs()
	{
		return getSafeRefListData(TAG_TAGGED_OBJECT_REFS);
	}
	
	public ORefSet getTaggedObjectRefsSet()
	{
		return new ORefSet(getTaggedObjectRefs());
	}
		
	public static boolean is(BaseObject object)
	{
		return is(object.getRef());
	}

	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == TaggedObjectSetSchema.getObjectType();
	}
	
	public static TaggedObjectSet find(ObjectManager objectManager, ORef objectTagRef)
	{
		return (TaggedObjectSet) objectManager.findObject(objectTagRef);
	}
	
	public static TaggedObjectSet find(Project project, ORef eportTemplateRef)
	{
		return find(project.getObjectManager(), eportTemplateRef);
	}

	public static final String TAG_SHORT_LABEL = "ShortLabel";
	public static final String TAG_TAGGED_OBJECT_REFS = "TaggedObjectRefs";
	public static final String TAG_COMMENTS = "Comments";
}
