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
package org.miradi.views;

import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.views.umbrella.ObjectPicker;

abstract public class ObjectsDoer extends ViewDoer
{
	@Override
	public boolean isAvailable()
	{
		if(!super.isAvailable())
			return false;
		
		return (getPicker() != null);
	}
	
	@Override
	public void setPicker(ObjectPicker pickerToUse)
	{
		picker = pickerToUse;
	}
	
	public ObjectPicker getPicker()
	{
		return picker;
	}
	
	public TreeTableNode[] getSelectedTreeNodes()
	{
		if(picker == null)
			return new TreeTableNode[0];
		return picker.getSelectedTreeNodes();
	}
	
	public ORefList getSelectionHierarchy()
	{
		if (picker == null)
			return new ORefList();
		
		return picker.getSelectionHierarchy();
	}
	
	public ORefList[] getSelectedHierarchies()
	{
		if (picker == null)
			return new ORefList[0];
		
		return picker.getSelectedHierarchies();
	}
	
	public BaseObject[] getObjects()
	{
		if(picker == null)
			return new BaseObject[0];
		
		return picker.getSelectedObjects();
	}
	
	protected BaseObject getSingleSelectedObject()
	{
		BaseObject[] selectedObjects = getObjects();
		if (selectedObjects == null)
			return null;

		if(selectedObjects.length != 1)
			return null;
		
		return selectedObjects[0];
	}
	
	public BaseObject getSingleSelected(int type)
	{
		if(picker == null)
			return null;
		
		if (picker.getSelectedHierarchies().length == 0)
			return null;
		
		ORefList selectionHierarchy = picker.getSelectedHierarchies()[0];
		ORefList extractedList = selectionHierarchy.getFilteredBy(type);
		if (extractedList.size() == 0)
			return null;
		
		return getProject().findObject(extractedList.get(0));
	}
	
	public BaseId[] getSelectedIds()
	{
		BaseObject[] selectedObjects = getObjects();
		if (selectedObjects == null)
		{
			BaseId[] emptyIds  = {BaseId.INVALID};
			return emptyIds;
		}
		return getObjectIds(selectedObjects);
	}
	
	private BaseId[] getObjectIds(BaseObject[] selectedObjects)
	{
		BaseId[] objectIds = new BaseId[selectedObjects.length];
		for (int i = 0; i < selectedObjects.length; i++)
			objectIds[i] = selectedObjects[i].getId();
		
		return objectIds;
	}

	public int getSelectedObjectType() 
	{
		return getSelectedRef().getObjectType();
	}
	
	public ORef getSelectedRef()
	{
		final BaseObject[] objects = getObjects();
		if(objects == null || objects.length == 0)
			return ORef.INVALID;
		
		return objects[0].getRef();
	}
	
	public BaseObject getSelectedParentFactor()
	{		
		for (int refListIndex = 0; refListIndex < getSelectedHierarchies().length; ++refListIndex)
		{
			ORefList selectedHierarchyRefs =  getSelectedHierarchies()[refListIndex];
			for (int refIndex = 0; refIndex <  selectedHierarchyRefs.size(); ++refIndex)
			{
				ORef factorRef = selectedHierarchyRefs.get(refIndex);
				if (Factor.isFactor(factorRef))
				{
					Factor factor = Factor.findFactor(getProject(), factorRef);
					//FIXME medium: this is a hack,  we need to exclude factors that are not parents
					if (!factor.isStress() && !factor.isActivity())
						return factor;
				}
			}
		}
		
		return null;
	}
		
	public void clearSelection()
	{
		picker.clearSelection();
	}

	private ObjectPicker picker;
}
