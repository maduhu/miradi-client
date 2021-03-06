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
package org.miradi.views.umbrella;

import org.miradi.commands.Command;
import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.project.Project;
import org.miradi.utils.CommandVector;
import org.miradi.views.ObjectsDoer;

import java.util.Vector;

public class DeleteResourceDoer extends ObjectsDoer
{
	@Override
	public boolean isAvailable()
	{
		if(!super.isAvailable())
			return false;
		
		BaseObject singleSelectedObject = getSingleSelectedObject();
		if (singleSelectedObject == null)
			return false;
		
		return (ProjectResource.is(singleSelectedObject));
	}

	@Override
	protected void doIt() throws Exception
	{
		if(!isAvailable())
			return;
		
		ProjectResource resource = (ProjectResource)getObjects()[0];
		Vector<String> dialogText = new Vector<String>();
		ORefList allThatUseThisResource = resource.findAllObjectsThatReferToUs();

		if (allThatUseThisResource.size() > 0)
			dialogText.add(EAM.text("This resource has been planned for work in the Strategic Plan and / or assigned work in the Work Plan"));
		
		dialogText.add(EAM.text("\nAre you sure you want to delete this resource?"));
		String[] buttons = {EAM.text("Yes"), EAM.text("No"), };
		if(!EAM.confirmDialog(EAM.text("Delete Resource"), dialogText.toArray(new String[0]), buttons))
			return;
		
		try
		{
			Project project = getProject();
			project.executeCommand(new CommandBeginTransaction());
			try
			{
				project.executeCommands(createCommandsToRemoveFromReferrers(allThatUseThisResource));
				project.executeCommands(resource.createCommandsToDeleteChildrenAndObject());
			}
			finally
			{
				project.executeCommand(new CommandEndTransaction());
			}
		}
		catch(CommandFailedException e)
		{
			throw(e);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}

	private Command[] createCommandsToRemoveFromReferrers(ORefList allThatUseThisResource) throws Exception
	{
		CommandVector commands = new CommandVector();
		for (int i = 0; i < allThatUseThisResource.size(); ++i)
		{
			ORef referrerRef = allThatUseThisResource.get(i);
			commands.addAll(removeFromReferrer(referrerRef));
		}
		
		return commands.toArray(new Command[0]);
	}

	private CommandVector removeFromReferrer(ORef ref)
	{
		CommandVector commands = new CommandVector();
		if (ResourceAssignment.is(ref))
			commands.add(new CommandSetObjectData(ref, ResourceAssignment.TAG_RESOURCE_ID, BaseId.INVALID.toString()));

		return commands;
	}
}
