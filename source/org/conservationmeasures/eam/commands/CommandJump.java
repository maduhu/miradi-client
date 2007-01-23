/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.commands;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.project.Project;

public class CommandJump extends Command
{
	public CommandJump(int destinationStep)
	{
		step = destinationStep;
		previousStep = -1;
	}

	public String getCommandName()
	{
		return COMMAND_NAME;
	}

	public void execute(Project target) throws CommandFailedException
	{
	}

	public void undo(Project target) throws CommandFailedException
	{
	}
	
	public Command getReverseCommand() throws CommandFailedException
	{
		return new CommandJump(previousStep);
	}

	public static final String COMMAND_NAME = "Jump";
	
	int step;
	int previousStep;
}
