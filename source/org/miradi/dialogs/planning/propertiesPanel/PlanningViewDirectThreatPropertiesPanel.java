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
package org.miradi.dialogs.planning.propertiesPanel;

import org.miradi.icons.DirectThreatIcon;
import org.miradi.main.EAM;
import org.miradi.objects.Cause;
import org.miradi.project.Project;
import org.miradi.questions.ThreatClassificationQuestion;
import org.miradi.schemas.CauseSchema;

public class PlanningViewDirectThreatPropertiesPanel extends MinimalFactorPropertiesPanel
{
	public PlanningViewDirectThreatPropertiesPanel(Project projectToUse) throws Exception
	{
		super(projectToUse, CauseSchema.getObjectType());
		
		createAndAddFields(EAM.text("Threat"), new DirectThreatIcon());
	}
	
	@Override
	protected void addCustomInBetweenFields()
	{
		super.addCustomInBetweenFields();
		
		addField(createRadioButtonEditorField(CauseSchema.getObjectType(), Cause.TAG_TAXONOMY_CODE, new ThreatClassificationQuestion()));
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Direct Threat Properties");
	}

}
