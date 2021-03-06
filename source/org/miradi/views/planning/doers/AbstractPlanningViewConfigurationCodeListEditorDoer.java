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
package org.miradi.views.planning.doers;

import org.miradi.dialogs.base.CodeListEditorPanel;
import org.miradi.dialogs.base.ModelessDialogWithClose;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.ViewData;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.views.ObjectsDoer;

abstract public class AbstractPlanningViewConfigurationCodeListEditorDoer extends ObjectsDoer
{
	@Override
	public boolean isAvailable()
	{
		return true;
	}
	
	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		try
		{
			ORef currentViewDataRef = getProject().getCurrentViewData().getRef();
			ViewData viewData = ViewData.find(getProject(), currentViewDataRef);
			ORef planningConfigurationRef = viewData.getORef(ViewData.TAG_TREE_CONFIGURATION_REF);

			ChoiceQuestion configurationQuestion = getConfigurationQuestion();
			ObjectDataInputPanel codeListPanel = createCodeListPanel(planningConfigurationRef, configurationQuestion);
			ModelessDialogWithClose dialog = new ModelessDialogWithClose(EAM.getMainWindow(), EAM.text("Selection Dialog"));
			dialog.setScrollableMainPanel(codeListPanel);
			getView().showFloatingPropertiesDialog(dialog);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}

	protected CodeListEditorPanel createCodeListPanel(ORef planningConfigurationRef, ChoiceQuestion configurationQuestion)
	{
		return new CodeListEditorPanel(getProject(), planningConfigurationRef, getConfigurationTag(), configurationQuestion, getGridColumnCount());
	}

	abstract protected int getGridColumnCount();

	abstract protected String getConfigurationTag();

	abstract protected ChoiceQuestion getConfigurationQuestion();
}
