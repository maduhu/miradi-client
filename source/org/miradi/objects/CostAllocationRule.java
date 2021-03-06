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
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.CostAllocationRuleSchema;

public class CostAllocationRule extends BaseObject
{
	public CostAllocationRule(ObjectManager objectManager, BaseId id)
	{
		super(objectManager, id, createSchema(objectManager));
	}

	public static CostAllocationRuleSchema createSchema(Project projectToUse)
	{
		return createSchema(projectToUse.getObjectManager());
	}

	public static CostAllocationRuleSchema createSchema(ObjectManager objectManager)
	{
		return (CostAllocationRuleSchema) objectManager.getSchemas().get(ObjectType.COST_ALLOCATION_RULE);
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	
	public static final String TAG_PARENT_REF = "ParentRef";
	public static final String TAG_CHILD_REF = "ChildRef";
	public static final String TAG_COST_PERCENTAGE = "CostPercentage";
}
