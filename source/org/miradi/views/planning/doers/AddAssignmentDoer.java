/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.planning.doers;

import org.miradi.commands.Command;
import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Task;
import org.miradi.views.ObjectsDoer;

public class AddAssignmentDoer extends ObjectsDoer
{
	public boolean isAvailable()
	{
		if (getObjects().length != 1)
			return false;
		
		if (getSelectedObjectType() != ObjectType.TASK)
			return false;
		
		return true;
	}
	
	public void doIt() throws CommandFailedException
	{
		if (! isAvailable())
			return;

		try 
		{
			Task selectedTask = (Task)getProject().findObject(ObjectType.TASK, getSelectedIds()[0]);
			if (selectedTask.getSubtaskCount() > 0)
			{
				EAM.errorDialog(EAM.text("Resources cannot be added to this task because it already has subtasks."));
				return;
			}
			
			createAssignment(selectedTask);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	private void createAssignment(Task selectedTask) throws Exception
	{
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			CommandCreateObject createAssignment = new CommandCreateObject(ObjectType.ASSIGNMENT);
			getProject().executeCommand(createAssignment);

			Command appendAssignment = CommandSetObjectData.createAppendIdCommand(selectedTask, Task.TAG_ASSIGNMENT_IDS, createAssignment.getCreatedId());
			getProject().executeCommand(appendAssignment);
		}
		finally 
		{
			getProject().executeCommand(new CommandEndTransaction());	
		}
	}
	
}
