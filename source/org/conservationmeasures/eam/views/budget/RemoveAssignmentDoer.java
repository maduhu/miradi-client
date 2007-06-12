/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.budget;

import java.util.Arrays;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.TaskId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.CreateAssignmentParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Assignment;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.ObjectsDoer;

public class RemoveAssignmentDoer extends ObjectsDoer
{
	public boolean isAvailable()
	{
		if (getObjects().length == 0 )
			return false;
		
		return true;
	}

	public void doIt() throws CommandFailedException
	{
		if (! isAvailable())
			return;
	
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			Assignment selectedObject = (Assignment)getObjects()[0];
			removeAssignment(getProject(), selectedObject);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}

	public static void removeAssignment(Project project, Assignment assignmentToRemove) throws Exception
	{
		Vector commands = new Vector();
		TaskId taskId = ((CreateAssignmentParameter)assignmentToRemove.getCreationExtraInfo()).getTaskId();
		Task task = (Task)project.findObject(ObjectType.TASK, taskId);
		
		Command removeIdCommand = CommandSetObjectData.createRemoveIdCommand(task, Task.TAG_ASSIGNMENT_IDS, assignmentToRemove.getId());
		commands.add(removeIdCommand);
		
		commands.addAll(Arrays.asList(assignmentToRemove.createCommandsToClear()));
		
		Command deleteCommand = new CommandDeleteObject(ObjectType.ASSIGNMENT, assignmentToRemove.getId());
		commands.add(deleteCommand);
		
		project.executeCommands((Command[])commands.toArray(new Command[0]));
	}
}
