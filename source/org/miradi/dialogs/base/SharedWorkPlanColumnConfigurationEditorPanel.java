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

package org.miradi.dialogs.base;

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;
import org.miradi.questions.SharedWorkPlanVisibleRowsQuestion;
import org.miradi.questions.StaticQuestionManager;

public class SharedWorkPlanColumnConfigurationEditorPanel extends AbstractWorkPlanColumnConfigurationEditorPanel
{
	public SharedWorkPlanColumnConfigurationEditorPanel(Project projectToUse, ORef orefToUse)
	{
		super(projectToUse, orefToUse);
		createPanel(orefToUse, StaticQuestionManager.getQuestion(SharedWorkPlanVisibleRowsQuestion.class), true);
	}

	@Override
	protected String getConfigurationLabel()
	{
		return EAM.text("Select which rows and column groups to display.");
	}
}
