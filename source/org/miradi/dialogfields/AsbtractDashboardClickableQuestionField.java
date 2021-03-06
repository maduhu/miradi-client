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

package org.miradi.dialogfields;


import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.objecthelpers.CodeToChoiceMap;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Dashboard;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;


abstract public class AsbtractDashboardClickableQuestionField extends AbstractDashboardClickableField
{
	public AsbtractDashboardClickableQuestionField(Project projectToUse, ORef refToUse, String stringMapCodeToUse, ChoiceQuestion questionToUse)
	{
		super(projectToUse, refToUse, stringMapCodeToUse);
		
		question = questionToUse;
	}
	
	@Override
	protected void updateLabelComponent(PanelTitleLabel labelComponentToUse, Dashboard dashboard) throws Exception
	{
		CodeToChoiceMap map = getProject().getCachedDashboardEffectiveMap();
		String mapValue = map.getChoiceCode(stringMapCode);
		ChoiceItem progressChoiceItem = question.findChoiceByCode(mapValue);
		if(progressChoiceItem == null)
			throw new RuntimeException("Couldn't find choice " + mapValue + " in question " + question.getClass().getSimpleName());
		updateLabel(labelComponentToUse, progressChoiceItem);
	}
	
	abstract protected void updateLabel(PanelTitleLabel componentToUpdate, ChoiceItem progressChoiceItem);

	private ChoiceQuestion question;
}
