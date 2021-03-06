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
package org.miradi.dialogs.goal;

import org.miradi.actions.ActionEditGoalIndicatorRelevancyList;
import org.miradi.actions.ActionEditGoalStrategyActivityRelevancyList;
import org.miradi.actions.Actions;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanelWithSections;
import org.miradi.dialogs.progressPercent.ProgressPercentSubPanel;
import org.miradi.icons.GoalIcon;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Desire;
import org.miradi.objects.Goal;
import org.miradi.project.Project;
import org.miradi.schemas.GoalSchema;

public class GoalPropertiesPanel extends ObjectDataInputPanelWithSections
{
	public GoalPropertiesPanel(Project projectToUse, Actions actionsToUse) throws Exception
	{
		super(projectToUse, ObjectType.GOAL);
		
		createSingleSection(EAM.text("Goal"));
		ObjectDataInputField shortLabelField = createShortStringField(GoalSchema.getObjectType(), Goal.TAG_SHORT_LABEL);
		ObjectDataInputField labelField = createExpandableField(GoalSchema.getObjectType(), Goal.TAG_LABEL);
		addFieldsOnOneLine(EAM.text("Goal"), new GoalIcon(), new ObjectDataInputField[]{shortLabelField, labelField,});
		addField(createMultilineField(GoalSchema.getObjectType(), Desire.TAG_FULL_TEXT));
		addField(createReadonlyTextField(Goal.PSEUDO_TAG_FACTOR));
		addField(createReadonlyTextField(Goal.PSEUDO_TAG_DIRECT_THREATS));
		addFieldWithEditButton(EAM.text("Indicators"), createReadOnlyObjectList(GoalSchema.getObjectType(), Goal.PSEUDO_TAG_RELEVANT_INDICATOR_REFS), createObjectsActionButton(actionsToUse.getObjectsAction(ActionEditGoalIndicatorRelevancyList.class), getPicker()));
		addFieldWithEditButton(EAM.text("Strategies And Activities"), createReadOnlyObjectList(GoalSchema.getObjectType(), Goal.PSEUDO_TAG_RELEVANT_STRATEGY_ACTIVITY_REFS), createObjectsActionButton(actionsToUse.getObjectsAction(ActionEditGoalStrategyActivityRelevancyList.class), getPicker()));
		addSubPanelWithTitledBorder(new ProgressPercentSubPanel(getProject()));
		addTaxonomyFields(GoalSchema.getObjectType());
		addField(createMultilineField(Goal.TAG_COMMENTS));
		
		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Goal Properties");
	}
}
