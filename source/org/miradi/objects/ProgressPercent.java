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
package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.GoalSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.schemas.ProgressPercentSchema;

public class ProgressPercent extends BaseObject
{
	public ProgressPercent(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, createSchema(objectManager));
	}

	public static ProgressPercentSchema createSchema(Project projectToUse)
	{
		return createSchema(projectToUse.getObjectManager());
	}

	public static ProgressPercentSchema createSchema(ObjectManager objectManager)
	{
		return (ProgressPercentSchema) objectManager.getSchemas().get(ObjectType.PROGRESS_PERCENT);
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return new int[] {
			GoalSchema.getObjectType(), 
			ObjectiveSchema.getObjectType(),
			};
	}
	
	@Override
	public String getLabel()
	{
		return getData(TAG_DATE);	
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == ProgressPercentSchema.getObjectType();
	}
	
	public static ProgressPercent find(ObjectManager objectManager, ORef progressPercentRef)
	{
		return (ProgressPercent) objectManager.findObject(progressPercentRef);
	}
	
	public static ProgressPercent find(Project project, ORef progressPercentRef)
	{
		return find(project.getObjectManager(), progressPercentRef);
	}
	
	public static final String TAG_DATE = "PercentDate";
	public static final String TAG_PERCENT_COMPLETE = "PercentComplete";
	public static final String TAG_PERCENT_COMPLETE_NOTES = "PercentCompleteNotes";
}
