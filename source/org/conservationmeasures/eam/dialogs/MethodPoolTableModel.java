/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;

public class MethodPoolTableModel extends ObjectPoolTableModel
{
	public MethodPoolTableModel(Project projectToUse)
	{
		super(projectToUse, ObjectType.TASK, COLUMN_TAGS);
	}
	
	private static final String[] COLUMN_TAGS = new String[] {
		Task.TAG_LABEL,
		Task.PSEUDO_TAG_INDICATOR_LABEL,
	};
	
	public IdList getLatestIdListFromProject()
	{
		IdList filteredIndicators = new IdList();
		
		IdList indicator = super.getLatestIdListFromProject();
		for (int i=0; i<indicator.size(); ++i)
		{
			BaseId baseId = indicator.get(i);
			Task task = (Task) project.findObject(ObjectType.TASK, baseId);
			if ((task.getOwnerRef().getObjectType() == ObjectType.INDICATOR))
				filteredIndicators.add(baseId);
		}
		return filteredIndicators;
	}


}
