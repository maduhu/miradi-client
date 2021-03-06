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

import org.miradi.ids.BaseId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.IucnRedlistSpecies;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;

public class IucnRedlistSpeciesPool extends BaseObjectPool
{
	public IucnRedlistSpeciesPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.IUCN_REDLIST_SPECIES);
	}
	
	public void put(IucnRedlistSpecies iucnRedlistSpecies) throws Exception
	{
		put(iucnRedlistSpecies.getId(), iucnRedlistSpecies);
	}
	
	public IucnRedlistSpecies find(BaseId id)
	{
		return (IucnRedlistSpecies) getRawObject(id);
	}
	
	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId)
	{
		return new IucnRedlistSpecies(objectManager, actualId);
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return IucnRedlistSpecies.createSchema(projectToUse);
	}
}
