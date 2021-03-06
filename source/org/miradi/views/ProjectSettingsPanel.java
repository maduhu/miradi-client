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

package org.miradi.views;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.FactorModeQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.TargetModeQuestion;
import org.miradi.schemas.ProjectMetadataSchema;

public class ProjectSettingsPanel extends ObjectDataInputPanel
{
	public ProjectSettingsPanel(Project projectToUse)
	{
		super(projectToUse, projectToUse.getMetadata().getRef());
		
		final ChoiceQuestion targetModeQuestion = StaticQuestionManager.getQuestion(TargetModeQuestion.class);
		addField(createChoiceField(ProjectMetadataSchema.getObjectType(), ProjectMetadata.TAG_HUMAN_WELFARE_TARGET_MODE, targetModeQuestion));
		
		final ChoiceQuestion factorModeQuestion = StaticQuestionManager.getQuestion(FactorModeQuestion.class);
		addField(createChoiceField(ProjectMetadataSchema.getObjectType(), ProjectMetadata.TAG_BIOPHYSICAL_FACTOR_MODE, factorModeQuestion));

		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Project Settings");
	}
}
