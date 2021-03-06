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
package org.miradi.dialogs.diagram;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ViewData;
import org.miradi.questions.DiagramModeQuestion;
import org.miradi.questions.FontFamiliyQuestion;
import org.miradi.questions.FontSizeQuestion;
import org.miradi.schemas.ProjectMetadataSchema;

public class DiagramProjectPreferencesPanel extends ObjectDataInputPanel
{
	public DiagramProjectPreferencesPanel(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse.getProject(), mainWindowToUse.getProject().getMetadata().getRef());
		
		mainWindow = mainWindowToUse;
		addField(createChoiceField(ProjectMetadataSchema.getObjectType(), ProjectMetadata.TAG_DIAGRAM_FONT_SIZE, new FontSizeQuestion()));
		addField(createChoiceField(ProjectMetadataSchema.getObjectType(), ProjectMetadata.TAG_DIAGRAM_FONT_FAMILY, new FontFamiliyQuestion()));
		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return null;
	}
	
	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		
		try
		{
			if(!event.isSetDataCommandWithThisType(ProjectMetadataSchema.getObjectType()))
				return;
			
			CommandSetObjectData command = (CommandSetObjectData)event.getCommand();
			CommandSetObjectData changeToDefaultMode = new CommandSetObjectData(getProject().getCurrentViewData().getRef(), ViewData.TAG_CURRENT_MODE, DiagramModeQuestion.MODE_DEFAULT);
			getProject().executeAsSideEffect(changeToDefaultMode);
			
			if (isOneOfOurFields(command.getFieldTag()))
				mainWindow.getCurrentView().refresh();
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}
	
	private MainWindow mainWindow;
}
