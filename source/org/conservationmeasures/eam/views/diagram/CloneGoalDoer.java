/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.dialogs.AnnotationSelectionList;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.objects.BaseObject;

public class CloneGoalDoer extends CreateGoal
{
	public void doIt() throws CommandFailedException
	{
		if (!isAvailable())
			return;
		
		AnnotationSelectionList list = displayAnnotationList("Goal Selection List", getAnnotationType());	
		objectToClone = list.getSelectedAnnotaton();
		
		if (objectToClone == null)
			return;
		
		super.doIt();
	}
	
	protected CommandCreateObject createObject() throws CommandFailedException
	{
		return cloneObject(objectToClone);
	}
	
	BaseObject objectToClone;
}
