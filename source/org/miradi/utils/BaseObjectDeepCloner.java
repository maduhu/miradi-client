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

package org.miradi.utils;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;

//FIXME urgent, this deep cloner class is still incomplete.  We will be
// fixing the owned object bug before we finish this
public class BaseObjectDeepCloner
{
	public BaseObjectDeepCloner(Project projectToUse)
	{
		project = projectToUse;
	}
	
	public ORef createDeepClone(BaseObject baseObejctToClone)
	{
		
		return ORef.INVALID;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	private Project project;
}
