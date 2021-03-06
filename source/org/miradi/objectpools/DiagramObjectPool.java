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

import java.util.Comparator;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.DiagramObject;

public abstract class DiagramObjectPool extends BaseObjectPool
{
	public DiagramObjectPool(IdAssigner idAssignerToUse, int objectTypeToStore)
	{
		super(idAssignerToUse, objectTypeToStore);
	}

	@Override
	public ORefList getSortedRefList()
	{
		ORefList refs = super.getSortedRefList();
		refs.sort(new DiagramObjectComparator());
		return refs;
	}

	public DiagramObject findDiagramObject(BaseId id)
	{
		return (DiagramObject)getRawObject(id);
	}

	public DiagramObject[] getAllDiagramObjects()
	{
		BaseId[] allIds = getIds();
		DiagramObject[] allDiagramObjects = new DiagramObject[allIds.length];
		for (int i = 0; i < allDiagramObjects.length; i++)
			allDiagramObjects[i] = findDiagramObject(allIds[i]);

		return allDiagramObjects;
	}

	class DiagramObjectComparator implements Comparator<ORef>
	{
		public int compare(ORef ref1, ORef ref2)
		{
			if(ref1 == null && ref2 == null)
				return 0;
			if(ref1 == null)
				return -1;
			if(ref2 == null)
				return 1;
			
			String label1 = findObject(ref1).toString();
			String label2 = findObject(ref2).toString();
			return label1.compareToIgnoreCase(label2);
		}
	}
}
