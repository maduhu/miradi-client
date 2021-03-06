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
package org.miradi.dialogs.planning.legend;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.main.EAM;
import org.miradi.objects.ViewData;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.utils.UiComboBoxWithSaneActionFiring;

abstract public class PlanningViewComboBox extends UiComboBoxWithSaneActionFiring 
{
	public PlanningViewComboBox(Project projectToUse, ChoiceItem[] choices) throws Exception
	{
		super(choices);
		
		project = projectToUse;
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{		
		try
		{
			saveState();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Error: " + e.getMessage());
		}
	}

	private void saveState() throws Exception
	{	
		Vector<CommandSetObjectData> commands = new Vector<CommandSetObjectData>();
		commands.addAll(getComboSaveCommnds());
		if (commands.size() == 0)
			return;
		
		project.executeCommand(new CommandBeginTransaction());
		try
		{
			getProject().executeCommands(commands.toArray(new Command[0]));
		}
		finally
		{
			project.executeCommand(new CommandEndTransaction());
		}
	}

	private Vector<CommandSetObjectData> getComboSaveCommnds() throws Exception
	{
		if (! comboBoxNeedsSave())
			return new Vector<CommandSetObjectData>();
		
		ChoiceItem selectedItem = (ChoiceItem) getSelectedItem();
		String newValue = selectedItem.getCode();
		Vector<CommandSetObjectData> comboSaveCommands = new Vector<CommandSetObjectData>();
		ViewData viewData = getProject().getCurrentViewData();

		comboSaveCommands.add(new CommandSetObjectData(viewData.getRef(), getChoiceTag(), newValue));
		return comboSaveCommands;
	}
	
	protected Project getProject()
	{
		return project;
	}

	abstract public String getChoiceTag();
	abstract boolean comboBoxNeedsSave() throws Exception;
	
	private Project project;
}
