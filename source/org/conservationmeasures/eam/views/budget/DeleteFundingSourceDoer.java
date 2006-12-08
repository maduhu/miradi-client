/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.budget;

import java.util.Vector;

import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.FundingSource;
import org.conservationmeasures.eam.views.ObjectsDoer;

public class DeleteFundingSourceDoer extends ObjectsDoer
{
	public boolean isAvailable()
	{
		return (getObjects().length == 1);
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		FundingSource resource = (FundingSource)getObjects()[0];
		BaseId idToRemove = resource.getId();
		
		Vector dialogText = new Vector();
		
		dialogText.add("\nAre you sure you want to delete this funding source?");

		String[] buttons = {"Yes", "No", };
		if(!EAM.confirmDialog("Delete Funding Source", (String[])dialogText.toArray(new String[0]), buttons))
			return;

		try
		{
			getProject().executeCommand(new CommandBeginTransaction());
			try
			{
				int type = resource.getType();
				BaseId id = idToRemove;
				getProject().executeCommands(resource.createCommandsToClear());
				getProject().executeCommand(new CommandDeleteObject(type, id));
			}
			finally
			{
				getProject().executeCommand(new CommandEndTransaction());
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

}

